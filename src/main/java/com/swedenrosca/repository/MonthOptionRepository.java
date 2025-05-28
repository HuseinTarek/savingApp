package com.swedenrosca.repository;

import com.swedenrosca.model.MonthOption;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class MonthOptionRepository {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    // MonthOptionRepository
    public List<MonthOption> getAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        List<MonthOption> options = session.createQuery(
                "SELECT DISTINCT mo FROM MonthOption mo", MonthOption.class
        ).getResultList();
        
        session.getTransaction().commit();
        session.close();
        return options;
    }


    public void save(MonthOption option) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(option);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void update(MonthOption option) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(option);
            tx.commit();
        }
    }

        public void deleteById(Long id) {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();
                MonthOption opt = session.get(MonthOption.class, id);
                if (opt != null) {
                    session.remove(opt);
                }
                tx.commit();
            }
        }

    public MonthOption findById(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        MonthOption result = session.get(MonthOption.class, id);
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM MonthOption").executeUpdate();
            session.getTransaction().commit();
        }
    }
}



