package com.example.demo.Controller;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.job.EmailJob;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.EmailResponse;

@RestController
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private Scheduler scheduler;

	
	@GetMapping("get")
	public String getMethod()
	{
		return "API has passed the test"; 
	}
	
	
	@PostMapping(path="/schedule/email", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<EmailResponse> scheduleEmail( @RequestBody EmailRequest emailRequest) {
		try {

			ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
			if (dateTime.isBefore(ZonedDateTime.now())) {
				EmailResponse EmailResponse = new EmailResponse(false, "dateTime must be after current time");
				return ResponseEntity.badRequest().body(EmailResponse);
			}

			 JobDetail jobDetail = buildJobDetail(emailRequest);
	            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
	            scheduler.scheduleJob(jobDetail, trigger);
			
	            EmailResponse scheduleEmailResponse = new EmailResponse(true,
	                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
	            return ResponseEntity.ok(scheduleEmailResponse);
			
			
		} catch (SchedulerException ex) {
			logger.error("Error scheduling email", ex);

			EmailResponse emailResponse = new EmailResponse(false, "ERROR while scheduling email ...pls try later");

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);

		}

	}

	/// FUNCTION TO BUILD JOB DETAIL

	private JobDetail buildJobDetail(EmailRequest scheduleEmailRequest) {

		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("email", scheduleEmailRequest.getEmail());
		jobDataMap.put("subject", scheduleEmailRequest.getSubject());
		jobDataMap.put("body", scheduleEmailRequest.getBody());

		return JobBuilder.newJob(EmailJob.class)
				.withIdentity(UUID.randomUUID().toString(), "email-jobs")
				.withDescription("Send Email Jobs")
				.usingJobData(jobDataMap)
				.storeDurably()
				.build();

	}

	/// FUNCTION TO BUILD TRIGGER

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "email-triggers").withDescription("Send Email Trigger")
				.startAt(Date.from(startAt.toInstant()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
	}

}
