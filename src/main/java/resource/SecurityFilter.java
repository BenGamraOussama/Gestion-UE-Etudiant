package resource;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final String AUTH_SCHEME = "Bearer";
    private static final String SECRET = "change-this-secret-to-a-longer-random-value"; // keep in sync with AuthenticationEndPoint

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.toLowerCase().startsWith(AUTH_SCHEME.toLowerCase() + " ")) {
            abortUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(AUTH_SCHEME.length()).trim();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            final String username = claims.getSubject();
            @SuppressWarnings("unchecked")
            final List<String> roles = (List<String>) Optional.ofNullable(claims.get("roles")).orElse(List.of());

            // Build a SecurityContext with principal and simple role check
            var original = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new jakarta.ws.rs.core.SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return roles.contains(role);
                }

                @Override
                public boolean isSecure() {
                    return original != null && original.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return AUTH_SCHEME;
                }
            });

        } catch (SignatureException | IllegalArgumentException e) {
            abortUnauthorized(requestContext, "Invalid or malformed token");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            abortUnauthorized(requestContext, "Token expired");
        }
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTH_SCHEME + " realm=\"api\"")
                .entity(message)
                .build());
    }
}
