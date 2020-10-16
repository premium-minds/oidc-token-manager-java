package com.premiumminds.oidc;

/**
 * Class to handle token management of an OpenID Provider
 * <p>
 * Example:
 * <pre>
 *     TokenManager tokenManager = new NimbusOIDCTokenManagerBuilder(oidcMetadata, "client_id")
 *          .clientSecret("client_secret").build();
 *     BearerAccessToken token = tokenManager.getAccessToken();
 * </pre>
 *
 * @param <T>
 *         token type
 */
public interface TokenManager<T> {
    /**
     * Retrieve access token. If no access token in cache or expired access token, it will try to retrieve it.
     * <p>
     * This method can have blocking IO.
     *
     * @return access token
     */
    T getAccessToken();
}
