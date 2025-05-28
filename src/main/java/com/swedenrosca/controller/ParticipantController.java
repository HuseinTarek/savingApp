package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ParticipantController {
    private final ParticipantRepository participantRepository;

    public ParticipantController(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    // ⬅️ عرض كل المشاركين في مجموعة
    public void showParticipants(Group group) {
        List<Participant> participants = participantRepository.getByGroup(group);
        for (Participant p : participants) {
            System.out.println("👤 " + p.getUser().getUsername() +
                    " | Turn: " + p.getTurnOrder() +
                    " | Paid: " + p.getStatus() +
                    " | Received: " + p.getReceiveStatus());
        }
    }


    // ⬅️ تسجيل أن مشارك دفع
    public void markAsPaid(Participant participant) {
        participant.setStatus(PaymentStatus.PAID);
        participant.setPaidAt(LocalDateTime.now());
        participantRepository.update(participant);
    }


    public void markAsReceived(Participant participant) {
        participant.setReceiveStatus(ReceiveStatus.RECEIVED);
        participant.setReceivedAt(LocalDateTime.now());
        participantRepository.update(participant);
    }


    public Participant findById(Long participantId) {
        return participantRepository.getById(participantId);
    }

    public Participant winner(Group group, int turnOrder) {
        return participantRepository.getByGroupAndTurnOrder(group,turnOrder);
    }

    public int countParticipants(Long groupId) {
        return participantRepository.countParticipantsByGroup(groupId);
    }


    public User getUserById(Long userId) {
        return participantRepository.getById(userId).getUser();
    }
}
