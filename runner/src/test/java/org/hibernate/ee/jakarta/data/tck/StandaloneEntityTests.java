package org.hibernate.ee.jakarta.data.tck;

import ee.jakarta.tck.data.core.cdi.provider.DirectoryBuildCompatibleExtension;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters_;
import ee.jakarta.tck.data.framework.read.only.CustomRepository_;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers_;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers_;
import ee.jakarta.tck.data.standalone.entity.Boxes_;
import ee.jakarta.tck.data.standalone.entity.EntityTests;
import ee.jakarta.tck.data.standalone.entity.MultipleEntityRepo_;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldJunit5Extension.class)
public class StandaloneEntityTests extends EntityTests {
    @Inject
    EntityManagerFactory appEMF;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(EntityTests.class,
                    AsciiCharacters_.class,
                    Boxes_.class,
                    NaturalNumbers_.class,
                    PositiveIntegers_.class,
                    CustomRepository_.class,
                    MultipleEntityRepo_.class,
                    EntityManagerFactoryProducer.class,
                    DirectoryBuildCompatibleExtension.class)
            .activate(RequestScoped.class)
            .inject(this)
            .setPersistenceUnitFactory((ip) -> appEMF)
            .build()
            ;

}
