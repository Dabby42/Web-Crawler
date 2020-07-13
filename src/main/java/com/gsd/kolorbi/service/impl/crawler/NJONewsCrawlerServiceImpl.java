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
public class NJONewsCrawlerServiceImpl  implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://notjustok.com/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleBlocks = homeDocument.select("section[id=featured-post-2]").get(0).select("article");

        for(int i = articleBlocks.size()-1; i >= 0; i--){
            News news = new News();
            Element articleBlock = articleBlocks.get(i);

            news.setSource("notjustok.com");
            news.setCountry("NG");
            news.setSourceURL(articleBlock.selectFirst("a[href]").attr("href"));
            news.setSubject(articleBlock.selectFirst("header h2").text());
            news.setCrawledDate(new Date());
            news.setNewsImageURL(articleBlock.selectFirst("a img").attr("srcset").split(",")[1].trim().split(" ")[0]);
            news.setCategory(NewsCategory.MUSIC.name());
            news.setContents(getNewsContents(news.getSourceURL()));

            newsService.saveNews(news);
        }
    }

    private List<String> getNewsContents(String newsURL) throws IOException {
        List<String> contents = new ArrayList<>();
        Document newsDocument = Jsoup.connect(newsURL).get();

        Element main = newsDocument.selectFirst("main").selectFirst("article");
        if(main.selectFirst("footer") != null) {
            main.selectFirst("footer").html("");
        }
        if(main.selectFirst("div[id=follow-buttons]") != null) {
            main.selectFirst("div[id=follow-buttons]").html("");
        }
        if(main.selectFirst("div[class=embedded-artists]") != null) {
            main.selectFirst("div[class=embedded-artists]").html("");
        }
        for(Element ad:main.select("div")){
            if(ad.attr("class").contains("code-block")) {
                ad.html("{{K_AD}}");
                //ad.html("<ins class=\"adsbygoogle\" style=\"display:block\" data-ad-client=\"{{_K_AD_CLIENT_}}\" data-ad-slot=\"{{_K_AD_SLOT_}}\" data-ad-format=\"auto\"></ins>");
                //ad.html("{{_KADV_}}");
            }
        }
        for(Element link:main.select("a[href]")){
             if(link.attr("href").contains("notjustok.com")){
                 link.attr("href", "#");
             }
        }

        contents.add(main.selectFirst("div.entry-content").html());

        return contents;
    }
}
