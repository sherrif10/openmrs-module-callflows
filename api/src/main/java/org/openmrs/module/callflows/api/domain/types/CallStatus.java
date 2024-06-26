/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.domain.types;

/**
 * The status of a IVR call
 * Some statuses adapted from the IVR module
 *
 * @author bramak09
 */
public enum CallStatus {

    /**
     * OpenMRS received an request to initiate an outbound call
     */
    OPENMRS_INITIATED,

    /**
     * A call was initiated at the IVR provider
     */
    INITIATED,

    /**
     * A call was created at the IVR provider
     */
    STARTED,

    /**
     * The destination has confirmed that the call is ringing
     */
    RINGING,

    /**
     * A call is in progress
     */
    IN_PROGRESS,

    /**
     * The destination has answered the call
     */
    ANSWERED,

    /**
     * The destination has not answered the call
     */
    UNANSWERED,

    /**
     * The call is answered by a machine
     */
    MACHINE,

    /**
     * A call was unsuccessful because the line was busy
     */
    BUSY,

    /**
     * The call was canceled by the caller
     */
    CANCELLED,

    /**
     * A call was unsuccessful for some other reason than BUSY or NO_ANSWER
     */
    FAILED,

    /**
     * The call attempt was rejected by the destination
     */
    REJECTED,

    /**
     * A call was unsuccessful because the recipient did not answer
     */
    NO_ANSWER,

    /**
     * The call timed out before it was answered
     */
    TIMEOUT,

    /**
     * A call was completed
     */
    COMPLETED,

    /**
     * I don't know what happened
     */
    UNKNOWN,
}
