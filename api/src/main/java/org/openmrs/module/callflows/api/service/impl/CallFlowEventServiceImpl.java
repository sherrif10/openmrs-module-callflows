/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.service.CallFlowEventService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

/** Service to manage CallFlow Events. */
public class CallFlowEventServiceImpl extends BaseOpenmrsService implements CallFlowEventService {

  /**
   * Implementation to send the Event Message
   *
   * @param event CallFlow Event
   * @implNote The Propagation.REQUIRES_NEW transaction propagation ensures that however the JMS
   *     events are configured in the Event's module, it will be send and never rolled back.
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendEventMessage(CallFlowEvent event) {
    Event.fireEvent(event.getSubject(), convertParamsToEventMessage(event.getParameters()));
  }
  /**
   * Coverts the Params to Event Message
   *
   * @param params Map of params with String and Object as key and value respectively
   * @return Event Message
   */
  private EventMessage convertParamsToEventMessage(Map<String, Object> params) {
    EventMessage eventMessage = new EventMessage();

    for (String key : params.keySet()) {
      eventMessage.put(key, (Serializable) params.get(key));
    }

    return eventMessage;
  }
}
