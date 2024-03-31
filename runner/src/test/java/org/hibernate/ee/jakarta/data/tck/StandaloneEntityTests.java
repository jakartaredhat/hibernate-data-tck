package org.hibernate.ee.jakarta.data.tck;

import ee.jakarta.tck.data.core.cdi.provider.DirectoryBuildCompatibleExtension;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters_;
import ee.jakarta.tck.data.framework.read.only.CustomRepository;
import ee.jakarta.tck.data.framework.read.only.CustomRepository_;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers_;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers_;
import ee.jakarta.tck.data.standalone.entity.Boxes_;
import ee.jakarta.tck.data.standalone.entity.EntityTests;
import jakarta.data.repository.Repository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Hibernate;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.function.Function;

@ExtendWith(WeldJunit5Extension.class)
public class StandaloneEntityTests extends EntityTests {
    static class EMFAction implements Function<InjectionPoint, EntityManagerFactory> {
        @Override
        public EntityManagerFactory apply(InjectionPoint injectionPoint) {
            HashMap<String, Object> properties = new HashMap<>();
            BeanManager beanManager = CDI.current().getBeanManager();
            System.out.println("BeanManager: " + beanManager);
            properties.put("jakarta.persistence.bean.manager", beanManager);
            properties.put("javax.persistence.bean.manager", beanManager);
            return Persistence.createEntityManagerFactory("jakarta-data-tck", properties);
        }
    }
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(EntityTests.class,
                    AsciiCharacters_.class,
                    Boxes_.class,
                    NaturalNumbers_.class,
                    PositiveIntegers_.class,
                    CustomRepository_.class,
                    DirectoryBuildCompatibleExtension.class)
            .activate(RequestScoped.class)
            .setPersistenceUnitFactory((Function) new EMFAction())
            .build()
            ;

}
