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
public class IndianExpressCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://indianexpress.com/latest-news/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("div.articles");
//        System.out.println(articleRows.outerHtml());

        for (int i = articleRows.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleRows.get(i);
            String sourceURL = articleBlock.select("div[class=snaps] a").attr("href");
            Document newsDocument = Jsoup.connect(sourceURL).get();
            Elements entryContent = newsDocument.getElementsByClass("full-details");
            String newsImageURL = getNewsImageURL(newsDocument);
            List<String> newsContent = getNewsContents(entryContent);
            Elements categoryRows = newsDocument.select("ol[class=m-breadcrumb] li");
            for (int j = 1; j <= categoryRows.size() - 2; j++) {
                Element categoryBlock = categoryRows.get(j);
                String category = categoryBlock.select("a").text();
                news.setCategory(category);
            }
            news.setSource("indianexpress.com");
            news.setCountry("NG");
            news.setSourceLogoURL("https://images.indianexpress.com/2020/05/iPhone-SE-2020-fb.jpg");
            news.setSourceURL(sourceURL);
            news.setNewsImageURL(newsImageURL);
            news.setSubject(newsDocument.select("div[class=heading-part] h1").text());
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
                    if(!ncontent.select("p").attr("class").toString().equals("appstext")){
                        contents.add(ncontent.select("p").text());
                    };
                }
            }
        }

        return contents;
    }
}