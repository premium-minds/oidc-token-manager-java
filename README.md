# OIDC Client Token Manager for Java

This library handles the server 2 server tokens from and OIDC provider.

## Maven project
![Maven Central](https://img.shields.io/maven-central/v/com.premiumminds.oidc/oidc-token-manager)

Add the following maven dependency to your project `pom.xml`:

```xml
<dependency>
   <groupId>com.premiumminds.oidc</groupId>
   <artifactId>oidc-token-manager</artifactId>
   <version>1.0</version>
</dependency>
```
Check out [sonatype repository](https://oss.sonatype.org/index.html#nexus-search;quick~oidc-token-manager) for latest snapshots and releases.

## Example usage

Include the [Nimbus OAuth 2.0 SDK with OpenID Connect extensions](https://search.maven.org/search?q=a:oauth2-oidc-sdk) to your maven project.

    TokenManager<BearerAccessToken> tokenManager = 
        new NimbusOIDCTokenManagerBuilder(new URI("http://provider/token"), "client_id")
            .clientSecret("client_secret").build();
    BearerAccessToken token = tokenManager.getAccessToken();

## Continuous Integration

[![Build Status](https://travis-ci.org/premium-minds/oidc-token-manager-java.png?branch=master)](https://travis-ci.org/premium-minds/oidc-token-manager-java)

CI is hosted by [travis-ci.org](https://travis-ci.org/)

## Licence

Copyright (C) 2020 [Premium Minds](https://www.premium-minds.com/)

Licensed under the [GNU Lesser General Public Licence](https://www.gnu.org/licenses/lgpl.html)