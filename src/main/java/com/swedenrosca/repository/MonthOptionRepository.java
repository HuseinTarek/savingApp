package com.swedenrosca.repository;

import com.swedenrosca.model.MonthOption;
import org.hibernate.Session;
import java.util.List;

public class MonthOptionRepository {
    public List<MonthOption> getAll(Session session) {
        return session.createQuery("SELECT DISTINCT mo FROM MonthOption mo", MonthOption.class)
                      .getResultList();
    }

    public void save(Session session, MonthOption option) {
        session.persist(option);
    }

    public void update(Session session, MonthOption option) {
        session.merge(option);
    }

    public void deleteById(Session session, Long id) {
        MonthOption opt = session.get(MonthOption.class, id);
        if (opt != null) {
            session.remove(opt);
        }
    }

    public MonthOption findById(Session session, Long id) {
        return session.get(MonthOption.class, id);
    }

    public void deleteAll(Session session) {
        session.createMutationQuery("DELETE FROM MonthOption").executeUpdate();
    }
}



