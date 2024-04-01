package org.hibernate.ee.jakarta.data.tck;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;

@ApplicationScoped
public class EntityManagerFactoryProducer {
    private EntityManagerFactory entityManagerFactory;;
    @Produces
    public EntityManagerFactory createEntityManagerFactory() {
        HashMap<String, Object> properties = new HashMap<>();
        BeanManager beanManager = CDI.current().getBeanManager();
        System.out.println("BeanManager: " + beanManager);
        properties.put("jakarta.persistence.bean.manager", beanManager);
        properties.put("javax.persistence.bean.manager", beanManager);
        entityManagerFactory = Persistence.createEntityManagerFactory("jakarta-data-tck", properties);
        return entityManagerFactory;
    }
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
