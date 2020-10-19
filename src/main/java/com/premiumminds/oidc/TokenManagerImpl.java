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
        try {
            tokens = provider.refreshToken(tokens.getRefreshToken());
            calculateValidity();
        } catch (Exception e) {
            // something went wrong, let's try the grant method
            grantToken();
        }
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
