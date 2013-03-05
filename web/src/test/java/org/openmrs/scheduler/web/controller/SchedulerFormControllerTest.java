/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.scheduler.web.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.Task;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.Verifies;
import org.openmrs.web.test.BaseWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests for the SchedulerFormController
 */
public class SchedulerFormControllerTest extends BaseWebContextSensitiveTest {
	
	private static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
	
	private static final String INITIAL_SCHEDULER_TASK_CONFIG_XML = "org/openmrs/web/include/SchedulerFormControllerTest.xml";
	
	private static final long MAX_WAIT_TIME_IN_MILLISECONDS = 2048;
	
	private MockHttpServletRequest mockRequest;
	
	private TaskHelper taskHelper;
	
	@Autowired
	private SchedulerFormController controller;
	
	// should be @Autowired as well but the respective bean is commented out
	// in applicationContext-service.xml at the time of coding (Jan 2013)
	private SchedulerService service;
	
	@Before
	public void setUpSchedulerService() throws Exception {
		executeDataSet(INITIAL_SCHEDULER_TASK_CONFIG_XML);
		
		service = Context.getSchedulerService();
		taskHelper = new TaskHelper(service);
		
		mockRequest = new MockHttpServletRequest();
		mockRequest.setMethod("POST");
		mockRequest.setParameter("action", "");
		mockRequest.setParameter("taskId", "1");
	}
	
	/**
	 * @see {@link SchedulerFormController#onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)}
	 */
	@Test
	@Verifies(value = "should reschedule a currently scheduled task", method = "onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)")
	public void onSubmit_shouldRescheduleACurrentlyScheduledTask() throws Exception {
		Date timeOne = taskHelper.getTime(Calendar.MINUTE, 5);
		TaskDefinition task = taskHelper.getScheduledTaskDefinition(timeOne);
		Task oldTaskInstance = task.getTaskInstance();
		
		Date timeTwo = taskHelper.getTime(Calendar.MINUTE, 2);
		mockRequest.setParameter("startTime", new SimpleDateFormat(DATE_TIME_FORMAT).format(timeTwo));
		
		ModelAndView mav = controller.handleRequest(mockRequest, new MockHttpServletResponse());
		assertNotNull(mav);
		assertTrue(mav.getModel().isEmpty());
		
		Assert.assertNotSame(oldTaskInstance, task.getTaskInstance());
	}
	
	/**
	 * @see {@link SchedulerFormController#onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)}
	 */
	@Test
	@Verifies(value = "should not reschedule a task that is not currently scheduled", method = "onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)")
	public void onSubmit_shouldNotRescheduleATaskThatIsNotCurrentlyScheduled() throws Exception {
		Date timeOne = taskHelper.getTime(Calendar.MINUTE, 5);
		TaskDefinition task = taskHelper.getUnscheduledTaskDefinition(timeOne);
		Task oldTaskInstance = task.getTaskInstance();
		
		Date timeTwo = taskHelper.getTime(Calendar.MINUTE, 2);
		mockRequest.setParameter("startTime", new SimpleDateFormat(DATE_TIME_FORMAT).format(timeTwo));
		
		ModelAndView mav = controller.handleRequest(mockRequest, new MockHttpServletResponse());
		assertNotNull(mav);
		assertTrue(mav.getModel().isEmpty());
		
		Assert.assertSame(oldTaskInstance, task.getTaskInstance());
	}
	
	/**
	 * @see {@link SchedulerFormController#onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)}
	 */
	@Test
	@Verifies(value = "should not reschedule a task if the start time has passed", method = "onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)")
	public void onSubmit_shouldNotRescheduleATaskIfTheStartTimeHasPassed() throws Exception {
		Date timeOne = taskHelper.getTime(Calendar.MINUTE, 5);
		TaskDefinition task = taskHelper.getScheduledTaskDefinition(timeOne);
		Task oldTaskInstance = task.getTaskInstance();
		
		Date timeTwo = taskHelper.getTime(Calendar.SECOND, -1);
		mockRequest.setParameter("startTime", new SimpleDateFormat(DATE_TIME_FORMAT).format(timeTwo));
		
		ModelAndView mav = controller.handleRequest(mockRequest, new MockHttpServletResponse());
		assertNotNull(mav);
		assertTrue(mav.getModel().isEmpty());
		
		Assert.assertSame(oldTaskInstance, task.getTaskInstance());
	}
	
	/**
	 * @see {@link SchedulerFormController#onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)}
	 */
	@Test
	@Verifies(value = "should not reschedule an executing task", method = "onSubmit(HttpServletRequest,HttpServletResponse,Object,BindException)")
	public void onSubmit_shouldNotRescheduleAnExecutingTask() throws Exception {
		Date startTime = taskHelper.getTime(Calendar.SECOND, 1);
		TaskDefinition task = taskHelper.getScheduledTaskDefinition(startTime);
		
		taskHelper.waitUntilTaskIsExecuting(task, MAX_WAIT_TIME_IN_MILLISECONDS);
		Task oldTaskInstance = task.getTaskInstance();
		
		// use the *same* start time as in the task already running
		mockRequest.setParameter("startTime", new SimpleDateFormat(DATE_TIME_FORMAT).format(startTime));
		
		ModelAndView mav = controller.handleRequest(mockRequest, new MockHttpServletResponse());
		assertNotNull(mav);
		assertTrue(mav.getModel().isEmpty());
		
		Assert.assertSame(oldTaskInstance, task.getTaskInstance());
	}
	
}
