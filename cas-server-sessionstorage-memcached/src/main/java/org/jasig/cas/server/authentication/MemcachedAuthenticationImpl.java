/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.cas.server.authentication;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Immutable version of the {@link org.jasig.cas.server.authentication.Authentication} interface that can easily
 * be stored via Memcached.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.5
 */
public final class MemcachedAuthenticationImpl implements Authentication, Serializable {

    private final Date date = new Date();

    private final AttributePrincipal principal;

    private final Map<String, List<Object>> authenticationMetaData;

    private final boolean longTermAuthentication;

    public MemcachedAuthenticationImpl(final AttributePrincipal principal, final Map<String, List<Object>> authenticationMetaData, final boolean longTermAuthentication) {
        this.principal = principal;
        this.authenticationMetaData = Collections.unmodifiableMap(authenticationMetaData);
        this.longTermAuthentication = longTermAuthentication;
    }

    public Date getAuthenticationDate() {
        return new Date(this.date.getTime());
    }

    public Map<String, List<Object>> getAuthenticationMetaData() {
        return this.authenticationMetaData;
    }

    public AttributePrincipal getPrincipal() {
        return this.principal;
    }

    public boolean isLongTermAuthentication() {
        return this.longTermAuthentication;
    }
}
