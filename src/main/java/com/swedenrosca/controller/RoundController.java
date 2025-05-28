package com.swedenrosca.controller;

import com.swedenrosca.model.Participant;
import com.swedenrosca.model.Round;
import com.swedenrosca.model.Group;
import com.swedenrosca.model.User;
import com.swedenrosca.repository.GroupRepository;
import com.swedenrosca.repository.ParticipantRepository;
import com.swedenrosca.repository.RoundRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RoundController {
      private final ParticipantController participantController;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final RoundRepository roundRepository ;

    public RoundController(ParticipantController participantController, GroupRepository groupRepository, ParticipantRepository participantRepository, RoundRepository roundRepository) {
        this.participantController = participantController;
        this.groupRepository = groupRepository;
        this.participantRepository = participantRepository;
        this.roundRepository = roundRepository;
    }

    public String createRound(Long groupId, int turnOrder, BigDecimal amount) {
         Group group = groupRepository.getById(groupId);
        if (group == null) {
            return "Group not found";
        }

         Participant winner = participantRepository.getByGroupAndTurnOrder(group,turnOrder);

        if (winner == null) {
            return "Winner not found";
        }

        LocalDateTime startDate = group.getStartDate().plusMonths(turnOrder - 1);
        LocalDateTime endDate = startDate.plusMonths(1).minusMinutes(1);

        Round round = new Round();
        round.setGroup(group);
        round.setWinnerParticipant(winner);
        round.setAmount(amount);
        round.setStartDate(startDate);
        round.setEndDate(endDate);
        roundRepository.save(round);
        return "Round created successfully!";
    }

    public List<Round> getGroupRounds(Long groupId) {
        Group group = groupRepository.getById(groupId);
        if (group == null) {
            return List.of();
        }
        return roundRepository.getByGroup(group);
    }

    public List<Round> getAllRounds() {
        return roundRepository.getAll();
    }




    public List<Round> getUserRounds(Long userId) {
        Participant participant = participantRepository.getById(userId);
        if (participant == null) {
            return List.of();
        }
        return roundRepository.getByWinnerUser(participant);
    }

    public Round getRoundById(Long id) {
        return roundRepository.getById(id);
    }

    public String updateRound(Long id, BigDecimal amount, Group group, int turnOrder,
                              LocalDateTime startDate, LocalDateTime endDate) {
        Round round = roundRepository.getById(id);
        if (round == null) {
            return "Round not found";
        }
        round.setAmount(amount);
        round.setGroup(group);
        round.setTurnOrder(turnOrder);
        round.setStartDate(startDate);
        round.setEndDate(endDate);

        roundRepository.update(round);
        return "Round updated successfully";
    }

    public String deleteRound(Long id) {
        Round round = roundRepository.getById(id);
        if (round == null) {
            return "Round not found";
        }
        roundRepository.delete(round);
        return "Round deleted successfully!";
    }

}