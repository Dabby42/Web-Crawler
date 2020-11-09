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
        Elements articleRows = homeDocument.select("div#collection-wrapper");

        for (int i = articleRows.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleRows.get(i);
            String sourceURL = "https://www.oneindia.com/india/" + articleBlock.select("h2.collection-heading.news-desc a").attr("href");
            Document newsDocument = Jsoup.connect(sourceURL).get();
            Elements entryContent = newsDocument.getElementsByClass("oi-article-lt");
            String newsImageURL = getNewsImageURL(newsDocument);
            List<String> newsContent = getNewsContents(entryContent);
            String category = newsDocument.select("div.breadcrump.clearfix a").text();
            news.setCategory(category);
            news.setSource("oneindia.com");
            news.setCountry("NG");
            news.setSourceLogoURL("https://www.oneindia.com/images/oneindia-logo.svg");
            news.setSourceURL(sourceURL);
            news.setNewsImageURL(newsImageURL);
            Elements subjects = articleBlock.select("div.collection-container");

            for (int j = 0; j < subjects.size(); j++) {
                String subject = subjects.get(j).text();
//                System.out.println(subject);
                news.setSubject(subject);
            }

            news.setCrawledDate(new Date());
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