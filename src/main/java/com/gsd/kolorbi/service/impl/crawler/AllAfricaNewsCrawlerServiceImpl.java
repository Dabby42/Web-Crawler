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
        Elements articleRows = homeDocument.select("div[id=home-section-b]");

        // Gets all the news for the day on the landing page of punch
        for (Element article:articleRows){
            if (article.hasClass("section")){
                Elements articleChildren = article.children();
                Element childOne = articleChildren.get(0);
                Elements childOneChildren = childOne.children();
                Element childTwo = childOneChildren.get(0);
                Elements childTwoChildren = childTwo.children();
                Element childThree = childTwoChildren.get(0);
                Elements childThreeChildren = childThree.children();
                Element childFour = childThreeChildren.get(0);
                Elements childFourChildren = childFour.children();
                for(Element child:childFourChildren){
                    String titleHeader = child.children().get(0).children().get(0).text();
                    System.out.println(titleHeader);
                    if (child.hasClass("no-gutter")){
                        Elements childList = child.children();
                        for(Element linkContainer:childList){
                            String articleLink = linkContainer.select("a[href]").attr("href");
                            System.out.println(articleLink);
                            linkContainer.children().get(0).children().get(0);
                            String[] newsImageURLArray = linkContainer.select("img[img-responsive]").attr("srcset").split(",", 2);
                            String newsImageURL = newsImageURLArray[0];
                            System.out.println(newsImageURL);
                            Document newsDocument = Jsoup.connect(articleLink).get();
                            Elements entryContent = newsDocument.getElementsByClass("topic");

                            if (entryContent.size() > 0) {
                                String content = entryContent.select("p[class=user-select]").text();
                                System.out.println(content);
                                String subject = entryContent.get(0).text();


                                News news = new News();
                                news.setSource("allafrica");
                                news.setCountry("NG");
                                news.setSourceURL(articleLink);
                                news.setSubject(subject);
                                news.setCrawledDate(new Date());
                                news.setCategory(titleHeader);
                                news.setContents(getNewsContent(content));
                                news.setNewsImageURL(newsImageURL);
                                newss.add(news);
                                newsService.saveNews(news);

                            }
                        };
                    }
                }
            }
        }


    }

    private List<String> getNewsContent(String content) throws IOException {
        List<String> contents = new ArrayList<>();
        contents.add(content);

        return contents;
    }


}