package in.connectwithsandeepan.marathon.filter;

import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.service.AuthUserDetailsService;
import in.connectwithsandeepan.marathon.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AuthUserDetailsService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        log.debug("JWT filter started for request: {} {}", request.getMethod(), request.getRequestURI());
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                log.debug("Extracted token from Authorization header");
                username = jwtUtil.extractUsername(token);
                log.info("Username extracted from token: {}", username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Loading user details for username: {}", username);
                UserDetails user = this.userService.loadUserByUsername(username);
                if (!user.isEnabled()) {
                    log.warn("User account is disabled: {}", username);
                    throw new DisabledException("User account is disabled");
                }
                if (jwtUtil.validateToken(token)) {
                    log.info("Token is valid for username: {}", username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("Token validation failed for username: {}", username);
                }
            }

            filterChain.doFilter(request, response);
            log.debug("JWT filter completed for request: {} {}", request.getMethod(), request.getRequestURI());
        } catch (ExpiredJwtException | DisabledException ex) {
            log.error("JWT token expired: {}", ex.getMessage());
            handleJwtException(response, ex, request);
        }
    }

    private void handleJwtException(HttpServletResponse response, Exception ex, HttpServletRequest request) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        response.getWriter().write(error.toString());
    }
}
