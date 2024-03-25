package org.hibernate.data.tck.container;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class EmbeddedContainerExtension implements LoadableExtension {

    /**
     * Registers extension points defining the Embedded Container
     */
    @Override
    public void register(final ExtensionBuilder builder) {
        builder.service(DeployableContainer.class, EmbeddedDeployableContainer.class);
    }
}