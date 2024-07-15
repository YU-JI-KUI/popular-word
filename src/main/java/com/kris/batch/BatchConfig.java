package com.kris.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final JobRepository jobRepository;

    public BatchConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public Job job(ReadDataTasklet readDataTasklet, ProcessDataTasklet processDataTasklet, WriteDataTasklet writeDataTasklet) {
        return new JobBuilder("job1", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1(readDataTasklet))
                .next(step2(processDataTasklet))
                .next(step3(writeDataTasklet))
                .build();
    }

    @Bean
    public Step step1(ReadDataTasklet readDataTasklet) {
        return new StepBuilder("job1-step1", jobRepository).tasklet(readDataTasklet, transactionManager()).build();
    }

    @Bean
    public Step step2(ProcessDataTasklet processDataTasklet) {
        return new StepBuilder("job1-step2", jobRepository).tasklet(processDataTasklet, transactionManager()).build();
    }

    @Bean
    public Step step3(WriteDataTasklet writeDataTasklet) {
        return new StepBuilder("job1-step3", jobRepository).tasklet(writeDataTasklet, transactionManager()).build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        // 这里配置你的事务管理器
        return new ResourcelessTransactionManager();
    }

}
