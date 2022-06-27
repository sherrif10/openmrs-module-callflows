/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.util.PrivilegeConstants;

import java.util.List;

/**
 * Service to manage CallFlows
 *
 * @author bramak09
 * @see org.openmrs.module.callflows.api.domain.CallFlow
 */
public interface CallFlowService extends OpenmrsService {

    /**
     * Create a call flow in the system.
     * Validates the call flow name to ensure that it contains only alphanumeric characters
     * as special characters have special meaning during call flow execution
     *
     * @param callflow the call flow object to be saved
     * @return the callflow with id as generated by the database
     * @throws CallFlowAlreadyExistsException if a duplicate call flow with the same name already exists
     * @throws IllegalArgumentException       if the call flow name does not contain only alphanumeric characters
     */
    @Authorized(PrivilegeConstants.CALLFLOWS_PRIVILEGE)
    CallFlow create(CallFlow callflow) throws CallFlowAlreadyExistsException;

    /**
     * Save the current state of {@code callFlow}. Creates a new CallFlow or updates existing.
     * <p>
     * This method complies with default OpenMRS entity service method names pattern and is required.
     * </p>
     *
     * @param callFlow the callFlow to save, not null
     * @return the saved CallFlow, never null
     */
    @Authorized(PrivilegeConstants.CALLFLOWS_PRIVILEGE)
    CallFlow saveCallFlow(CallFlow callFlow);

    /**
     * Updates a call flow in the system.
     * If the name of the call flow is being changed, it cannot be the same as that of an existing call flow
     * and special characters cannot be used in the name of the callflow as they have special significance during call flow execution
     *
     * @param callflow the call flow object to be updated
     * @return the callflow object
     * @throws CallFlowAlreadyExistsException if a duplicate callflow with the same name already exists
     * @throws IllegalArgumentException       if the call flow is not retrievable or does not contain only alphanumeric characters
     */
    @Authorized(PrivilegeConstants.CALLFLOWS_PRIVILEGE)
    CallFlow update(CallFlow callflow) throws CallFlowAlreadyExistsException;

    /**
     * Find all callflows that start with a specific name
     *
     * @param prefix a search term that is used to search for callflows
     * @return a list of found callflows
     */
    @Authorized(PrivilegeConstants.CALLFLOWS_PRIVILEGE)
    List<CallFlow> findAllByNamePrefix(String prefix);

    /**
     * Find a callflow by the given name
     *
     * @param name to search by
     * @return the callflow corresponding to the passed name
     * @throws IllegalArgumentException if the callflow cannot be found with the given name
     */
    CallFlow findByName(String name);

    /**
     * Deletes a callflow
     *
     * @param id of the callflow to delete
     * @throws IllegalArgumentException if no callflow with the given id exists
     */
    @Authorized(PrivilegeConstants.CALLFLOWS_PRIVILEGE)
    void delete(Integer id);
}
