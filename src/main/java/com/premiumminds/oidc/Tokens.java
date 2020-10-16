package com.premiumminds.oidc;

/**
 * Tokens holder
 *
 * @param <T>
 *         token type
 * @param <R>
 *         refresh token type
 */
public class Tokens<T, R> {
    private T accessToken;

    private R refreshToken;

    private long expiresIn;

    /**
     * Create new tokens DTO
     *
     * @param accessToken
     *         granted access token
     * @param refreshToken
     *         refresh token
     * @param expiresIn
     *         number of milliseconds this access token is valid
     */
    public Tokens(T accessToken, R refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    /**
     * Create new tokens DTO. This access token never expires.
     *
     * @param accessToken
     *         granted access token
     * @param refreshToken
     *         refresh token
     */
    public Tokens(T accessToken, R refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = -1;
    }

    /**
     * Create new tokens DTO
     *
     * @param accessToken
     *         granted access token
     * @param expiresIn
     *         number of milliseconds this access token is valid
     */
    public Tokens(T accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    /**
     * Create new tokens DTO. This token never expires.
     *
     * @param accessToken
     *         granted access token
     */
    public Tokens(T accessToken) {
        this.accessToken = accessToken;
        this.expiresIn = -1;
    }

    /**
     * Get access token
     *
     * @return access token
     */
    public T getAccessToken() {
        return accessToken;
    }

    /**
     * Get refresh token
     *
     * @return refresh token
     */
    public R getRefreshToken() {
        return refreshToken;
    }

    /**
     * Get number of milliseconds this access token is valid. If access token never
     * expires, then this should return -1.
     *
     * @return number of milliseconds this access token is valid. -1 if never expires.
     */
    public long getExpiresIn() {
        return expiresIn;
    }

}
