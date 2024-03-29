package com.premiumminds.oidc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenManagerImplTest {
    @Test
    public void testGrantToken() {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", 1000); // 1 second
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
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", null,  1000); // 1 second
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        tokenManager.getAccessToken();
        provider.reset();
        tokenManager.getAccessToken();
        Assertions.assertFalse(provider.refreshTokenCalled);

        Thread.sleep(1001); // 1 seconds and 1 millisecond

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertTrue(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);
    }

    @Test
    public void testValidityExpiredWithRefreshToken() throws InterruptedException {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", 1000); // 1 second
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        tokenManager.getAccessToken();
        provider.reset();
        tokenManager.getAccessToken();
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);

        Thread.sleep(1001); // 1 seconds and 1 millisecond

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertTrue(provider.refreshTokenCalled);
    }

    @Test
    public void testValidityExpiredWithExpireThreshold() throws InterruptedException {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", 6000); // 6 second
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 5000); // 5 seconds

        tokenManager.getAccessToken();
        provider.reset();
        tokenManager.getAccessToken();
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);

        Thread.sleep(1001); // 1 seconds and 1 millisecond

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertTrue(provider.refreshTokenCalled);
    }

    @Test
    public void testValidityExpiredWithRefreshTokenButInvalid() throws InterruptedException {
        OpenIDProviderTest provider = new OpenIDProviderTest("accessToken", "refreshToken", 1000); // 1 seconds
        TokenManager<String> tokenManager = new TokenManagerImpl<>(provider, 0);

        tokenManager.getAccessToken();
        provider.reset();
        provider.failRefreshToken = true;
        tokenManager.getAccessToken();
        Assertions.assertFalse(provider.grantTokenCalled);
        Assertions.assertFalse(provider.refreshTokenCalled);

        Thread.sleep(1001); // 1 seconds and 1 millisecond

        String accessToken = tokenManager.getAccessToken();

        Assertions.assertEquals("accessToken", accessToken);
        Assertions.assertTrue(provider.grantTokenCalled);
        Assertions.assertTrue(provider.refreshTokenCalled);
    }

    public static class OpenIDProviderTest implements OpenIDProvider<String, String> {
        private boolean refreshTokenCalled;

        private boolean grantTokenCalled;

        private boolean failRefreshToken = false;

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
            if (failRefreshToken) {
                throw new RuntimeException("failing refresh token");
            }
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
            this.failRefreshToken = false;
        }
    }
}
