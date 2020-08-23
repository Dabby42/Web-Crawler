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
public class LIBNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.lindaikejisblog.com";

    public static void main(String[] a){
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void crawlWebsiteForNews() throws Exception {
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleBlocks = homeDocument.select("article.story_block");

        for(int i = articleBlocks.size()-1; i >= 0; i--){
            News news = new News();
            Element articleBlock = articleBlocks.get(i);

            news.setSource(source.replaceAll("https://www.", ""));
            news.setCountry("NG");
            news.setSourceURL(articleBlock.select("figure h1 a").attr("href"));
            news.setSubject(articleBlock.select("figure h1 a").text());
            news.setCrawledDate(new Date());
            news.setSourceLogoURL("https://www.lindaikejisblog.com/img/favicon.png");
            news.setCategory(NewsCategory.BLOG.name());
            Document newsDocument = Jsoup.connect(news.getSourceURL()).get();
            news.setNewsImageURL(getNewsImageURL(newsDocument));

            news.setContents(getNewsContents(newsDocument));

            newsService.saveNews(news);
        }
    }

    private String getNewsImageURL(Document newsDocument) throws IOException {
        String url = "";
        for(Element ad:newsDocument.select("summary p img")){
            url = ad.attr("src");
            return ad.attr("src");
        }

        return url;
    }

    private List<String> getNewsContents(Document newsDocument) throws IOException {
        List<String> contents = new ArrayList<>();
        for(Element ad:newsDocument.select("summary")){
            for(Element news:ad.children()){

                if(news.select("p") != null){
                    if(news.select("p").text().length() > 0){
                        contents.add(news.select("p").text());
                        contents.add("");
                    }

                }
            }
        }

        return contents;
    }
}
