package br.com.sysmap.bootcamp.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String getAuthenticatedUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal().toString();
    }
}
