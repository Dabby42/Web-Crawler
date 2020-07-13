package com.gsd.kolorbi.service.impl.crawler;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.gsd.kolorbi.enums.NewsCategory;
import com.gsd.kolorbi.model.News;
import com.gsd.kolorbi.service.NewsCrawlerService;
import com.gsd.kolorbi.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Service
public class YouTubeNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    @Value("${google.api.key}")
    private String GOOGLE_API_KEY;

    YouTube youtube;


    @Override
    public void crawlWebsiteForNews() throws Exception {
        youtube= new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                request -> {}).setApplicationName("Kolorbi").build();

        getFeedsFromYoutube("");
        getFeedsFromYoutube("comedy");
        getFeedsFromYoutube("politics");

    }

    private void getFeedsFromYoutube(@NotNull String keyword){

        try {

            YouTube.Search.List searchListByKeywordRequest = youtube.search()
                    .list("snippet")
                    .setKey(GOOGLE_API_KEY)
                    .setPart("snippet")
                    .setQ(keyword)
                    .setLocation("9.0820, 8.6753")
                    .setLocationRadius("5km")
                    .setMaxResults(10l)
                    .setType("video");


            SearchListResponse response = searchListByKeywordRequest.execute();
            for(SearchResult searchResult:response.getItems()){
                if (searchResult.getId().getKind().equalsIgnoreCase("youtube#video")) {
                    News news = new News();
                    news.setCategory(getCategory(keyword.toLowerCase()));
                    news.setSource("youtube.com");
                    news.setNewsImageURL(searchResult.getSnippet().getThumbnails().getDefault().getUrl());
                    news.setCrawledDate(new Date());
                    news.setSourceURL("https://www.youtube.com/embed/"+searchResult.getId().getVideoId());
                    news.setSubject(searchResult.getSnippet().getDescription());
                    news.setCountry("NG");


                    newsService.saveNews(news);
                }
                //System.out.println(searchResult);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getCategory(String s){
        switch(s){
            case "politics":
                return NewsCategory.POLITICS.name();
            case "sports":
                return NewsCategory.SPORTS.name();
            case "comedy":
                return NewsCategory.COMEDY.name();
            default:
                return NewsCategory.YOUTUBE.name();
        }
    }

}
