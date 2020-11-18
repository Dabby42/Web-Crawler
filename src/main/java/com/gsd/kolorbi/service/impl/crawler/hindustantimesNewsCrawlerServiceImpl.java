package com.gsd.kolorbi.service.impl.crawler;

import com.gsd.kolorbi.model.News;
import com.gsd.kolorbi.service.NewsCrawlerService;
import com.gsd.kolorbi.service.NewsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class hindustantimesNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.hindustantimes.com/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("div.row.pb-20");
        for (Element ncontainer : articleRows) {
            for (Element ncontent : ncontainer.children()) {
                News news = new News();
                Elements categoryRows = ncontent.select("div.new-h2-head a");
                for (int j = 0; j < categoryRows.size(); j++) {
                    Element categoryBlock = categoryRows.get(j);
                    String category = categoryBlock.text();
                    news.setCategory(category);
                }
                Elements sourceRows = ncontent.select("div.random-tx.clearfix");
                for (int j = 0; j < sourceRows.size(); j++) {
                    Element sourceBlock = sourceRows.get(j);
                    Elements sourceURLS = sourceBlock.select("div.para-txt a");;
                    for (int k = 0; k < sourceURLS.size() ; k++) {
                        String sourceURL = sourceURLS.get(k).attr("href");
                        Document newsDocument = Jsoup.connect(sourceURL).get();
                        String newsImageURL = getNewsImageURL(newsDocument);
                        List<String> newsContent = getNewsContents(newsDocument);
                        news.setSourceURL(sourceURL);
                        news.setNewsImageURL(newsImageURL);
                        news.setContents(newsContent);
                    }
                    String subject = sourceBlock.select("div.para-txt a").text();
                    news.setSubject(subject);
                }

                news.setSource("hindustantimes.com");
                news.setCountry("NG");
                news.setSourceLogoURL("https://www.hindustantimes.com/images/app-images/ht2020/sticky-1.png");
                news.setCrawledDate(new Date());

                newss.add(news);
                newsService.saveNews(news);
            }
        }
    }

    private String getNewsImageURL(Document newsDocument){
        Elements meta = newsDocument.select("meta");
        String link = "";
        for(int l = 0; l < meta.size(); l++){
            if(meta.get(l).attr("property").toString().equals("og:image")){
                link = meta.get(l).attr("content");
            }

        }
        return link;

    }

    public List<String> getNewsContents(Document newsDocument) throws IOException {
        List<String> contents = new ArrayList<>();

        Elements newsBlock = newsDocument.select("div.storyDetail");
        for(Element ncontainer:newsBlock){
            for(Element ncontent:ncontainer.children()){
                if(ncontent.select("p") != null){
//                System.out.println(ncontent.select("p").text().trim() +" "+ncontent.select("p").text().trim().length());
                    if(ncontent.select("p").text().trim().length() > 0){
                        contents.add(ncontent.select("p").text());
                        contents.add("");
                    }

                }
            }

        }

        return contents;


    }
}