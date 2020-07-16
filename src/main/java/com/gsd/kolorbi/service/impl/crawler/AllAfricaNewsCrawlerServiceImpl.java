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
        for (int i = 0; i < articleRows.size(); i++) {
            Element values = articleRows.get(i);
            Elements children = values.children();
            for (int j = 0; j < children.size(); j++) {
                Element child = children.get(j);
//                System.out.println(child.hasClass("title-header"));
                if (child.hasClass("section")) {
//                        hasClass("title-header")) {
                    Elements childOne = child.children();
                    for (int n = 0; n < childOne.size(); n++) {
                        Element childOneValue = childOne.get(n);
                        Elements childTwo = childOneValue.children();
                        for (int m = 0; m < childTwo.size(); m++) {
                            Element childTwoValue = childTwo.get(n);
                            Elements childThree = childTwoValue.children();
                            for (int p = 0; p < childThree.size(); p++) {
                                Elements sections = childThree.get(p).children();
//                                child = child.nextElementSibling();
                                for (int k = 0; k < sections.size(); k++) {
                                    String titleHeader = sections.get(k).children().get(0).select("a[class=widget-label]").text();
                                    if (sections.get(k).hasClass("no-gutter")) {
                                        Element value = sections.get(k);
                                        int sectionsLength = sections.get(k).children().size();
                                        for (int l = 0; l < sectionsLength - 1; l++) {
                                            String link = value.children().get(l).children().select("a").attr("href");
                                            String[] newsImageURLArray = value.children().get(l).children().get(0).select("img[class=img-responsive]").attr("srcset").split(",", 2);
                                            String newsImageURL = newsImageURLArray[0];
                                            Document newsDocument = Jsoup.connect(link).get();
                                            Elements entryContent = newsDocument.getElementsByClass("topic");

                                            if (entryContent.size() > 0) {
                                                String content = entryContent.select("p[class=user-select]").text();
                                                String subject = entryContent.get(0).text();
                                                String sourceURL = link;


                                                News news = new News();
                                                news.setSource("allafrica");
                                                news.setCountry("NG");
                                                news.setSourceURL(sourceURL);
                                                news.setSubject(subject);
                                                news.setCrawledDate(new Date());
                                                news.setCategory(titleHeader);
                                                news.setContents(getNewsContent(content));
                                                news.setNewsImageURL(newsImageURL);
                                                newss.add(news);
                                                newsService.saveNews(news);

                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    @org.jetbrains.annotations.NotNull
    private List<String> getNewsContent(String content) throws IOException {
        List<String> contents = new ArrayList<>();
        contents.add(content);

        return contents;
    }


}