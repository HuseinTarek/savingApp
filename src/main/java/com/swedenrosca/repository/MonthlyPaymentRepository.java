package com.swedenrosca.repository;

import com.swedenrosca.model.MonthlyPayment;
import com.swedenrosca.model.Participant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;


    public class MonthlyPaymentRepository {
        private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

        public void save(MonthlyPayment payment) {
            Transaction tx = null;
            try (Session session = sessionFactory.openSession()) {
                tx = session.beginTransaction();
                session.persist(payment);
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }

        public MonthlyPayment getById(Long id) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find a monthly payment by ID
            Query<MonthlyPayment> query = session.createQuery(
                    "FROM MonthlyPayment WHERE id = :id", MonthlyPayment.class
            );
            query.setParameter("id", id);
            MonthlyPayment result = query.uniqueResult();

            session.getTransaction().commit();
            session.close();
            return result;
        }

        public List<MonthlyPayment> getByParticipant(Participant participant) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find all payments for a specific participant
            Query<MonthlyPayment> query = session.createQuery(
                    "FROM MonthlyPayment WHERE participant = :participant", MonthlyPayment.class
            );
            query.setParameter("participant", participant);
            List<MonthlyPayment> payments = query.list();

            session.getTransaction().commit();
            session.close();
            return payments;
        }

        public List<MonthlyPayment> getByGroupId(Long groupId) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find all payments associated with a group ID
            Query<MonthlyPayment> query = session.createQuery(
                    "FROM MonthlyPayment WHERE participant.group.id = :groupId", MonthlyPayment.class
            );
            query.setParameter("groupId", groupId);
            List<MonthlyPayment> payments = query.list();

            session.getTransaction().commit();
            session.close();
            return payments;
        }

        public List<MonthlyPayment> getByGroupIdAndMonth(Long groupId, int monthNumber) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find all payments for a group in a specific month
            Query<MonthlyPayment> query = session.createQuery(
                    "FROM MonthlyPayment WHERE participant.group.id = :groupId AND monthNumber = :month", MonthlyPayment.class
            );
            query.setParameter("groupId", groupId);
            query.setParameter("month", monthNumber);
            List<MonthlyPayment> payments = query.list();

            session.getTransaction().commit();
            session.close();
            return payments;
        }

        public List<MonthlyPayment> getAll() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Retrieve all monthly payments
            Query<MonthlyPayment> query = session.createQuery("FROM MonthlyPayment", MonthlyPayment.class);
            List<MonthlyPayment> payments = query.getResultList();
            session.getTransaction().commit();
            session.close();
            return payments;
        }
    }



