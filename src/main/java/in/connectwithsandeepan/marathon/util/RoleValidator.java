package in.connectwithsandeepan.marathon.util;


import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleValidator {
    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");

    public Set<String> sanitizeRoles(Set<String> inputRoles) {
        if (inputRoles == null || inputRoles.isEmpty()) {
            return Set.of("ROLE_USER");
        }

        return inputRoles.stream()
                .map(String::toUpperCase)
                .distinct()
                .filter(ALLOWED_ROLES::contains)
                .collect(Collectors.toSet());
    }
}
