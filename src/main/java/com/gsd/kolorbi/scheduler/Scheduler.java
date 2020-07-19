package com.gsd.kolorbi.scheduler;

import com.gsd.kolorbi.model.DevicePreference;
import com.gsd.kolorbi.model.RafflePlayed;
import com.gsd.kolorbi.repository.RafflePlayedRepository;
import com.gsd.kolorbi.repository.RaffleRepository;
import com.gsd.kolorbi.service.NewsCrawlerService;
import com.gsd.kolorbi.service.RaffleService;
import com.gsd.kolorbi.service.impl.crawler.PunchNewsCrawlerServiceImpl;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    RafflePlayedRepository rafflePlayedRepository;

    @Autowired
    RaffleRepository raffleRepository;

    @Autowired
    RaffleService raffleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private List<? extends NewsCrawlerService> newsCrawlerServices;

    @Scheduled(cron = "0 0/30 8 * * *", zone = "Africa/Lagos")
    public void dailySchedule() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, -23);

        raffleRepository.findAll().forEach(raffle -> {
            List<RafflePlayed> rafflePlayeds
                    = rafflePlayedRepository.findRafflePlayedsDueForDraw(now.getTime(), raffle.getId());
            if (!rafflePlayeds.isEmpty()) {
                try {
                    raffleService.drawRaffle(raffle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Scheduled(cron = "0 */30 * * * *", zone = "Africa/Lagos")
    public void newsCrawlSchedule() {

        for(NewsCrawlerService newsCrawlerService:newsCrawlerServices){
            try {
                System.out.println(newsCrawlerService.getClass().getName());
                if(newsCrawlerService.getClass().getName() == "com.gsd.kolorbi.service.impl.crawler.PunchNewsCrawlerServiceImpl"){
                    newsCrawlerService.crawlWebsiteForNews();
                }
                if(newsCrawlerService.getClass().getName() == "com.gsd.kolorbi.service.impl.crawler.AllAfricaNewsCrawlerServiceImpl"){
                    newsCrawlerService.crawlWebsiteForNews();
                }
                if(newsCrawlerService.getClass().getName() == "com.gsd.kolorbi.service.impl.crawler.TechPointNewsCrawlerServiceImpl"){
                    newsCrawlerService.crawlWebsiteForNews();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Document updateQuery = new Document();
        updateQuery.append("$set", new Document().append("newsIds", null));
        mongoTemplate.getCollection(mongoTemplate.getCollectionName(DevicePreference.class)).updateMany(new Document(), updateQuery);

    }
}
