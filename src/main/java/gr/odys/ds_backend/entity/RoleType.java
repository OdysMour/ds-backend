package gr.odys.ds_backend.entity;

public enum RoleType {
    ROLE_USER,           // Basic user access
    ROLE_EMPLOYEE,       // Staff member with elevated permissions
    ROLE_VET,           // Veterinarian with animal health permissions
    ROLE_ADMIN          // System administrator with full access
}