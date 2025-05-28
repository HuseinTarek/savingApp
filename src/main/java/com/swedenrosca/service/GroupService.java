package com.swedenrosca.service;

import com.swedenrosca.model.Group;
import com.swedenrosca.model.User;
import com.swedenrosca.model.GroupStatus;
import com.swedenrosca.repository.GroupRepository;

public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group createGroup(Group group, User creator) {
        // Validate that the group has at least one member (the creator)
        if (group.getParticipants() == null || group.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("A group must have at least one member (the creator)");
        }

        // Set initial status to WAITING_FOR_MEMBERS since we already have the creator
        group.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
        
        // Save the group
        return groupRepository.save(group);
    }
} 