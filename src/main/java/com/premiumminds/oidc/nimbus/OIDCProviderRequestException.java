package com.premiumminds.oidc.nimbus;

public class OIDCProviderRequestException extends RuntimeException {

    public OIDCProviderRequestException(String message) {
        super(message);
    }

    public OIDCProviderRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
