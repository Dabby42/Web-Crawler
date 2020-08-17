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
import java.util.regex.*;

@Service
public class PunchNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://punchng.com";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        List<News> newss = new ArrayList<>();

        Document homeDocument = Jsoup.connect(source).get();
        Elements articleRows = homeDocument.select("main");

        // Gets all the news for the day on the landing page of punch
        for(int i = 0; i < articleRows.size(); i++){
            Element values = articleRows.get(i);
            Elements children = values.children();
            for(int j = 0; j < children.size(); j++){
                Element child = children.get(j);
                System.out.println(child.hasClass("title-header"));
                if(child.hasClass("title-header")) {
                    String titleHeader = child.text();
                    while (child.nextElementSibling().hasClass("cat-sections")){
                        Elements sections = child.nextElementSibling().children();
                        child = child.nextElementSibling();
                        for (int k = 0; k < sections.size(); k++) {
                            String link = sections.get(k).child(0).attr("href");
                            Document newsDocument = Jsoup.connect(link).get();
                            Elements imageTags = newsDocument.getElementsByClass("post_featured_image");

//                            System.out.println(imageTags.size());

                            if (imageTags.size() > 0) {
                                Elements entryContent = newsDocument.getElementsByClass("entry-content").get(0).children();
                                String subject = imageTags.get(0).child(0).attr("title");
                                String sourceURL = imageTags.get(0).child(0).attr("href");
                                String newsImageURL = getNewsImageURL(newsDocument);
                                List<String> content = getNewsContents(entryContent);

                                News news = new News();
                                news.setSource("punchng");
                                news.setCountry("NG");
                                news.setSourceURL(sourceURL);
                                news.setSubject(subject);
                                news.setSourceLogoURL("https://cdn.punchng.com/wp-content/uploads/2020/01/06142718/New-Logo.png");
                                news.setCrawledDate(new Date());
                                news.setCategory(titleHeader.split("read more")[0].trim());
                                news.setContents(content);
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

    private String getCategory(String s){

        switch(s){
            case "celebs":
                return NewsCategory.CELEBRITY.name();
            case "politics":
                return NewsCategory.POLITICS.name();
            case "sports":
                return NewsCategory.SPORTS.name();
            case "jokes":
                return NewsCategory.COMEDY.name();
            default:
                try{
                    return NewsCategory.valueOf(s).name();
                }catch(IllegalArgumentException e){
                    return s;
                }
        }
    }

    public List<String> getNewsContents(Elements newsContent) throws IOException {
        List<String> contents = new ArrayList<>();
        List<String> filteredContents = new ArrayList<>();

        for(Element ncontent:newsContent){
            if(!ncontent.select("p").equals(null)){

                contents.add(ncontent.text());
            }

        }

        for(int i = 0; i < contents.size(); i++){
            if(contents.get(i).equals("Copyright PUNCH.")){
                break;
            }
            filteredContents.add(contents.get(i));
        }


        return filteredContents;
    }
}
