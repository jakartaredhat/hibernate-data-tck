/*
 * Copyright 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package embedded;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Test CDI Bean
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
@ApplicationScoped
public class GreetingService {

    public static final String GREETING_PREPENDED = "Hello, ";

    /**
     * Prepends the specified name with {@link GreetingService#GREETING_PREPENDED}
     *
     * @param name
     * @return
     */
    public String greet(final String name) {
        return GREETING_PREPENDED + name;
    }
}
