package org.hibernate.ee.jakarta.data.tck;

import ee.jakarta.tck.data.standalone.persistence.Catalog_;
import ee.jakarta.tck.data.standalone.persistence.PersistenceEntityTests;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldJunit5Extension.class)
public class StandalonePersistenceEntityTests extends PersistenceEntityTests {
    @Inject
    EntityManagerFactory appEMF;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(PersistenceEntityTests.class,
                    Catalog_.class,
                    EntityManagerFactoryProducer.class)
            .activate(RequestScoped.class)
            .inject(this)
            .setPersistenceUnitFactory(ip -> appEMF)
            .build()
            ;
}
