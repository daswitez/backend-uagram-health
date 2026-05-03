package bo.edu.uagrm.ugram.identity.entity;

/**
 * User types in the Ugram Health ecosystem.
 * Maps directly to the RBAC permission model.
 */
public enum UserType {
    STUDENT,
    PATIENT,
    DOCTOR,
    ADMIN,
    LAB_TECH,
    RECEPTIONIST
}
