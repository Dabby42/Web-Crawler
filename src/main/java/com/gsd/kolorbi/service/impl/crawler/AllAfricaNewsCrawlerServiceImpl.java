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
public class AllAfricaNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://allafrica.com";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();

        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.getElementsByClass("section generic current-boxes");

        // Gets all the news for the day on the landing page of punch
        for (Element article:articleRows){
            Elements contents = article.child(0).children();
            for (Element row:contents){
                Element column = row.children().first();
                if(column != null && !column.hasClass("column float-right")){
                    Elements features = column.children();
                    if(features.size() > 0){
                        Element feature = features.first();
                        Elements featured = feature.children();
                        String category = "";
                        for (Element newsz:featured){

                            if(newsz.hasClass("row no-gutter items")){
                                Elements items = newsz.children();
                                for (Element item:items){
                                    System.out.println(item.child(0).attr("href"));
                                    System.out.println(item.child(0).attr("title"));
                                    String link = "https://allafrica.com"+item.child(0).attr("href");
                                    Document newsDocument = Jsoup.connect(link).get();
                                    News news = new News();
                                    news.setSource("allafrica.com");
                                    news.setCountry("NG");
                                    news.setSourceURL(link);
                                    news.setSubject(item.child(0).attr("title"));
                                    news.setSourceLogoURL("https://cdn03.allafrica.com/static/images/structure/aa-logo.png");
                                    news.setCrawledDate(new Date());
                                    news.setCategory(category);
                                    news.setContents(getNewsContent(newsDocument));
                                    news.setNewsImageURL(getNewsImageURL(newsDocument));
                                    newss.add(news);
                                    newsService.saveNews(news);

                                }

                            }else{
                                if(newsz.hasClass("row")){
                                    category = newsz.text();
                                }
                            }

                        }

                    }

                }

            }


        }


    }

    private List<String> getNewsContent(Document newsDocument) throws IOException {
        List<String> contents = new ArrayList<>();
        Elements elements = newsDocument.getElementsByClass("topic");

        for(Element el:elements){
            for(Element tag:el.children()){
                if(tag.is("p")){
                    contents.add(tag.text());
                }
            }
        }
        return contents;
    }

    private String getNewsImageURL(Document newsDocument) throws IOException {
        String url = "";
        Element element = newsDocument.getElementsByClass("section featured-story").first();

        Element content = element.child(0);

        for(Element ele:content.children()){
            if(ele.is("img")){
                url = ele.attr("src");
            }
        }
        return url;
    }


}