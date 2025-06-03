package com.swedenrosca.model;

public enum GroupStatus {
    WAITING_FOR_MEMBERS,
    PENDING_APPROVAL,    // Round is created bUT NEED APPROVAL TO START
    ACTIVE,     // Round is currently running
    COMPLETED ,  // Round has finished
    BLOCKED // Round has blocked
} 