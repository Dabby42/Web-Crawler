package com.gsd.kolorbi.service.impl.crawler;

import com.gsd.kolorbi.enums.NewsCategory;
import com.gsd.kolorbi.model.News;
import com.gsd.kolorbi.service.NewsCrawlerService;
import com.gsd.kolorbi.service.NewsService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class IGNewsCrawlerServiceImpl implements NewsCrawlerService {

    @Autowired
    NewsService newsService;

    private final String source = "https://www.instagram.com/instablog9ja";

    @Override
    public void crawlWebsiteForNews() throws Exception {
        Document homeDocument = Jsoup.connect(source).get();
        JSONObject data = null;
        for(Element element:homeDocument.select("script[type=text/javascript]")) {
            if(element.html().contains("window._sharedData = ")){
                String j = element.html().replace(";","").replace("window._sharedData = ","");
                data = new JSONObject(j);
                break;
            }
        }

        if(data != null){
            JSONArray edges = data.getJSONObject("entry_data")
                    .getJSONArray("ProfilePage")
                    .getJSONObject(0)
                    .getJSONObject("graphql")
                    .getJSONObject("user")
                    .getJSONObject("edge_owner_to_timeline_media")
                    .getJSONArray("edges");
            for(int i = edges.length()-1; i >= 0 ; i++){
                JSONObject node = edges.getJSONObject(i).getJSONObject("node");
                String caption = node.getJSONObject("edge_media_to_caption")
                        .getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("text");
                String id = node.getString("shortcode");
                List<String> contents = new ArrayList<>();
                contents.add(caption);
                contents.add(getIGEmbedded(id));

                News news = new News();
                news.setCountry(data.getString("country_code"));
                news.setSource("instagram.com");
                news.setCrawledDate(new Date());
                news.setCategory(NewsCategory.BLOG.name());
                news.setNewsImageURL(node.getString("display_url"));
                news.setSubject(caption);
                news.setContents(contents);
                news.setSourceURL("https://www.instagram.com/p/"+id);

                newsService.saveNews(news);
            }
        }
    }

    private String getIGEmbedded(String id){
        return "<center>\n" +
                "<blockquote class=\"instagram-media\" data-instgrm-captioned data-instgrm-permalink=\"https://www.instagram.com/p/"+id+"/?utm_source=ig_embed&amp;utm_medium=loading\" data-instgrm-version=\"12\" style=\" background:#FFF; border:0; border-radius:3px; box-shadow:0 0 1px 0 rgba(0,0,0,0.5),0 1px 10px 0 rgba(0,0,0,0.15); margin: 1px; max-width:540px; min-width:326px; padding:0; width:99.375%; width:-webkit-calc(100% - 2px); width:calc(100% - 2px);\"><div style=\"padding:16px;\"> <a href=\"https://www.instagram.com/p/"+id+"/?utm_source=ig_embed&amp;utm_medium=loading\" style=\" background:#FFFFFF; line-height:0; padding:0 0; text-align:center; text-decoration:none; width:100%;\" target=\"_blank\"> <div style=\" display: flex; flex-direction: row; align-items: center;\"> <div style=\"background-color: #F4F4F4; border-radius: 50%; flex-grow: 0; height: 40px; margin-right: 14px; width: 40px;\"></div> <div style=\"display: flex; flex-direction: column; flex-grow: 1; justify-content: center;\"> <div style=\" background-color: #F4F4F4; border-radius: 4px; flex-grow: 0; height: 14px; margin-bottom: 6px; width: 100px;\"></div> <div style=\" background-color: #F4F4F4; border-radius: 4px; flex-grow: 0; height: 14px; width: 60px;\"></div></div></div><div style=\"padding: 19% 0;\"></div><div style=\"display:block; height:50px; margin:0 auto 12px; width:50px;\"><svg width=\"50px\" height=\"50px\" viewBox=\"0 0 60 60\" version=\"1.1\" xmlns=\"https://www.w3.org/2000/svg\" xmlns:xlink=\"https://www.w3.org/1999/xlink\"></svg></div><div style=\"padding-top: 8px;\"> <div style=\" color:#3897f0; font-family:Arial,sans-serif; font-size:14px; font-style:normal; font-weight:550; line-height:18px;\"> View this post on Instagram</div></div><div style=\"padding: 12.5% 0;\"></div> <div style=\"display: flex; flex-direction: row; margin-bottom: 14px; align-items: center;\"><div> <div style=\"background-color: #F4F4F4; border-radius: 50%; height: 12.5px; width: 12.5px; transform: translateX(0px) translateY(7px);\"></div> <div style=\"background-color: #F4F4F4; height: 12.5px; transform: rotate(-45deg) translateX(3px) translateY(1px); width: 12.5px; flex-grow: 0; margin-right: 14px; margin-left: 2px;\"></div> <div style=\"background-color: #F4F4F4; border-radius: 50%; height: 12.5px; width: 12.5px; transform: translateX(9px) translateY(-18px);\"></div></div><div style=\"margin-left: 8px;\"> <div style=\" background-color: #F4F4F4; border-radius: 50%; flex-grow: 0; height: 20px; width: 20px;\"></div> <div style=\" width: 0; height: 0; border-top: 2px solid transparent; border-left: 6px solid #f4f4f4; border-bottom: 2px solid transparent; transform: translateX(16px) translateY(-4px) rotate(30deg)\"></div></div><div style=\"margin-left: auto;\"> <div style=\" width: 0px; border-top: 8px solid #F4F4F4; border-right: 8px solid transparent; transform: translateY(16px);\"></div> <div style=\" background-color: #F4F4F4; flex-grow: 0; height: 12px; width: 16px; transform: translateY(-4px);\"></div> <div style=\" width: 0; height: 0; border-top: 8px solid #F4F4F4; border-left: 8px solid transparent; transform: translateY(-4px) translateX(8px);\"></div></div></div></a> <p style=\" margin:8px 0 0 0; padding:0 4px;\"> <a href=\"https://www.instagram.com/p/"+id+"/?utm_source=ig_embed&amp;utm_medium=loading\" style=\" color:#000; font-family:Arial,sans-serif; font-size:14px; font-style:normal; font-weight:normal; line-height:17px; text-decoration:none; word-wrap:break-word;\" target=\"_blank\">We’re putting the Weekend Hashtag Project on hold this weekend. Instead, we’re challenging people around the world to participate in the 10th Worldwide InstaMeet! Grab a few good friends or meet up with a larger group in your area and share your best photos and videos from the InstaMeet with the #WWIM10 hashtag for a chance to be featured on our blog Monday morning. Be sure to include the name of the location where your event took place along with the unique hashtag you&#39;ve chosen for your InstaMeet in your caption. Photo by @sun_shinealight</a></p> <p style=\" color:#c9c8cd; font-family:Arial,sans-serif; font-size:14px; line-height:17px; margin-bottom:0; margin-top:8px; overflow:hidden; padding:8px 0 7px; text-align:center; text-overflow:ellipsis; white-space:nowrap;\">A post shared by <a href=\"https://www.instagram.com/instagram/?utm_source=ig_embed&amp;utm_medium=loading\" style=\" color:#c9c8cd; font-family:Arial,sans-serif; font-size:14px; font-style:normal; font-weight:normal; line-height:17px;\" target=\"_blank\"> Instagram</a> (@instagram) on <time style=\" font-family:Arial,sans-serif; font-size:14px; line-height:17px;\" datetime=\"2014-10-03T18:00:13+00:00\">Oct 3, 2014 at 11:00am PDT</time></p></div></blockquote> <script async src=\"//www.instagram.com/embed.js\"></script>\n" +
                "\n" +
                "</center>";
    }
}
