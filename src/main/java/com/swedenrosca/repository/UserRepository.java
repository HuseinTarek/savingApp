package com.swedenrosca.repository;

import com.swedenrosca.model.Role;
import com.swedenrosca.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class UserRepository {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    public UserRepository() {}

        public List<User> getAll() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Retrieve all users
            Query<User> query = session.createQuery("FROM User", User.class);
            List<User> users = query.getResultList();

            session.getTransaction().commit();
            session.close();
            return users;
        }

        public User save(User user) {
            Transaction tx = null;
            try (Session session = sessionFactory.openSession()) {
                tx = session.beginTransaction();
                session.persist(user);
                tx.commit();
                return user;
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }

        public User update(User user) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            User managedUser = session.merge(user); // Merge for update
            session.getTransaction().commit();
            session.close();
            return managedUser;
        }

        public void delete(User user) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            User managedUser = session.merge(user); // Ensure managed before delete
            session.remove(managedUser);
            session.getTransaction().commit();
            session.close();
        }

        public User getByUsername(String username) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find user by username
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();

            session.getTransaction().commit();
            session.close();
            return user;
        }

        public boolean existsByUsername(String username) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Check if a user with this username exists
            String hql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("username", username)
                    .uniqueResult();

            session.getTransaction().commit();
            session.close();
            return count != null && count > 0;
        }

        public User getByEmail(String email) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find user by email
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();

            session.getTransaction().commit();
            session.close();
            return user;
        }

        public boolean existsByEmail(String email) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Check if a user with this email exists
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            boolean exists = query.uniqueResult() != null;

            session.getTransaction().commit();
            session.close();
            return exists;
        }

        public User getById(long id) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find user by ID
            Query<User> query = session.createQuery("FROM User WHERE id = :id", User.class);
            query.setParameter("id", id);
            User user = query.uniqueResult();

            session.getTransaction().commit();
            session.close();
            return user;
        }

        public User getByUserNameAndPassword(String username, String password) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Authenticate user by username and password
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :username AND password = :password", User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            User user = query.uniqueResult();

            session.getTransaction().commit();
            session.close();
            return user;
        }

        public List<User> getUsersByPlan(int numberOfMembers, BigDecimal monthlyContribution, Role role) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find users by membership plan and role
            Query<User> query = session.createQuery(
                    "FROM User WHERE numberOfMembers = :members AND monthlyContribution = :contribution AND role = :role",
                    User.class
            );
            query.setParameter("members", numberOfMembers);
            query.setParameter("contribution", monthlyContribution);
            query.setParameter("role", role);
            List<User> users = query.getResultList();

            session.getTransaction().commit();
            session.close();
            return users;
        }

        public User getByGroupRole(Role role) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Find one user with the specified role
            Query<User> query = session.createQuery("FROM User WHERE role = :role", User.class);
            query.setParameter("role", role);
            User user = query.uniqueResult();

            session.getTransaction().commit();
            session.close();
            return user;
        }

    public boolean emailExists(String email) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("email", email)
                .uniqueResult();

        session.getTransaction().commit();
        session.close();

        return count != null && count > 0;
    }



    public boolean existsByBankAccount(String bankAccount) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT COUNT(u) FROM User u WHERE u.bankAccount = :bankAccount";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("bankAccount", bankAccount)
                .uniqueResult();

        session.getTransaction().commit();
        session.close();
        return count != null && count > 0;
    }

    public boolean existsByMobileNumber(String mobileNumber) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT COUNT(u) FROM User u WHERE u.mobileNumber = :mobileNumber";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("mobileNumber", mobileNumber)
                .uniqueResult();

        session.getTransaction().commit();
        session.close();
        return count != null && count > 0;
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }
}




