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

package org.jasig.cas.web.flow;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.server.authentication.Credential;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.util.StringUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.RequestContext;

/**
 * Action to authenticate credentials and retrieve a TicketGrantingTicket for
 * those credentials. If there is a request for renew, then it also generates
 * the Service Ticket required.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0.4
 */
public class AuthenticationViaFormAction {

    /** Core we delegate to for handling all ticket related tasks. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private CookieGenerator warnCookieGenerator;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public final String submit(final RequestContext context, final Credential credentials, final MessageContext messageContext) throws Exception {
        final String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(context);
        final Service service = WebUtils.getService(context);

        if (StringUtils.hasText(context.getRequestParameters().get("renew")) && ticketGrantingTicketId != null && service != null) {

            try {
                final String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, credentials);
                WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
                putWarnCookieIfRequestParameterPresent(context);
                return "warn";
            } catch (final TicketException e) {
                if (e.getCause() != null && AuthenticationException.class.isAssignableFrom(e.getCause().getClass())) {
                    populateErrorsInstance(e, messageContext);
                    return "error";
                }
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempted to generate a ServiceTicket using renew=true with different credentials", e);
                }
            }
        }

        try {
            WebUtils.putTicketGrantingTicketInRequestScope(context, this.centralAuthenticationService.createTicketGrantingTicket(credentials));
            putWarnCookieIfRequestParameterPresent(context);
            return "success";
        } catch (final TicketException e) {
            populateErrorsInstance(e, messageContext);
            return "error";
        }
    }


    private void populateErrorsInstance(final TicketException e, final MessageContext messageContext) {

        try {
            messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).defaultText(e.getCode()).build());
        } catch (final Exception fe) {
            logger.error(fe.getMessage(), fe);
        }
    }

    private void putWarnCookieIfRequestParameterPresent(final RequestContext context) {
        final HttpServletResponse response = WebUtils.getHttpServletResponse(context);

        if (StringUtils.hasText(context.getExternalContext().getRequestParameterMap().get("warn"))) {
            this.warnCookieGenerator.addCookie(response, "true");
        } else {
            this.warnCookieGenerator.removeCookie(response);
        }
    }

    public final void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }
   
    public final void setWarnCookieGenerator(final CookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }
}
