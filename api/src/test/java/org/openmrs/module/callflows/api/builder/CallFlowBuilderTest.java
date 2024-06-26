/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.builder;

import org.junit.Test;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.helper.CallFlowContractHelper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * CallFlow builder Test Class
 *
 * @author bramak09
 */
public class CallFlowBuilderTest extends BaseTest {

    private CallFlowRequest callFlowRequest;

    private CallFlow callFlow;

    @Test
    public void shouldBuildCallFlowFromCallFlowRequest() {
        // Given
        callFlowRequest = CallFlowContractHelper.createMainFlowRequest();

        // When
        callFlow = CallFlowBuilder.createFrom(callFlowRequest);

        // Then
        assertThat(callFlow.getId(), equalTo(null));
        assertThat(callFlow.getName(), equalTo(callFlowRequest.getName()));
        assertThat(callFlow.getDescription(), equalTo(callFlowRequest.getDescription()));
        assertThat(callFlow.getStatus().name(), equalTo(callFlowRequest.getStatus()));
        assertThat(callFlow.getRaw(), equalTo(callFlowRequest.getRaw()));
    }

}
