package com.swedenrosca.repository;

import com.swedenrosca.model.MonthlyPayment;
import com.swedenrosca.model.Participant;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class MonthlyPaymentRepository {

    public void save(Session session, MonthlyPayment payment) {
        session.persist(payment);
    }

    public MonthlyPayment getById(Session session, Long id) {
        Query<MonthlyPayment> query = session.createQuery(
                "FROM MonthlyPayment WHERE id = :id", MonthlyPayment.class
        );
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public List<MonthlyPayment> getByParticipant(Session session, Participant participant) {
        Query<MonthlyPayment> query = session.createQuery(
                "FROM MonthlyPayment WHERE participant = :participant", MonthlyPayment.class
        );
        query.setParameter("participant", participant);
        return query.list();
    }

    public List<MonthlyPayment> getByGroupId(Session session, Long groupId) {
        Query<MonthlyPayment> query = session.createQuery(
                "FROM MonthlyPayment WHERE participant.group.id = :groupId", MonthlyPayment.class
        );
        query.setParameter("groupId", groupId);
        return query.list();
    }

    public List<MonthlyPayment> getByGroupIdAndMonth(Session session, Long groupId, int monthNumber) {
        Query<MonthlyPayment> query = session.createQuery(
                "FROM MonthlyPayment WHERE participant.group.id = :groupId AND monthNumber = :month", MonthlyPayment.class
        );
        query.setParameter("groupId", groupId);
        query.setParameter("month", monthNumber);
        return query.list();
    }

    public List<MonthlyPayment> getAll(Session session) {
        Query<MonthlyPayment> query = session.createQuery("FROM MonthlyPayment", MonthlyPayment.class);
        return query.getResultList();
    }

    public void update(Session session, MonthlyPayment payment) {
        session.merge(payment);
    }

    public void delete(Session session, MonthlyPayment payment) {
        session.remove(session.contains(payment) ? payment : session.merge(payment));
    }

    public void deleteAll(Session session) {
        session.createMutationQuery("DELETE FROM MonthlyPayment").executeUpdate();
    }
}



