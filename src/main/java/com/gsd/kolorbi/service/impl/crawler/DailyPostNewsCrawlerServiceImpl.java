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
public class DailyPostNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://dailypost.ng/entertainment/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("li.mvp-blog-story-wrap.left.relative.infinite-post");
        System.out.println(articleRows.outerHtml());

        for (int i = articleRows.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleRows.get(i);
            String sourceURL = articleBlock.select("a").attr("href");
            Document newsDocument = Jsoup.connect(sourceURL).get();
            String newsImageURL = getNewsImageURL(newsDocument);
            List<String> newsContent = getNewsContents(newsDocument);

            news.setSource("dailypost.ng");
            news.setCountry("NG");
            news.setSourceLogoURL("https://dailypost.ng/wp-content/uploads/2018/03/cropped-DAILY-POST-ICON-32x32.jpg");
            news.setSourceURL(sourceURL);
            news.setNewsImageURL(newsImageURL);
            news.setSubject(articleBlock.select("div[class=mvp-blog-story-text left relative] h2").html());
            news.setCrawledDate(new Date());
            news.setCategory(articleBlock.select("div[class=mvp-cat-date-wrap left relative] span[class=mvp-cd-cat left relative]").text());
            news.setContents(newsContent);
            newss.add(news);
            newsService.saveNews(news);
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

        Element newsBlock = newsDocument.select("div[id=mvp-content-body]").get(0).selectFirst("div#mvp-content-main.left.relative");

        for(Element ncontent:newsBlock.children()){
            if(ncontent.select("p") != null){
//                System.out.println(ncontent.select("p").text().trim() +" "+ncontent.select("p").text().trim().length());
                if(ncontent.select("p").text().trim().length() > 0){
                    contents.add(ncontent.select("p").text());
                    contents.add("");
                }

            }
        }

        return contents;


    }
}