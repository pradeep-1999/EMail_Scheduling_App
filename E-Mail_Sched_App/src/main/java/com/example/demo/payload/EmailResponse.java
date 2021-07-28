package com.example.demo.payload;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailResponse {

	private boolean success;

	private String jobId;

	private String jobGroup;

	private String message;

	// Constructor-1
	public EmailResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	// Constructor-2
	public EmailResponse(boolean success, String jobId, String jobGroup, String message) {
		this.success = success;
		this.jobId = jobId;
		this.jobGroup = jobGroup;
		this.message = message;

	}

}
