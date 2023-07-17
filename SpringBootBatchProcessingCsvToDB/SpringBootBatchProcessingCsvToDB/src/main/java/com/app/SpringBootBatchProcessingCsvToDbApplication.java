package com.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class SpringBootBatchProcessingCsvToDbApplication {
	
	private final Logger logger = LoggerFactory.getLogger(SpringBootBatchProcessingCsvToDbApplication.class);

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootBatchProcessingCsvToDbApplication.class, args);
	}

	//@Scheduled(fixedRate = 50000) // fixedDelay = 5000
	@Scheduled(cron="${cronExpression}")
	public String importCsvToDBJob() throws InterruptedException {
		JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(job, jobParameters);
			logger.info("Batch job has been invoked");
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
			logger.error("Failed to invoke batch job", e);
		}
		return "Batch job has been invoked";
	}
}

//	@Scheduled(fixedRate = 50000) // fixedDelay = 5000 D:\DataFolder\
//	public void importCsvToDBJob() throws InterruptedException {
//
//		String filePath = "D:/DataFolder/customer1.csv"; // Specify the file path here D:\DataFolder
//
//		JobParameters jobParameters = new JobParametersBuilder().addString("filePath", filePath)
//				.addLong("startAt", System.currentTimeMillis()).toJobParameters();
//
//		try {
//			jobLauncher.run(job, jobParameters);
//
//			logger.info("Batch job has been invoked");
//		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
//				| JobParametersInvalidException e) {
//			e.printStackTrace();
//			logger.error("Failed to invoke batch job", e);
//		}
//
//	}
//}
