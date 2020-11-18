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
public class BBCNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.bbc.com/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("div.content--block--modules");

        for (Element ncontainer : articleRows) {
            for (Element ncontent : ncontainer.children()) {
                    News news = new News();
//                    Element articleBlock = ncontent;
                    Elements subjectRows = ncontent.select("h3.media__title a");
//                     System.out.println(subjectRows.size());
                    for (int j = 0; j < subjectRows.size(); j++) {
                        Element subjectBlock = subjectRows.get(j);
                        String subject = subjectBlock.text();
                        System.out.println(subject);
                        String sourceURL = subjectBlock.attr("href");
                        if (!sourceURL.startsWith("https")){
                            sourceURL = "https://www.bbc.com" + subjectBlock.attr("href");
                        }
                        Document newsDocument = Jsoup.connect(sourceURL).get();
                        List<String> newsContent = getNewsContents(newsDocument);
                        //  System.out.println(newsContent);
                        String newsImageURL = getNewsImageURL(newsDocument);
                        news.setNewsImageURL(newsImageURL);
                        news.setContents(newsContent);
                        news.setSourceURL(sourceURL);
                        news.setSubject(subject);
                    }

                    Elements categoryRows = ncontent.select("div.media__content a");
                    for (int k = 0; k < categoryRows.size(); k++) {
                        Element categoryBlock = categoryRows.get(k);
                        String category = categoryBlock.text();
                        news.setCategory(category);
//                if (category != "Worklife"){
//                    news.setContents(newsContent);
//                }
                    }
                    news.setSource("bbc.com");
                    news.setCountry("NG");
                    news.setSourceLogoURL("https://static.bbci.co.uk/wwhp/1.146.0/responsive/img/apple-touch/apple-touch-180.jpg");
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

        Elements newsBlock = newsDocument.select("article");
        for(Element ncontainer:newsBlock){
            for(Element ncontent:ncontainer.children()){
                if(ncontent.select("p") != null){
//                System.out.println(ncontent.select("p").text().trim() +" "+ncontent.select("p").text().trim().length());
                    if(ncontent.select("p").text().trim().length() > 0){
//                        System.out.println(ncontent.select("p").text());
                        contents.add(ncontent.select("p").text());
                        contents.add("");
                    }

                }
            }
        }

        return contents;


    }
}