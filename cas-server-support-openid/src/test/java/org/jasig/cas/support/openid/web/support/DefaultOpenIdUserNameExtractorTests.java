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

package org.jasig.cas.support.openid.web.support;

import junit.framework.TestCase;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.1 $ $Date: 2005/08/19 18:27:17 $
 * @since 3.1
 *
 */
public class DefaultOpenIdUserNameExtractorTests extends TestCase {

    private DefaultOpenIdUserNameExtractor extractor = new DefaultOpenIdUserNameExtractor();

    public void testExtractionSuccessful() {
        assertEquals("scootman28", this.extractor
            .extractLocalUsernameFromUri("http://test.com/scootman28"));
    }
    
    public void testExtractionFailed() {
        assertNull(this.extractor
            .extractLocalUsernameFromUri("test.com"));
    }

    public void testNull() {
        assertNull(this.extractor
            .extractLocalUsernameFromUri(null));
    }
}
