package org.hibernate.data.tck.container;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;

public class EmbeddedDeployableContainer implements DeployableContainer {
    @Override
    public Class getConfigurationClass() {
        return null;
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return null;
    }

    @Override
    public void undeploy(Archive archive) throws DeploymentException {

    }

    @Override
    public ProtocolMetaData deploy(Archive archive) throws DeploymentException {
        return null;
    }
}
