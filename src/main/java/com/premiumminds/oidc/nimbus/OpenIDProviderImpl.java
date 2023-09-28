package com.premiumminds.oidc.nimbus;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.premiumminds.oidc.OpenIDProvider;
import com.premiumminds.oidc.Tokens;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

class OpenIDProviderImpl implements OpenIDProvider<BearerAccessToken, RefreshToken> {
    private final Supplier<URI> tokenEndpoint;

    private final ClientID clientID;

    private final Secret clientSecret;

    private final AuthorizationGrant authorizationGrant;

    private final Scope scope;

    private final int connectTimeout;

    private final int readTimeout;

    private final Map<String, List<String>> headers;

    public OpenIDProviderImpl(Supplier<URI> tokenEndpoint, ClientID clientID, Secret clientSecret,
            AuthorizationGrant authorizationGrant, Scope scope, int connectTimeout, int readTimeout, Map<String, List<String>> headers) {
        this.tokenEndpoint = tokenEndpoint;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.authorizationGrant = authorizationGrant;
        this.scope = scope;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.headers = headers;
    }

    @Override
    public Tokens<BearerAccessToken, RefreshToken> refreshToken(RefreshToken refreshToken) {
        return tokenRequest(new RefreshTokenGrant(refreshToken));
    }

    @Override
    public Tokens<BearerAccessToken, RefreshToken> grantToken() {
        return tokenRequest(authorizationGrant);
    }

    private Tokens<BearerAccessToken, RefreshToken> tokenRequest(AuthorizationGrant authzGrant) {
        TokenRequest request;
        if (clientSecret != null) {
            request = new TokenRequest(tokenEndpoint.get(), new ClientSecretBasic(clientID, clientSecret), authzGrant,
                    scope);
        } else {
            request = new TokenRequest(tokenEndpoint.get(), clientID, authzGrant, scope);
        }

        HTTPRequest httpRequest = request.toHTTPRequest();
        httpRequest.setConnectTimeout(connectTimeout);
        httpRequest.setReadTimeout(readTimeout);

        if (headers != null) {
            headers.forEach((key, value) -> httpRequest.setHeader(key, value.toArray(new String[0])));
        }

        HTTPResponse httpResponse;
        try {
            httpResponse = httpRequest.send();
        } catch (IOException e) {
            throw new OIDCProviderRequestException("connection problem", e);
        }
        TokenResponse tokenResponse;
        try {
            tokenResponse = OIDCTokenResponseParser.parse(httpResponse);
        } catch (ParseException e) {
            throw new OIDCProviderRequestException("response parse problem", e);
        }

        if (tokenResponse instanceof TokenErrorResponse) {
            ErrorObject error = ((TokenErrorResponse) tokenResponse).getErrorObject();
            throw new OIDCProviderRequestException(
                    "provider error - " + error.getCode() + ": " + error.getDescription());
        }

        OIDCTokenResponse oidcTokenResponse = (OIDCTokenResponse) tokenResponse;
        com.nimbusds.oauth2.sdk.token.Tokens tokens = oidcTokenResponse.getTokens();

        return new Tokens<>(tokens.getBearerAccessToken(), tokens.getRefreshToken(),
                tokens.getBearerAccessToken().getLifetime() * 1000);

    }
}
