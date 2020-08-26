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
public class TechPointNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://techpoint.africa/";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();
        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("div.row");
        for(Element row:articleRows){

            Elements items = row.children();//creating an array
            for(Element item:items){// looping through elements in the array

                if (item.hasClass("small-12 medium-4 columns")){
                    Elements itm = item.children();
                    if (itm.size() > 0){
                        String link = itm.first().child(0).attr("href");
                        System.out.println(link);
                        Document newsDocument = Jsoup.connect(link).get();
                        Elements src = newsDocument.getElementsByClass("small-12 medium-8 columns");
                        Elements header = newsDocument.getElementsByClass("entry-title");
                        Elements title = newsDocument.getElementsByClass("post-subjects");
                        System.out.println(header.size());

                        if (header.size() > 0) {
                            Elements entryContent = newsDocument.getElementsByClass("post-content entry-content cf");
                            String subject = header.get(0).text();
                            String sourceURL = link;
                            String titleHeader = title.get(0).text();
                            String newsImageURL = getNewsImageURL(newsDocument);
                            List<String> content = getNewsContents(entryContent);
//                            System.out.println(content);

                            News news = new News();
                            news.setSource("techpoint.africa");
                            news.setCountry("NG");
                            news.setSourceURL(sourceURL);
                            news.setSubject(subject);
                            news.setSourceLogoURL("https://techpoint.africa/wp-content/uploads/2018/07/Techpoint.africa-Logo.png");
                            news.setCrawledDate(new Date());
                            news.setCategory(titleHeader);
                            news.setContents(content);
                            news.setNewsImageURL(newsImageURL);
                            newss.add(news);
                            newsService.saveNews(news);
                        }
                    }

//                } else {
//                    Elements itm = item.children();
//                    for (Element i: itm){
//                        if (i.hasClass("post-content entry-content small")){
//                            System.out.println(i.child(0).text());
//                        }
//
//                    }
//
//                }
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
        for(Element ncontainer:newsContent){
            for(Element ncontent:ncontainer.children()){
                if(ncontent.select("p") != null){
                    contents.add(ncontent.select("p").text());

                }
            }

        }

        return contents;
    }

}