package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.service.ParticipantService;
import com.swedenrosca.service.RoundService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RoundController {
    private final ParticipantService participantService;
    private final RoundService roundService;

    public RoundController(ParticipantService participantService, RoundService roundService) {
        this.participantService = participantService;
        this.roundService = roundService;
    }

    public String createRound(Long groupId, int turnOrder, BigDecimal amount) {
        return roundService.createRound(groupId, turnOrder, amount);
    }

    public List<Round> getGroupRounds(Long groupId) {
        return roundService.getGroupRounds(groupId);
    }

    public List<Round> getAllRounds() {
        return roundService.getAllRounds();
    }

    public List<Round> getUserRounds(Long userId) {
        return roundService.getGroupRounds(userId);
    }

    public Round getRoundById(Long id) {
        return roundService.getRoundById(id);
    }

    public String updateRound(Long id, BigDecimal amount, Group group, int turnOrder,
                              LocalDateTime startDate, LocalDateTime endDate) {
        return roundService.updateRound(id, amount, group, turnOrder, startDate, endDate);
    }

    public String deleteRound(Long id) {
        return roundService.deleteRound(id);
    }
}