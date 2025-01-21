package com.edara.edara.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Fetch the Authentication object from the SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Handle cases where the context is not set or the user is not authenticated
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.of("system"); // Default auditor for non-authenticated context
        }

        return Optional.ofNullable(auth.getName());
    }


}
