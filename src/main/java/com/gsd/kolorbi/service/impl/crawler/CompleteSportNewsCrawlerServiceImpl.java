package com.gsd.kolorbi.service.impl.crawler;

import com.gsd.kolorbi.enums.NewsCategory;
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
public class CompleteSportNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.completesports.com/?s=";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("div.widget-content.feed-widget-content.widget-content-magone-archive-blog-rolls");
        for (Element ncontainer : articleRows) {
            for (Element ncontent : ncontainer.children()) {
//                System.out.println(ncontent);
                if (!ncontent.hasClass("AdCompactSides")){
                    News news = new News();
                    String sourceURL = ncontent.select("a").attr("href");
                    System.out.println(sourceURL);
                    Document newsDocument = Jsoup.connect(sourceURL).get();
                    String newsImageURL = getNewsImageURL(newsDocument);
                    List<String> newsContent = getNewsContents(newsDocument);

                    news.setSource("completesports.com");
                    news.setCountry("NG");
                    news.setSourceLogoURL("https://www.completesports.com/wp-content/uploads/2017/08/CSLogo.png");
                    news.setSourceURL(sourceURL);
                    news.setNewsImageURL(newsImageURL);
                    news.setSubject(ncontent.select("h3.item-title").text());
                    news.setCrawledDate(new Date());
                    news.setCategory("Sport");
                    news.setContents(newsContent);
                    newss.add(news);
                    newsService.saveNews(news);
                }
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

        Element newsBlock = newsDocument.select("div.post-body.entry-content.content-template").get(0).selectFirst("div.post-body-inner");

        for(Element ncontent:newsBlock.children()){
            if(ncontent.select("p") != null){
//                System.out.println(ncontent.select("p").text().trim() +" "+ncontent.select("p").text().trim().length());
                if(ncontent.select("p").text().trim().length() > 0){
                    contents.add(ncontent.select("p").text().trim());
                    contents.add("");
                }

            }
        }

        return contents;


    }
}