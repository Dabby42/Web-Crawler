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
public class CNNNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://edition.cnn.com";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("div.zn__containers");
        System.out.println(articleRows);
        for (int i = 1; i < articleRows.size(); i++) {
            Element title = articleRows.get(i);
//            String t = title.select("h2.cn__title").text();

        }
//        div.pg-no-rail.pg-wrapper.pg__background__image
//        System.out.println(articleRows.outerHtml());
//        for (int i = articleRows.size() - 1; i >= 0; i--) {
//            News news = new News();
//            Element articleBlock = articleRows.get(i);
//            Elements subjectBlock = articleBlock.select("div.l-container.zn__background--content-relative");
////            Elements subjectContainer = subjectBlock.select("div.zn__containers");
////            Elements subjectContainer = subjectBlock.select("h3.cd__headline");
//            System.out.println(subjectContainer);
////
//            String category = articleBlock.select("h2.cn__title a").text();
//            String sourceURL = "https://edition.cnn.com" + articleBlock.select("h3.cd__headline a").attr("href");
//            Document newsDocument = Jsoup.connect(sourceURL).get();
//            String newsImageURL = getNewsImageURL(newsDocument);
//            List<String> newsContent = getNewsContents(newsDocument);
//            news.setCategory(category);
//            news.setSourceURL(sourceURL);
////            news.setSubject(subject);
//            news.setSource("cnn.com");
//            news.setCountry("NG");
//            news.setSourceLogoURL("https://cdn.cnn.com/cnn/.e/img/3.0/global/misc/apple-touch-icon.png");
//            news.setCrawledDate(new Date());
//            news.setContents(newsContent);
//            news.setNewsImageURL(newsImageURL);
//            newss.add(news);
//            newsService.saveNews(news);
//        }
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

        Elements newsBlock = newsDocument.select("div.l-container");

        for(Element ncontainer:newsBlock){
            for(Element ncontent:ncontainer.children()){
                if(ncontent.select("p") != null){
//                System.out.println(ncontent.select("p").text().trim() +" "+ncontent.select("p").text().trim().length());
                    if(ncontent.select("p").text().trim().length() > 0){
                        contents.add(ncontent.select("p").text());
                        contents.add("");
                    }

                }
            }

        }

        return contents;


    }
}