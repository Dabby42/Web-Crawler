package com.gsd.kolorbi.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

@Configuration
public class AWSConfig {

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Value("${amazon.sqs.endpoint}")
    private String amazonSqsEndpoint;

    @Bean
    @Primary
    public AmazonSQSAsyncClient amazonSQSAsyncClient() {

        AmazonSQSAsyncClient amazonSQSAsyncClient = new AmazonSQSAsyncClient(awsCredentials());
        if (!StringUtils.isEmpty(amazonSqsEndpoint)) {
            amazonSQSAsyncClient.setEndpoint(amazonSqsEndpoint);
        }

        return amazonSQSAsyncClient;

    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }

}
