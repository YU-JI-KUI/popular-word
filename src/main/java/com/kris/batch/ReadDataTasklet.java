package com.kris.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ReadDataTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var data = new ArrayList<>();
        data.add(new Person("John", "Doe"));
        data.add(new Person("Jane", "Doe"));

        chunkContext.getStepContext().getStepExecution().getExecutionContext().put("data", data);
        return RepeatStatus.FINISHED;
    }
}
