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
public class NairametricsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://nairametrics.com/category/nigeria-business-news/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("li.mvp-blog-story-wrap.left.relative.infinite-post");
        for (int i = articleRows.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleRows.get(i);
            String sourceURL = articleBlock.select("a").attr("href");
            ;
            Document newsDocument = Jsoup.connect(sourceURL).get();
            String newsImageURL = getNewsImageURL(newsDocument);
            List<String> newsContent = getNewsContents(newsDocument);

            news.setSource("nairametrics.com");
            news.setCountry("NG");
            news.setSourceLogoURL("https://i2.wp.com/nairametrics.com/wp-content/uploads/2020/04/cropped-favicon-1.png?fit=32%2C32&ssl=1");
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

        Elements newsBlock = newsDocument.select("div.theiaPostSlider_preloadedSlide").get(0).children();

        for(Element ncontent:newsBlock){
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