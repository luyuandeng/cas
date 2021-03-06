/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.adaptors.ldap;

import org.apache.commons.io.IOUtils;
import org.jasig.cas.util.ldap.uboundid.InMemoryTestLdapDirectoryServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.ldaptive.LdapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;

import java.util.Collection;

/**
 * Base class for LDAP tests that provision and deprovision DIRECTORY data as part of test setup/teardown.
 * @author Marvin S. Addison
 * @author Misagh Moayyed
 */
public abstract class AbstractLdapTests implements ApplicationContextAware {

    private ApplicationContext context;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static InMemoryTestLdapDirectoryServer DIRECTORY;

    @BeforeClass
    public static void beforeClass() throws Exception {
        final ClassPathResource properties = new ClassPathResource("ldap.properties");
        final ClassPathResource schema = new ClassPathResource("schema/standard-ldap.schema");

        DIRECTORY = new InMemoryTestLdapDirectoryServer(properties.getFile(),
                new ClassPathResource("ldif/ldap-base.ldif").getFile(),
                schema.getFile());
    }

    @AfterClass
    public static void tearDown() {
        IOUtils.closeQuietly(DIRECTORY);
    }

    protected Collection<LdapEntry> getEntries() {
        return DIRECTORY.getLdapEntries();
    }

    protected String getUsername(final LdapEntry entry) {
        final String unameAttr = this.context.getBean("usernameAttribute", String.class);
        return entry.getAttribute(unameAttr).getStringValue();
    }

    protected <T> T getBean(final String id, final Class<T> clazz) {
        return (T) this.context.getBean(id, clazz);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
