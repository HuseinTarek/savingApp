package com.swedenrosca.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.swedenrosca.model.*;

public class SingletonSessionFactory {
    private static SessionFactory sessionFactory;

    private SingletonSessionFactory() {}

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                
                // Add all entity classes
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Group.class);
                configuration.addAnnotatedClass(Participant.class);
                configuration.addAnnotatedClass(Payment.class);
                configuration.addAnnotatedClass(Round.class);
                configuration.addAnnotatedClass(PaymentPlan.class);
                configuration.addAnnotatedClass(PaymentOption.class);
                configuration.addAnnotatedClass(MonthOption.class);
                configuration.addAnnotatedClass(MonthlyPayment.class);
                
                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                throw new RuntimeException("Error creating SessionFactory: " + e.getMessage(), e);
            }
        }
        return sessionFactory;
    }
} 