package com.gsd.kolorbi.service;

import com.gsd.kolorbi.model.SQSMessage;

public interface SqsQueueService {

    void send(SQSMessage message) throws Exception;
}
