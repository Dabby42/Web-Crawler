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
public class LegitNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.legit.ng/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleBlocks = homeDocument.select("section[data-news-type=Trending news]").select("article");

        for (int i = articleBlocks.size() - 1; i >= 0; i--) {
            News news = new News();
            Element articleBlock = articleBlocks.get(i);

            news.setSource("legit.ng");
            news.setCountry("NG");
            news.setSourceURL(articleBlock.selectFirst("a.c-article-card__headline").attr("href"));
            news.setSubject(articleBlock.select("a.c-article-card__headline span").text());
            news.setCrawledDate(new Date());


            Document newsDocument = Jsoup.connect(news.getSourceURL()).get();
            for (Element img : newsDocument.select("img")) {
                //System.out.println(img.attr("src"));
                if (img.attr("src").contains("netstorage-legit.akamaized.net")) {
                    news.setNewsImageURL(img.attr("src"));
                }
            }
            news.setCategory(getCategory(newsDocument));
            news.setContents(getNewsContents(newsDocument));
            //System.out.println(news);

            newsService.saveNews(news);
        }
    }

    private String getCategory(Document newsDocument) {
        String s = "";
        for(Element label:newsDocument.select("a.c-label-item")){
            s += (label.html()+" ");
        }

        if(s.contains("sports")){
            return NewsCategory.SPORTS.name();
        }else if(s.contains("entertainment")){
            return NewsCategory.CELEBRITY.name();
        }else if(s.contains("local news")){
            return NewsCategory.POLITICS.name();
        }else if(s.contains("politics")){
            return NewsCategory.POLITICS.name();
        }else{
            return NewsCategory.BLOG.name();
        }
    }

    private List<String> getNewsContents(Document newsDocument) throws IOException {
        List<String> contents = new ArrayList<>();
        Element article = newsDocument.selectFirst("div[class=l-article__desktop-left]");

        for (Element ad : article.select("div.adv")) {
            ad.html("{{K_AD}}");
            //ad.html("<ins class=\"adsbygoogle\" style=\"display:block\" data-ad-client=\"{{_K_AD_CLIENT_}}\" data-ad-slot=\"{{_K_AD_SLOT_}}\" data-ad-format=\"auto\"></ins>");
            //ad.html("{{_KADV_}}");
        }

        for (Element link : article.select("a[href]")) {
            if (link.attr("href").contains("legit.ng")) {
                link.attr("href", "#");
            }
        }
        String content = article.selectFirst("h1").outerHtml() + article.selectFirst("div.c-article__body").outerHtml();
        contents.add(content);

        return contents;
    }
}
