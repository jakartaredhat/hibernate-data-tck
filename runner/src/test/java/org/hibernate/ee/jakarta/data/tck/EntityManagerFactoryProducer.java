package org.hibernate.ee.jakarta.data.tck;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;

import java.util.HashMap;

@ApplicationScoped
public class EntityManagerFactoryProducer {

    @Produces @ApplicationScoped
    public SessionFactory createEntityManagerFactory(BeanManager beanManager) {
        HashMap<String, Object> properties = new HashMap<>();
        System.out.println("BeanManager: " + beanManager);
        properties.put("jakarta.persistence.bean.manager", beanManager);
        properties.put("javax.persistence.bean.manager", beanManager);
        SessionFactory sessionFactory
                = Persistence.createEntityManagerFactory("jakarta-data-tck", properties)
                        .unwrap(SessionFactory.class);
        return sessionFactory;
    }
}
