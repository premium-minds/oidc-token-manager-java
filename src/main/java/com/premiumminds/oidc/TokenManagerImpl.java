package com.premiumminds.oidc;

public class TokenManagerImpl<T, R> implements TokenManager<T> {
    private Tokens<T, R> tokens;

    private long validity;

    private int expireThreshold;

    private OpenIDProvider<T, R> provider;

    public TokenManagerImpl(OpenIDProvider<T, R> provider, int expireThreshold) {
        this.expireThreshold = expireThreshold;
        this.provider = provider;
        this.validity = -1;
    }

    @Override
    public T getAccessToken() {
        if (tokens == null || expired()) {
            fetchNewToken();
        }
        return tokens.getAccessToken();
    }

    private synchronized void fetchNewToken() {
        if (tokens != null && tokens.getRefreshToken() != null) {
            refreshToken();
        } else {
            grantToken();
        }
    }

    public boolean expired() {
        return validity < System.currentTimeMillis();
    }

    private void refreshToken() {
        tokens = provider.refreshToken(tokens.getRefreshToken());
        calculateValidity();
    }

    private void grantToken() {
        tokens = provider.grantToken();
        calculateValidity();
    }

    private void calculateValidity() {
        if (tokens.getExpiresIn() >= 0) {
            this.validity = System.currentTimeMillis() + tokens.getExpiresIn() - expireThreshold;
        } else {
            this.validity = Long.MAX_VALUE;
        }
    }
}
