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

    public UserRepository() {}

    public List<User> getAll(Session session) {
        Query<User> query = session.createQuery("FROM User", User.class);
        return query.getResultList();
    }

    public void save(Session session, User user) {
        session.persist(user);
    }

    public User update(Session session, User user) {
        return session.merge(user);
    }

    public void delete(Session session, User user) {
        session.remove(session.contains(user) ? user : session.merge(user));
    }

    public User getByUsername(Session session, String username) {
        Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    public boolean existsByUsername(Session session, String username) {
        String hql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("username", username)
                .uniqueResult();
        return count != null && count > 0;
    }

    public User getByEmail(Session session, String email) {
        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }

    public boolean existsByEmail(Session session, String email) {
        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult() != null;
    }

    public User getById(Session session, long id) {
        return session.get(User.class, id);
    }

    public User getByUserNameAndPassword(Session session, String username, String password) {
        Query<User> query = session.createQuery(
                "FROM User WHERE username = :username AND password = :password", User.class
        );
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.uniqueResult();
    }

    public List<User> getUsersByPlan(Session session, int numberOfMembers, BigDecimal monthlyContribution, Role role) {
        Query<User> query = session.createQuery(
                "FROM User WHERE numberOfMembers = :members AND monthlyContribution = :contribution AND role = :role",
                User.class
        );
        query.setParameter("members", numberOfMembers);
        query.setParameter("contribution", monthlyContribution);
        query.setParameter("role", role);
        return query.getResultList();
    }

    public User getByGroupRole(Session session, Role role) {
        Query<User> query = session.createQuery("FROM User WHERE role = :role", User.class);
        query.setParameter("role", role);
        return query.uniqueResult();
    }

    public boolean existsByBankAccount(Session session, String bankAccount) {
        String hql = "SELECT COUNT(u) FROM User u WHERE u.bankAccount = :bankAccount";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("bankAccount", bankAccount)
                .uniqueResult();
        return count != null && count > 0;
    }

    public boolean existsByMobileNumber(Session session, String mobileNumber) {
        String hql = "SELECT COUNT(u) FROM User u WHERE u.mobileNumber = :mobileNumber";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("mobileNumber", mobileNumber)
                .uniqueResult();
        return count != null && count > 0;
    }

    public void deleteAll(Session session) {
        session.createQuery("DELETE FROM User").executeUpdate();
    }
}




