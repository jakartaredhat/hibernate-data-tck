package org.hibernate.data.tck.container;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class EmbeddedContainerConfiguration implements ContainerConfiguration {

    @Override
    public void validate() throws ConfigurationException {
        /*
        if (username != null && password == null) {
            throw new ConfigurationException("username has been set, but no password given");
        }
        if (protocol != null && !("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol))) {
            throw new ConfigurationException("Only http and https are allowed protocol settings, found " + protocol);
        }
        */
    }
}
