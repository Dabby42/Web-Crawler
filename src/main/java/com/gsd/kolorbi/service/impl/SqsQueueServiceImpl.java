package com.gsd.kolorbi.service.impl;

import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsd.kolorbi.model.SQSMessage;
import com.gsd.kolorbi.service.SqsQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

//@Service
public class SqsQueueServiceImpl implements SqsQueueService {

    @Value("${amazon.sqs.queue.name}")
    private String queueName;

    @Autowired
    AmazonSQSAsyncClient amazonSQSAsyncClient;

    @Override
    public void send(SQSMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        amazonSQSAsyncClient.sendMessage(new SendMessageRequest(queueName, objectMapper.writeValueAsString(message)));
    }
}
