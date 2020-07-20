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
public class news24NewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.news24.com/sport";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
//        System.out.println(homeDocument.outerHtml());

        Elements articleRows = homeDocument.select("div.article-list--container");
        for (Element row : articleRows) {

            Elements items = row.children();
            String category = items.first().text();


            for (Element item : items) {

                if (item.hasClass("article-list article-list--standard") || item.hasClass("article-list article-list--feature")) {
                    Elements itm = item.children();
                    for (int i = 0; i < itm.size() ; i++) {

                        String sourceURL = "https://www.news24.com" + itm.get(i).select("a").attr("href");
                        System.out.println(sourceURL);

                        Document newsDocument = Jsoup.connect(sourceURL).get();
                        Elements titleHeader = newsDocument.select("div.article.tf-lhs-col");
                        Elements entryContent = newsDocument.getElementsByClass("article__body");
                        String subject = titleHeader.select("h1.article__title").text();
                        String newsImageURL = getNewsImageURL(newsDocument);
                        List<String> content = getNewsContents(entryContent);

                        News news = new News();
                        news.setSource("news24.com");
                        news.setCountry("NG");
                        news.setSourceURL(sourceURL);
                        news.setSubject(subject);
                        news.setSourceLogoURL("https://cdn.24.co.za/files/Cms/General/d/10142/b33ddf2fb6224fe9b22ddfa9ac4202a8.svg");
                        news.setCrawledDate(new Date());
                        news.setCategory(category);
                        news.setContents(content);
                        news.setNewsImageURL(newsImageURL);
                        newss.add(news);
                        newsService.saveNews(news);


                    }



                }

            }

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
    public List<String> getNewsContents(Elements newsContent) throws IOException {
        List<String> contents = new ArrayList<>();
    //    List<String> filteredContents = new ArrayList<>();

        for(Element ncontent:newsContent){
            if(!ncontent.select("p").text().equals(null)){

                contents.add(ncontent.text());
            }

        }

        return contents;
    }

}