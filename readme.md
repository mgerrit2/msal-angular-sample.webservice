# Project for testing Microsoft Entra ID

## Implementation of Microsoft Entra ID

```java

```


## Controller endpoint security implementation

### The annotation implementation
```java
package com.msal.angular.msal_angular_sample.webservice.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
// Calls the 'dbSecurity' bean we defined above and passes the current authentication
@PreAuthorize("@dbSecurity.isAdmin(authentication)")
public @interface AdminOnly {
}
```

### The security component add to the annotation implementation
```java
package com.msal.angular.msal_angular_sample.webservice.annotation;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("dbSecurity")
public class DatabaseSecurityEvaluator {

    // Inject your database repository here when ready
    // private final UserRepository userRepository;

    public boolean isAdmin(Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {

            // Extract the 'oid' directly from the claims map seen in your debugger
            String oid = jwt.getClaimAsString("oid");
            String email = jwt.getClaimAsString("preferred_username");
            String name = jwt.getClaimAsString("name");

            // Build a log message using StringBuilder
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Evaluating Admin Access -> ")
                    .append("OID: ").append(oid)
                    .append(", Email: ").append(email)
                    .append(", Name: ").append(name);

            // Print out the details for debugging
            System.out.println(stringBuilder.toString());

            // Query database to check if this user's OID has admin privileges
            // return userRepository.existsByOidAndRole(oid, "ADMIN");
        }

        return true; // placeholder logic
    }
}
```

### Enable the methode security
```java
package com.msal.angular.msal_angular_sample.webservice.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class MethodSecurityConfig {
}

```

## swagger url
http://localhost:8080/swagger-ui/index.html