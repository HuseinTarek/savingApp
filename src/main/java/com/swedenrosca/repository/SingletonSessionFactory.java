package com.swedenrosca.repository;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SingletonSessionFactory {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    private SingletonSessionFactory() {
        // private to prevent instantiation
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

