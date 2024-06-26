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

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.types.CallFlowStatus;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.service.impl.CallFlowServiceImpl;
import org.openmrs.module.callflows.api.util.ValidationComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Call Flow Service Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class CallFlowServiceTest extends BaseTest {

    private CallFlow mainFlow;

    private CallFlow badCallFlow;

    private CallFlow existingMainFlow;

    private List<CallFlow> searchedFlows;

    private Flow validFlow;

    @InjectMocks
    private CallFlowService callFlowService = new CallFlowServiceImpl();

    @Mock
    private CallFlowDao callFlowDao;

    @Mock
    private FlowService flowService;

    @Mock
    private ValidationComponent validationComponent;

    @Before
    public void setUp() {
        mainFlow = CallFlowHelper.createMainFlow();
        validFlow = new Flow();
        validFlow.setName(mainFlow.getName());
        validFlow.setNodes(Collections.emptyList());
        badCallFlow = CallFlowHelper.createBadFlow();

        existingMainFlow = CallFlowHelper.createMainFlow();
        existingMainFlow.setId(1);

        searchedFlows = new ArrayList<>();
        searchedFlows.add(CallFlowHelper.createMainFlow());
    }

    @Test
    public void shouldCreateCallFlow() throws CallFlowAlreadyExistsException {
        // Given
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDao.create(callFlowArgumentCaptor.capture())).willReturn(mainFlow);
        doNothing().when(validationComponent).validate(mainFlow);

        // When
        CallFlow createdCallFlow = callFlowService.create(mainFlow);

        // Then
        verify(callFlowDao, times(1)).create(mainFlow);
        assertNotNull(createdCallFlow);
        assertThat(createdCallFlow.getName(), equalTo(mainFlow.getName()));
        assertThat(createdCallFlow.getDescription(), equalTo(mainFlow.getDescription()));
        assertThat(createdCallFlow.getStatus(), equalTo(CallFlowStatus.DRAFT));
        assertThat(createdCallFlow.getRaw(), equalTo(mainFlow.getRaw()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateCallFlowForEmptyRawFlow() throws CallFlowAlreadyExistsException {
        // Given
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDao.create(callFlowArgumentCaptor.capture())).willReturn(mainFlow);
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(mainFlow);

        // When And Then
        callFlowService.create(mainFlow);
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameDoesNotHaveAlphanumericCharacters()
            throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(badCallFlow);
        // Given
        try {
            // When
            CallFlow createdCallFlow = callFlowService.create(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDao);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsNull() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(null);
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(badCallFlow);
        try {
            // When
            CallFlow createdCallFlow = callFlowService.create(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDao);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsBlank() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(StringUtils.EMPTY);
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(badCallFlow);
        try {
            // When
            CallFlow createdCallFlow = callFlowService.create(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDao);
        }
    }

    @Test
    public void shouldThrowCallFlowAlreadyExistsIfCallFlowIsAddedWithDuplicateName()
            throws CallFlowAlreadyExistsException {
        expectException(CallFlowAlreadyExistsException.class);
        // Given
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);
        doThrow(CallFlowAlreadyExistsException.class).when(validationComponent).validate(mainFlow);

        // When
        CallFlow duplicateFlow = callFlowService.create(mainFlow);
    }

    @Test
    public void shouldUpdateCallFlow() throws CallFlowAlreadyExistsException {

        // Given a Main Flow exists
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);
        given(flowService.loadByJson(any(String.class))).willReturn(validFlow);
        doNothing().when(validationComponent).validate(existingMainFlow);

        // And the update on the data service returns the updated callflow
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDao.update(callFlowArgumentCaptor.capture())).willReturn(existingMainFlow);

        // When we try to update it
        existingMainFlow.setDescription(existingMainFlow.getDescription() + Constants.UPDATED);
        CallFlow updatedFlow = callFlowService.update(existingMainFlow);

        // Then
        verify(callFlowDao, times(1)).update(existingMainFlow);
        assertNotNull(updatedFlow);
        assertThat(updatedFlow.getName(), equalTo(existingMainFlow.getName()));
        assertThat(updatedFlow.getDescription(), equalTo(existingMainFlow.getDescription()));
        assertThat(updatedFlow.getStatus(), equalTo(existingMainFlow.getStatus()));
        assertThat(updatedFlow.getRaw(), equalTo(existingMainFlow.getRaw()));
    }

    @Test
    public void shouldUpdateCallFlowToNewName() throws CallFlowAlreadyExistsException {

        // Given a Main Flow exists
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);
        given(flowService.loadByJson(any(String.class))).willReturn(validFlow);
        doNothing().when(validationComponent).validate(existingMainFlow);

        // And the update on the data service returns the updated callflow
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDao.update(callFlowArgumentCaptor.capture())).willReturn(existingMainFlow);
        given(callFlowDao.findById(1)).willReturn(existingMainFlow);

        // When we try to update it
        existingMainFlow.setName(Constants.CALLFLOW_MAIN2);
        CallFlow updatedFlow = callFlowService.update(existingMainFlow);

        // Then
        verify(callFlowDao, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        verify(callFlowDao, times(1)).findById(1);
        verify(callFlowDao, times(1)).update(existingMainFlow);
        assertNotNull(updatedFlow);
        assertThat(updatedFlow.getName(), equalTo(Constants.CALLFLOW_MAIN2));
        assertThat(updatedFlow.getDescription(), equalTo(existingMainFlow.getDescription()));
        assertThat(updatedFlow.getStatus(), equalTo(existingMainFlow.getStatus()));
        assertThat(updatedFlow.getRaw(), equalTo(existingMainFlow.getRaw()));
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsNewButIdIsInvalidDuringUpdate()
            throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);

        // Given a Main Flow exists
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);

        // And the update on the data service returns the updated callflow
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDao.update(callFlowArgumentCaptor.capture())).willReturn(existingMainFlow);
        given(callFlowDao.findById(1)).willReturn(existingMainFlow);

        existingMainFlow.setName(Constants.CALLFLOW_MAIN2);
        existingMainFlow.setId(-1);
        try {
            // When we try to update with a new name but a bad id
            CallFlow updatedFlow = callFlowService.update(existingMainFlow);
        } finally {
            // Then
            verify(callFlowDao, times(1)).findByName(Constants.CALLFLOW_MAIN2);
            verify(callFlowDao, times(1)).findById(-1);
            verify(callFlowDao, never()).update(any(CallFlow.class));
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameDoesNotHaveAlphanumericCharactersDuringUpdate()
            throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given A bad call flow
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(badCallFlow);
        try {
            // When
            CallFlow updatedCallFlow = callFlowService.update(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDao);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsNullDuringUpdate() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(null);
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(badCallFlow);
        try {
            // When
            CallFlow updatedCallFlow = callFlowService.update(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDao);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsBlankDuringUpdate() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(StringUtils.EMPTY);
        doThrow(IllegalArgumentException.class).when(validationComponent).validate(badCallFlow);
        try {
            // When
            CallFlow updatedCallFlow = callFlowService.update(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDao);
        }
    }

    @Test
    public void shouldThrowCallFlowAlreadyExistsIfAnotherCallFlowExistsWithDuplicateNameDuringUpdate()
            throws CallFlowAlreadyExistsException {
        expectException(CallFlowAlreadyExistsException.class);
        // Given
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);
        doThrow(CallFlowAlreadyExistsException.class).when(validationComponent).validate(existingMainFlow);

        // And that we make the bad call flow good and duplicate by just changing the name
        CallFlow duplicateFlow = badCallFlow;
        duplicateFlow.setName(existingMainFlow.getName());

        // When we try to update this duplicate call flow
        CallFlow updatedFlow = callFlowService.update(duplicateFlow);
    }

    @Test
    public void shouldFindCallFlowsForValidSearchTerm() {
        // Given
        given(callFlowDao.findAllByName(Constants.CALLFLOW_MAIN_PREFIX)).willReturn(searchedFlows);

        // When we search for a callflow by a prefix
        List<CallFlow> foundCallflows = callFlowService.findAllByNamePrefix(Constants.CALLFLOW_MAIN_PREFIX);

        // Then
        assertThat(foundCallflows.size(), equalTo(searchedFlows.size()));
        verify(callFlowDao, times(1)).findAllByName(Constants.CALLFLOW_MAIN_PREFIX);
    }

    @Test
    public void shouldReturnEmptyListOfCallFlowsIfInvalidSearchTermIsUsed() {
        // Given
        given(callFlowDao.findAllByName(Constants.CALLFLOW_MAIN_PREFIX)).willReturn(searchedFlows);

        // When we search for a callflow by a invalid prefix
        List<CallFlow> foundCallflows = callFlowService.findAllByNamePrefix(Constants.CALLFLOW_INVALID_PREFIX);

        // Then
        assertThat(foundCallflows.size(), equalTo(0));
        verify(callFlowDao, times(1)).findAllByName(Constants.CALLFLOW_INVALID_PREFIX);
    }

    @Test
    public void shouldFindCallFlowByValidName() {
        // Given
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        // When we search for that flow
        CallFlow returnedFlow = callFlowService.findByName(Constants.CALLFLOW_MAIN);

        // Then
        verify(callFlowDao, times(1)).findByName(Constants.CALLFLOW_MAIN);
        assertNotNull(returnedFlow);
        assertThat(returnedFlow.getName(), equalTo(mainFlow.getName()));
    }

    @Test
    public void shouldThrowIllegalArgumentIfAttemptedToFindCallFlowByInvalidName() {
        expectException(IllegalArgumentException.class);
        // Given
        given(callFlowDao.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        try {
            // When we search for a non existent flow
            CallFlow returnedFlow = callFlowService.findByName(Constants.CALLFLOW_MAIN2);
        } finally {
            // Then
            verify(callFlowDao, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        }
    }

    @Test
    public void shouldDeleteCallFlow() {
        // Given
        given(callFlowDao.findById(1)).willReturn(mainFlow);

        // When
        callFlowService.delete(1);

        // Then
        verify(callFlowDao, times(1)).findById(1);
        verify(callFlowDao, times(1)).delete(mainFlow);
    }

    @Test
    public void shouldThrowIllegalArgumentIfAttemptedToDeleteCallFlowWithInvalidId() {
        expectException(IllegalArgumentException.class);
        // Given
        given(callFlowDao.findById(1)).willReturn(mainFlow);

        try {
            // When
            callFlowService.delete(-1);
        } finally {
            // Then
            verify(callFlowDao, times(1)).findById(-1);
            verify(callFlowDao, never()).delete(any(CallFlow.class));
        }
    }
}
