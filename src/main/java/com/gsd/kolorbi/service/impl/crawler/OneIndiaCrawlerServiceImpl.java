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
public class OneIndiaCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.oneindia.com/india/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("li.clearfix");
        for (Element ncontent : articleRows) {
            if (!ncontent.hasClass("mid-outbrain")) {
                News news = new News();
                String sourceURL = "https://www.oneindia.com" + ncontent.select("div.cityblock-title.news-desc a").attr("href");
                Document newsDocument = Jsoup.connect(sourceURL).get();
                Elements entryContent = newsDocument.getElementsByClass("oi-article-lt");
                String newsImageURL = getNewsImageURL(newsDocument);
                List<String> newsContent = getNewsContents(entryContent);
                Elements categories = newsDocument.select("div.breadcrump.clearfix a");
                String subject = ncontent.select("div.cityblock-title.news-desc a").text();
                for (int i = 1; i < categories.size(); i++) {
                    String category = categories.get(i).text();
                    news.setCategory(category);
                }
                news.setSource("oneindia.com");
                news.setCountry("NG");
                news.setSourceLogoURL("https://www.oneindia.com/images/oneindia-logo.svg");
                news.setSourceURL(sourceURL);
                news.setNewsImageURL(newsImageURL);
                news.setSubject(subject);
                news.setCrawledDate(new Date());
                news.setContents(newsContent);
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

    public List<String> getNewsContents(Elements newsContent) throws IOException {
        List<String> contents = new ArrayList<>();
        for(Element ncontainer:newsContent){
            for(Element ncontent:ncontainer.children()){
                if(ncontent.select("p") != null ){
//                    if(!ncontent.select("p").attr("class").toString().equals("appstext")){
                        contents.add(ncontent.select("p").text());
//                    };
                }
            }
        }

        return contents;
    }
}