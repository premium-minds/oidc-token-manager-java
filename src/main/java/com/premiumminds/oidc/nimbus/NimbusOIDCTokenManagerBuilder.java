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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
    private final Supplier<URI> providerTokenEndpoint;

    private final ClientID clientID;

    private Secret clientSecret;

    private AuthorizationGrant authorizationGrant = new ClientCredentialsGrant();

    private Scope scope = new Scope();

    private int connectTimeout = 5000; // default 5 seconds

    private int readTimeout = 5000; // default 5 seconds

    private int expireThreshold = 5000; // default 5 seconds

    private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
        this.providerTokenEndpoint = () -> providerEndpointMetadata.getTokenEndpointURI();
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
        this.providerTokenEndpoint = () -> metadata.getTokenEndpointURI();
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
        this.providerTokenEndpoint = () -> tokenEndpoint;
        this.clientID = clientID;
    }

    /**
     * Create a new builder
     *
     * @param tokenEndpoint
     *         token endpoint supplier
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(Supplier<URI> tokenEndpoint, ClientID clientID) {
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
        this.providerTokenEndpoint = () -> providerEndpointMetadata.getTokenEndpointURI();
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
        this.providerTokenEndpoint = () -> metadata.getTokenEndpointURI();
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
        this.providerTokenEndpoint = () -> tokenEndpoint;
        this.clientID = new ClientID(clientID);
    }

    /**
     * Create a new builder
     *
     * @param tokenEndpoint
     *         token endpoint supplier
     * @param clientID
     *         client id
     */
    public NimbusOIDCTokenManagerBuilder(Supplier<URI> tokenEndpoint, String clientID) {
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
     * @return the builder
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
     * Set the connect timeout in milliseconds. If the value is 0 it means no timeout.
     * <p>
     * Default: 5 seconds
     *
     * @param timeout
     *         timeout in milliseconds
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder connectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * Set the connect timeout. If the value is 0 it means no timeout.
     * <p>
     * Default: 5 seconds
     *
     * @param timeout
     *         timeout value
     * @param unit
     *         timeout unit
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder connectTimeout(int timeout, TimeUnit unit) {
        this.connectTimeout = (int) unit.toMillis(timeout);
        return this;
    }

    /**
     * Set the read timeout in milliseconds. If the value is 0 it means no timeout.
     * <p>
     * Default: 5 seconds
     *
     * @param timeout
     *         timeout in milliseconds
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder readTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    /**
     * Set the read timeout. If the value is 0 it means no timeout.
     * <p>
     * Default: 5 seconds
     *
     * @param timeout
     *         timeout value
     * @param unit
     *         timeout unit
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder readTimeout(int timeout, TimeUnit unit) {
        this.readTimeout = (int) unit.toMillis(timeout);
        return this;
    }

    /**
     * Set the expiring threshold used to calculate if the token is expired.
     * <p>
     * Default: 5 seconds
     *
     * @param expireThreshold
     *         value in milliseconds
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder expireThreshold(int expireThreshold) {
        this.expireThreshold = expireThreshold;
        return this;
    }

    /**
     * Set headers for the request to the token endpoint.
     * <p>
     * Default: empty map
     *
     * @param headers
     *         map of header (name: values)
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder headers(Map<String, List<String>> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Add header for the request to the token endpoint.
     * This will append the pair (name:value) to the existing headers
     *
     * @param name
     *         the header name
     *
     * @param value
     *         the header value
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder header(String name, String value) {
        this.headers.put(name, Collections.singletonList(value));
        return this;
    }

    /**
     * Add header for the request to the token endpoint.
     * This will append the pair (name:values) to the existing headers
     *
     * @param name
     *         the header name
     *
     * @param values
     *         a list of header values
     * @return the builder
     */
    public NimbusOIDCTokenManagerBuilder header(String name, List<String> values) {
        this.headers.put(name, values);
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
                        connectTimeout, readTimeout, headers);

        return new TokenManagerImpl<>(provider, expireThreshold);
    }
}
