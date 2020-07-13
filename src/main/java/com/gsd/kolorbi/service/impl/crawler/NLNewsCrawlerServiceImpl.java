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
public class NLNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.naijaloaded.com.ng/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleBlocks = homeDocument.select("div[class=trending-wrapper-outer]").select("article");

        for(int i = articleBlocks.size()-1; i >= 0; i--){
            News news = new News();
            Element articleBlock = articleBlocks.get(i);

            news.setSource("naijaloaded.com");
            news.setCountry("NG");
            news.setSourceURL(articleBlock.select("header h2 a").attr("href"));
            news.setSubject(articleBlock.select("header h2 a").text());
            news.setCrawledDate(new Date());
            news.setNewsImageURL(articleBlock.selectFirst("img").attr("src"));
            news.setCategory(getCategory(articleBlock.selectFirst("a").html()));
            news.setContents(getNewsContents(news.getSourceURL()));

            newsService.saveNews(news);
        }
    }

    private String getCategory(String s){
        switch(s.toLowerCase()){
            case "music":
                return NewsCategory.MUSIC.name();
            case "entertainment":
                return NewsCategory.CELEBRITY.name();
            case "news":
                return NewsCategory.POLITICS.name();
            case "sports":
                return NewsCategory.SPORTS.name();
            case "comedy":
                return NewsCategory.COMEDY.name();
            case "naija news":
                return NewsCategory.POLITICS.name();
            default:
                return NewsCategory.BLOG.name();
        }
    }

    private List<String> getNewsContents(String newsURL) throws IOException {
        List<String> contents = new ArrayList<>();
        Document newsDocument = Jsoup.connect(newsURL).get();
        Element article = newsDocument.selectFirst("article");
        article.selectFirst("footer").html("");

        for(Element ad:article.select("ins")){
            TextNode textNode = new TextNode("{{K_AD}}");
            ad.replaceWith(textNode);
            ad.attr("data-ad-client","{{_K_AD_CLIENT_}}");
            ad.attr("data-ad-slot","{{_K_AD_SLOT_}}");
        }

        for(Element link:article.select("a.drop-comment")){
            link.html("");
        }

        for(Element link:article.select("div.post-you-may-like")){
            link.html("");
        }

        for(Element link:article.select("a[href]")){
            if(link.attr("href").contains("naijaloaded.com")){
                link.attr("href", "#");
            }
        }
        String content = /*article.selectFirst("header.post-header").html() + */article.selectFirst("div.post-content").html();
        contents.add(content);

        Element commentBlock = article.selectFirst("ol.comment-list");
        contents.add(commentBlock.html());

        return contents;
    }
}