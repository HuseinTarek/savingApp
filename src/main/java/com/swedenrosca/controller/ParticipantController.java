package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.service.ParticipantService;
import java.time.LocalDateTime;
import java.util.List;

public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    // Show all participants in a group
    public void showParticipants(Group group) {
        List<Participant> participants = participantService.getByGroup(group);
        for (Participant p : participants) {
            System.out.println("👤 " + p.getUser().getUsername() +
                    " | Turn: " + p.getTurnOrder() +
                    " | Paid: " + p.getStatus() +
                    " | Received: " + p.getReceiveStatus());
        }
    }

    // Mark a participant as paid
    public void markAsPaid(Participant participant) {
        participant.setStatus(PaymentStatus.PAID);
        participant.setPaidAt(LocalDateTime.now());
        participantService.updateParticipant(participant);
    }

    // Mark a participant as received
    public void markAsReceived(Participant participant) {
        participant.setReceiveStatus(ReceiveStatus.RECEIVED);
        participant.setReceivedAt(LocalDateTime.now());
        participantService.updateParticipant(participant);
    }

    public Participant findById(Long participantId) {
        return participantService.getById(participantId);
    }

    public Participant winner(Group group, int turnOrder) {
        return participantService.getByGroupAndTurnOrder(group, turnOrder);
    }

    public int countParticipants(Long groupId) {
        return participantService.countParticipantsByGroup(groupId);
    }

    public User getUserById(Long userId) {
        Participant participant = participantService.getById(userId);
        return participant != null ? participant.getUser() : null;
    }
}
