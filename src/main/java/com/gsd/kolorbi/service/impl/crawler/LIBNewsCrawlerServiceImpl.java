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
            news.setNewsImageURL(articleBlock.select("figure div img").attr("src"));
            news.setCategory(NewsCategory.BLOG.name());
            news.setContents(getNewsContents(news.getSourceURL()));

            newsService.saveNews(news);
        }
    }

    private List<String> getNewsContents(String newsURL) throws IOException {
        List<String> contents = new ArrayList<>();
        Document newsDocument = Jsoup.connect(newsURL).get();
        for(Element ad:newsDocument.select("ins")){
            TextNode textNode = new TextNode("{{K_AD}}");
            ad.replaceWith(textNode);
            /*ad.attr("data-ad-client","{{_K_AD_CLIENT_}}");
            ad.attr("data-ad-slot","{{_K_AD_SLOT_}}");*/
        }
        Element newsBlock = newsDocument.select("article.big_story summary").get(0);
        String content = newsBlock.html().replaceAll("src=\"//www.instagram.com/embed.js\"", "src=\"https://www.instagram.com/embed.js\"");
        contents.add(content);

        Element commentBlock = newsDocument.select("div.comments_area").get(0);
        for(Element img:commentBlock.select("img")){
            img.attr("style", "width:40px");
        }
        for(Element commentArea:commentBlock.select("div.comment_field_area")){
            commentArea.html("");
        }

        contents.add(commentBlock.html());

        return contents;
    }
}
