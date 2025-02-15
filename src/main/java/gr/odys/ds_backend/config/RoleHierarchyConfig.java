package gr.odys.ds_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {

    @SuppressWarnings("deprecation")
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
            ROLE_ADMIN > ROLE_VET
            ROLE_VET > ROLE_EMPLOYEE
            ROLE_EMPLOYEE > ROLE_USER
            """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}