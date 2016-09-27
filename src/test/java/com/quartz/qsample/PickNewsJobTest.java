package com.quartz.qsample;

import java.text.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.quartz.qsample.service.PickNewsJobService;

/**
 * @author songyu.li@yeepay.com
 * @createDatetime 2016年9月20日 下午2:41:21
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
public class PickNewsJobTest {

	@Autowired
	private PickNewsJobService pickNewsJobService;
	
	
	@Test
    public void testJobForSimpleTrigger() throws SchedulerException {
		pickNewsJobService.simpleTriggerQrtz();
	}
	
	@Test
	public void testJobForCronTrigger() throws SchedulerException{
		pickNewsJobService.cronTriggerQrtz();
	}
	
	@Test
	public void testUpdateTrigger() throws SchedulerException{
		pickNewsJobService.upDateTrigger();
	}
	
	@Test
	public void testTimerTrigger() throws SchedulerException, ParseException{
		pickNewsJobService.timerTriggerQrtz();
	}
}
