/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.validate.annotation;

import org.openmrs.module.callflows.ValidationMessageConstants;
import org.openmrs.module.callflows.api.validate.validator.CallFlowValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Constraint(validatedBy = { CallFlowValidator.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCallFlow {

    /**
     * Specify the message in case of a validation error
     *
     * @return the message about the error
     */
    String message() default ValidationMessageConstants.CALL_FLOW_INVALID;

    /**
     * Specify validation groups, to which this constraint belongs
     *
     * @return array with group classes
     */
    Class<?>[] groups() default {
    };

    /**
     * Specify custom payload objects
     *
     * @return array with payload classes
     */
    Class<? extends Payload>[] payload() default {
    };
}
