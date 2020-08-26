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
            String sourceURL = articleBlock.select("h2[class=entry-title p_post_titles_font] a").attr("href");
            Document newsDocument = Jsoup.connect(sourceURL).get();
            String newsImageURL = getNewsImageURL(newsDocument);
            List<String> newsContent = getNewsContents(newsDocument);

            news.setSource("jamilakyari.com");
            news.setCountry("NG");
            news.setSourceURL(sourceURL);
            news.setNewsImageURL(newsImageURL);
            news.setSourceLogoURL("https://jamilakyari.com/wp-content/uploads/2019/06/cropped-JK-11.24.04-PM-32x32.png");
            news.setSubject(articleBlock.select("h2[class=entry-title p_post_titles_font] a").text());
            news.setCrawledDate(new Date());
            news.setCategory(articleBlock.select("div[class=entry-meta pipdig_meta] a").text());
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