package com.premiumminds.oidc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenManagerImplTest {
    @Test
    public void testGrantToken() {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", 100);
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertTrue(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);
    }

    @Test
    public void testNeverExpires() {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", -1);
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        String accessToken = tokenManager.getAccessToken();
        provider.reset();
        tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);
    }

    @Test
    public void testValidityExpiredWithoutRefreshToken() throws InterruptedException {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", null, 100);
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        tokenManager.getAccessToken();
        provider.reset();
        tokenManager.getAccessToken();
        Assertions.assertFalse(provider.refreshTokenCalled);

        Thread.sleep(101);

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertTrue(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);
    }

    @Test
    public void testValidityExpiredWithRefreshToken() throws InterruptedException {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", 100);
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        tokenManager.getAccessToken();
        provider.reset();
        tokenManager.getAccessToken();
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);

        Thread.sleep(101);

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertTrue(provider.refreshTokenCalled);
    }

    public static class OpenIDProviderTest implements OpenIDProvider<String, String> {
        private boolean refreshTokenCalled;
        private boolean grantTokenCalled;

        private String accessToken;
        private String refreshToken;
        private int expiresIn;

        public OpenIDProviderTest(String accessToken, String refreshToken, int expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }

        @Override
        public Tokens refreshToken(String refreshToken) {
            this.refreshTokenCalled = true;
            return new Tokens(accessToken, this.refreshToken, expiresIn);
        }

        @Override
        public Tokens grantToken() {
            this.grantTokenCalled = true;
            return new Tokens(accessToken, refreshToken, expiresIn);
        }

        private void reset() {
            this.refreshTokenCalled = false;
            this.grantTokenCalled = false;
        }
    }
}
