package com.kris.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WriteDataTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String filePath = (String) chunkContext.getStepContext().getStepExecution().getExecutionContext().get("filePath");


        // 上传文件到 SFTP 服务器
        File file = new File(filePath);
        // 在这里添加上传文件到 SFTP 服务器的逻辑
        System.out.println("Uploading file to SFTP server: " + file.getAbsolutePath());

        return RepeatStatus.FINISHED;
    }
}
