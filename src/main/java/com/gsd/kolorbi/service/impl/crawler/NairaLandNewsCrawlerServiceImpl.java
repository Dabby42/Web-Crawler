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
public class NairaLandNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.nairaland.com/trending";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("table").get(1).select("tr");

        for(int i = articleRows.size()-1; i >= 0; i--){
            News news = new News();
            Element articleBlock = articleRows.get(i);

            news.setSource("nairaland.com");
            news.setCountry("NG");
            news.setSourceURL("https://www." + news.getSource() +articleBlock.select("td b").get(1).child(0).attr("href"));
            news.setSubject(articleBlock.select("td b").get(1).child(0).html());
            news.setCrawledDate(new Date());
            news.setCategory(getCategory(articleBlock.select("td b").get(0).child(0).attr("href").replace("/", "")));


            Document newsDocument = Jsoup.connect(news.getSourceURL()).get();
            news.setContents(getNewsContents(newsDocument));
            news.setNewsImageURL(getNewsImageURL(newsDocument));
            newss.add(news);
            newsService.saveNews(news);
        }
    }

    private String getNewsImageURL(Document newsDocument){
        return newsDocument.select("table[summary=posts]").get(0).select("img").get(0).attr("src");
    }

    private String getCategory(String s){
        switch(s){
            case "celebs":
                return NewsCategory.CELEBRITY.name();
            case "politics":
                return NewsCategory.POLITICS.name();
            case "sports":
                return NewsCategory.SPORTS.name();
            case "jokes":
                return NewsCategory.COMEDY.name();
            default:
                try{
                    return NewsCategory.valueOf(s).name();
                }catch(IllegalArgumentException e){
                    return s;
                }
        }
    }

    public List<String> getNewsContents(Document newsDocument) throws IOException {
        List<String> contents = new ArrayList<>();

        Element newsBlock = newsDocument.select("table[summary=posts]").get(0);

        Elements nrLinks = newsBlock.select("td[class=bold l pu]");
        for(Element nrLink:nrLinks){
            for(Element link:nrLink.select("a[href]")){
                link.attr("href", "#");
            }
        }
        for(Element child:newsBlock.children()) {
            contents.add(child.html());
        }
        //System.out.println(newsBlock.html());

        return contents;
    }
}