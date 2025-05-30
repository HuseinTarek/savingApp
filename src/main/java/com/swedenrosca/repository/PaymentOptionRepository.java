package com.swedenrosca.repository;

import com.swedenrosca.model.PaymentOption;
import org.hibernate.Session;
import org.hibernate.query.*;
import java.util.List;

public class PaymentOptionRepository {
    
    public List<PaymentOption> getAllMonthlyPayments(Session session) {
        Query<PaymentOption> query = session.createQuery("FROM PaymentOption", PaymentOption.class);
        return query.getResultList();
    }
    
    public void save(Session session, PaymentOption option) {
        session.persist(option);
    }

    public void update(Session session, PaymentOption option) {
        session.merge(option);
    }

    public void deleteById(Session session, Long id) {
        PaymentOption option = session.get(PaymentOption.class, id);
        if (option != null) {
            session.remove(option);
        }
    }

    public PaymentOption findById(Session session, Long id) {
        return session.get(PaymentOption.class, id);
    }

    public void deleteAll(Session session) {
        session.createMutationQuery("DELETE FROM PaymentOption").executeUpdate();
    }

    public List<PaymentOption> getAll(Session session) {
        return session.createQuery("FROM PaymentOption", PaymentOption.class).getResultList();
    }
}
