package com.premiumminds.oidc.nimbus;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.premiumminds.oidc.TokenManager;
import com.premiumminds.oidc.TokenManagerImpl;
import java.net.URI;

/**
 * Token manager using the Nimbus OAuth 2.0 SDK with OpenID Connect extensions.
 * <p>
 * To use this class, the project must include:
 * <pre>
 *      &lt;dependency&gt;
 *          &lt;groupId&gt;com.nimbusds&lt;/groupId&gt;
 *          &lt;artifactId&gt;oauth2-oidc-sdk&lt;/artifactId&gt;
 *      &lt;/dependency&gt;
 * </pre>
 */
public class NimbusOIDCTokenManagerBuilder {
    private final URI providerTokenEndpoint;

    private final ClientID clientID;

    private Secret clientSecret;

    private AuthorizationGrant authorizationGrant = new ClientCredentialsGrant();

    private Scope scope = new Scope();

    private int connectTimeout = 5000; // default 5 seconds

    private int readTimeout = 5000; // default 5 seconds

    private int expireThreshold = 5000; // default 5 seconds

    /**
     * Create a new builder
     *
     * @param providerEndpointMetadata
     *         endpoint metadata
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(AuthorizationServerEndpointMetadata providerEndpointMetadata,
            ClientID clientID) {
        this.providerTokenEndpoint = providerEndpointMetadata.getTokenEndpointURI();
        this.clientID = clientID;
    }

    /**
     * Create a new builder
     *
     * @param metadata
     *         OIDC provider metadata
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(OIDCProviderMetadata metadata, ClientID clientID) {
        this.providerTokenEndpoint = metadata.getTokenEndpointURI();
        this.clientID = clientID;
    }

    /**
     * Create a new builder
     *
     * @param tokenEndpoint
     *         token endpoint
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(URI tokenEndpoint, ClientID clientID) {
        this.providerTokenEndpoint = tokenEndpoint;
        this.clientID = clientID;
    }

    /**
     * Create a new builder
     *
     * @param providerEndpointMetadata
     *         endpoint metadata
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(AuthorizationServerEndpointMetadata providerEndpointMetadata,
            String clientID) {
        this.providerTokenEndpoint = providerEndpointMetadata.getTokenEndpointURI();
        this.clientID = new ClientID(clientID);
    }

    /**
     * Create a new builder
     *
     * @param metadata
     *         OIDC provider metadata
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(OIDCProviderMetadata metadata, String clientID) {
        this.providerTokenEndpoint = metadata.getTokenEndpointURI();
        this.clientID = new ClientID(clientID);
    }

    /**
     * Create a new builder
     *
     * @param tokenEndpoint
     *         token endpoint
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(URI tokenEndpoint, String clientID) {
        this.providerTokenEndpoint = tokenEndpoint;
        this.clientID = new ClientID(clientID);
    }

    /**
     * If client is not public, specify the client secret
     *
     * @param secret
     *         client secret
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder clientSecret(String secret) {
        this.clientSecret = new Secret(secret);
        return this;
    }

    /**
     * If client is not public, specify the client secret
     *
     * @param secret
     *         client secret
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder clientSecret(Secret secret) {
        this.clientSecret = secret;
        return this;
    }

    /**
     * Specify an authorization grant.
     * <p>
     * Default: client credentials
     *
     * @param authorizationGrant
     *         authorization grant
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder authorizationGrant(AuthorizationGrant authorizationGrant) {
        this.authorizationGrant = authorizationGrant;
        return this;
    }

    /**
     * Use resource owner password credentials grant
     *
     * @param username
     *         username
     * @param password
     *         password
     * @return this builder
     */
    public NimbusOIDCTokenManagerBuilder authentication(String username, String password) {
        this.authorizationGrant = new ResourceOwnerPasswordCredentialsGrant(username, new Secret(password));
        return this;
    }

    /**
     * Specify the scope for the access token
     * <p>
     * Default: empty scope
     *
     * @param scope
     *         scope
     * @return this builder
     */
    public NimbusOIDCTokenManagerBuilder scope(Scope scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Set the connect timeout in milliseconds. If the value is 0, it will wait
     * infinitely.
     * <p>
     * Default: 30 seconds
     *
     * @param connectTimeout
     *         timeout in milliseconds
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Set the read timeout in milliseconds. If the value is 0, it will wait
     * infinitely.
     * <p>
     * Default: 30 seconds
     *
     * @param readTimeout
     *         timeout in milliseconds
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Set the expiring threshold used to calculate if the token is expired.
     * <p>
     * Default: 5 seconds
     *
     * @param expireThreshold
     *         value in milliseconds
     * @return
     */
    public NimbusOIDCTokenManagerBuilder expireThreshold(int expireThreshold) {
        this.expireThreshold = expireThreshold;
        return this;
    }

    /**
     * Build a new TokenManager
     *
     * @return the token manager
     */
    public TokenManager<BearerAccessToken> build() {
        OpenIDProviderImpl provider =
                new OpenIDProviderImpl(providerTokenEndpoint, clientID, clientSecret, authorizationGrant, scope,
                        connectTimeout, readTimeout);

        return new TokenManagerImpl<>(provider, expireThreshold);
    }
}
