package com.gsd.kolorbi;

import com.gsd.kolorbi.scheduler.Scheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;



@PropertySource(name = "myProperties", value = "application.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KolorbiApplication.class)


public class KolorbiApplicationTests {

    @Autowired
    Scheduler scheduler;

    @Test
    public void contextLoads() {
        try {
            scheduler.newsCrawlSchedule();
        } catch (Exception e) {
            e.printStackTrace();
        }

 }

}
