package com.gsd.kolorbi.service.impl.crawler;

import com.gsd.kolorbi.enums.NewsCategory;
import com.gsd.kolorbi.model.News;
import com.gsd.kolorbi.service.NewsCrawlerService;
import com.gsd.kolorbi.service.NewsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VanguardNewsCrawlerServiceImpl  implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.vanguardngr.com/category/trending/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("article");
//        System.out.println(articleRows.outerHtml());

        for (int i = articleRows.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleRows.get(i);

            news.setSource("vanguardngr.com");
            news.setCountry("NG");
            news.setSourceLogoURL("https://cdn.vanguardngr.com/wp-content/uploads/2016/06/vanguardlogo.png");
            news.setSourceURL(articleBlock.select("h2[class=entry-title] a").attr("href"));
            news.setNewsImageURL(articleBlock.select("a[class=rtp-thumb]").get(0).select("img").get(0).attr("data-lazy-src"));
            news.setSubject(articleBlock.select("h2[class=entry-title] a").html());
            news.setCrawledDate(new Date());
            news.setCategory(getCategory(articleBlock).toUpperCase());
            news.setContents(getNewsContents(news.getSourceURL()));
            newss.add(news);
            newsService.saveNews(news);
        }
    }

    private String getCategory(Element element) {
        Elements categories = element.select("a[rel=category tag]");
        String category = categories.get(0).html();
        if (category.equalsIgnoreCase("News") && categories.size() > 0 && !categories.get(1).html().equalsIgnoreCase("Trending")) {
            category = categories.get(1).html();
        }

        switch (category.toLowerCase()) {
            case "celebrity":
                return NewsCategory.CELEBRITY.name();
            case "politics":
                return NewsCategory.POLITICS.name();
            case "sports":
                return NewsCategory.SPORTS.name();
            case "comedy":
                return NewsCategory.COMEDY.name();
            default:
                return category;
        }

    }

    public List<String> getNewsContents(String newsURL) throws IOException {
        List<String> contents = new ArrayList<>();
        Document newsDocument = Jsoup.connect(newsURL).get();

        Element newsBlock = newsDocument.select("article").get(0).selectFirst("div.entry-content");

        for(Element ncontent:newsBlock.children()){
            if(ncontent.select("p") != null){
                contents.add(ncontent.select("p").text());
                contents.add("");
            }


        }

        return contents;


    }
}