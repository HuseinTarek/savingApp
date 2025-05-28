package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.SessionFactory;
import java.math.BigDecimal;
import java.util.List;

public class PaymentController {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
    final private PaymentRepository paymentRepository ;
    final private ParticipantRepository participantRepository;
    final private GroupRepository groupRepository ;

    public PaymentController(PaymentRepository paymentRepository, ParticipantRepository participantRepository, GroupRepository groupRepository) {
        this.paymentRepository = paymentRepository;
        this.participantRepository = participantRepository;
        this.groupRepository = groupRepository;
    }

//    public Payment makePayment( Long userId, Long groupId, BigDecimal amount) {
//        return paymentRepository.(userId, groupId, amount);
//    }


    public List<Payment> getPayerPayments(Participant participant) {
        if (participant == null || participant.getId() == null) {
            return List.of();
        }

        Participant managed = participantRepository.getById(participant.getId());
        if (managed == null) {
            return List.of();
        }

        return paymentRepository.getByPayer(managed);
    }

    public List<Payment> getGroupPayments(Long groupId) {
        Group group = groupRepository.getById(groupId);
        if (group == null) {
            return List.of();
        }
        return paymentRepository.getByGroup(group);
    }

    public BigDecimal getTotalPaymentsByPayer(Long participantId) {
        Participant participant = participantRepository.getById(participantId);
        if (participant == null) {
            return BigDecimal.ZERO;
        }
        List<Payment> payments = paymentRepository.getByPayer(participant);

        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPaymentsByGroup(Long groupId) {
        Group group = groupRepository.getById(groupId);
        if (group == null) {
            return BigDecimal.ZERO;
        }
        List<Payment> payments = paymentRepository.getByGroup(group);
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}