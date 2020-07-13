package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.service.SMSService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class SMSServiceImpl implements SMSService {

    @Qualifier("threadPoolTaskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void sendSMS(String phone, String message) {

        taskExecutor.execute(() -> {
            String url = "https://api.smartsmssolutions.com/smsapi.php?username=ezenwakakelechi@yahoo.com" +
                    "&password=password&sender=KOLORBI&recipient=" + phone + "&message=" + message;
            //System.out.println(url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();


                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
