package com.kris.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

@Component
public class ProcessDataTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        var data = (List<Person>) chunkContext.getStepContext().getStepExecution().getExecutionContext().get("data");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (Person person : data) {
                writer.write(person.getFirstName() + " " + person.getLastName());
                writer.newLine();
            }
        }

        // 将文件路径存入 ExecutionContext 以供后续步骤使用
        chunkContext.getStepContext().getStepExecution().getExecutionContext().put("filePath", "output.txt");
        return RepeatStatus.FINISHED;
    }
}
