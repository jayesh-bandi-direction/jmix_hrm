package com.company.jmix_hrm.job;

import io.jmix.email.Emailer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EmailQueueJob implements Job {

    private final Emailer emailer;

    public EmailQueueJob(Emailer emailer){
        this.emailer = emailer;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Email Queue Job Executed At " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy' 'HH:mm:ss")));
        emailer.processQueuedEmails();
    }

}
