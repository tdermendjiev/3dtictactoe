package com.tictactoe.config;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TokenProcessingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)) {
            throw new RuntimeException("Not a HTTP request");
        }

        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

        if (context == null) {
            handleNoSecurityContext(request, response, chain);
            return;
        }

        AccessToken accessToken = context.getToken();
        String usId = accessToken.getOtherClaims().get("user_id").toString();

        chain.doFilter(request, response);
    }

    private void handleNoSecurityContext(ServletRequest request, ServletResponse response, FilterChain chain) {
    }

}