package com.premiumminds.oidc;

public interface OpenIDProvider<T, R> {
    /**
     * This method should retrieve the tokens (access token, refresh token and expire time) from an OpenID Provider
     * using the refresh token.
     *
     * @param refreshToken refresh token
     * @return Tokens values
     */
    Tokens<T, R> refreshToken(R refreshToken);

    /**
     * This method should retrieve the tokens (access token, refresh token and expire time) from an OpenID Provider.
     * This can use whatever authentication request you want
     *
     * @return Tokens values
     */
    Tokens<T, R> grantToken();
}
