package in.connectwithsandeepan.marathon.exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String body = """
                {
                  "timestamp": "%s",
                  "error": "Forbidden",
                  "message": "You do not have permission to access this resource. From chain",
                  "path": "%s",
                  "requestMethod": "%s"
                }
                """.formatted(LocalDateTime.now(), request.getRequestURI(), request.getMethod());

        response.getWriter().write(body);
    }
}
