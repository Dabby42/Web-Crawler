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
public class JANewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://jamilakyari.com/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("article");
        System.out.println(articleRows.outerHtml());

        for (int i = articleRows.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleRows.get(i);

            news.setSource("jamilakyari.com");
            news.setCountry("NG");
            news.setSourceURL(articleBlock.select("h2[class=entry-title p_post_titles_font] a").attr("href"));
            news.setNewsImageURL(articleBlock.select("div[class=floated_summary_left] a").attr("href"));
            news.setSubject(articleBlock.select("h2[class=entry-title p_post_titles_font] a").text());
            news.setCrawledDate(new Date());
            news.setCategory(articleBlock.select("div[class=entry-meta pipdig_meta] a").text());
            news.setContents(getNewsContents(news.getSourceURL()));
            newss.add(news);
            newsService.saveNews(news);
        }
    }

    public List<String> getNewsContents(String newsURL) throws IOException {
        List<String> contents = new ArrayList<>();
        Document newsDocument = Jsoup.connect(newsURL).get();

        Element newsBlock = newsDocument.select("article").get(0).selectFirst("div.clearfix.entry-content");

        for(Element ncontent:newsBlock.children()){
            if(ncontent.select("p") != null){
                contents.add(ncontent.select("p").text());
                contents.add("");
            }


        }

        return contents;


    }
}