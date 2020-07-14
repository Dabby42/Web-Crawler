package com.gsd.kolorbi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsd.kolorbi.config.GlobalValue;
import com.gsd.kolorbi.enums.AdPlacement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.xpath.operations.Bool;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "articles")
@ToString
public class News {

    @Id
    @Getter
    @Setter
    String id;

    @Getter
    @Setter
    String source;

    @Getter
    @Setter
    String subject;

    @Getter
    @Setter
    Date createdAt = new Date();

    @Getter
    @Setter
    Date updatedAt = new Date();

    @Getter
    @Setter
    Boolean isActive = true;

    @Getter
    @Setter
    String newsImageURL;

    @Getter
    @Setter
    List<String> contents;

    @Indexed()
    @Getter
    @Setter
    Date crawledDate;

    @Indexed(unique = true)
    @Getter
    @Setter
    String sourceURL;

    @Getter
    @Setter
    String category;

    @Getter
    @Setter
    String sourceLogoURL;

    @JsonIgnore
    @Getter
    @Setter
    String country;

    @JsonIgnore
    String linkURL;

    @JsonIgnore
    String viewContents;

    @JsonIgnore
    String seoKeyword;

    public String getLinkURL() {
        String sourceURL = this.sourceURL;
        if(sourceURL.endsWith("/")){
            sourceURL = sourceURL.substring(0, sourceURL.length() - 1);
        }

        if(this.source.equalsIgnoreCase("youtube.com")){
            String path = "";
            int count = 0;
            for (String s: this.subject.split(" ")){
                if(count == 8){
                    break;
                }
                if(count == 0) {
                    path += s.replaceAll("[^a-zA-Z0-9]", "");
                }else{
                    path += ("-" +s.replaceAll("[^a-zA-Z0-9]", ""));
                }
                count++;
            }

            return "/news/" + this.id + '/' + path;
        }else {
            return "/news/" + this.id + '/' + sourceURL.split("/")[sourceURL.split("/").length - 1];
        }
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public void setViewContents(String viewContents) {
        this.viewContents = viewContents;
    }

    public String getViewContents() {
        String adsenseClientId = GlobalValue.getProperty("adsense.article.inline.client.id");
        String adsenseSlotId = GlobalValue.getProperty("adsense.article.inline.slot.id");
        String serverHost = GlobalValue.getProperty("server.host");

        //System.out.println("KAYCEE" + adsenseClientId + " " +adsenseSlotId +" "+ serverHost);
        String viewContents = "";
        if (this.contents != null) {
            for (int i = 0; i < this.contents.size(); i++) {
                viewContents += this.contents.get(i)
                        .replaceAll("\\{\\{K_AD}}", AdPlacement.ADS_350_250.getPlacement())
                        .replaceAll("https:\\/\\/www.vanguardngr.com\\/wp-content", serverHost+"mirror?url=" +"https://www.vanguardngr.com/wp-content");

                if(this.contents.size() / 2 == i){
                    viewContents += AdPlacement.ADS_NATIVE.getPlacement();
                }
            }
        }
        return viewContents;
    }

    public String getSeoKeyword() {
        String path = "";
        String[] keywords = this.subject.split(" ");
        for (int i =0; i < keywords.length; i++){
            if(i == 0) {
                path += keywords[i].replaceAll("[^a-zA-Z0-9]", "");
            }else{
                path += (", " +keywords[i].replaceAll("[^a-zA-Z0-9]", ""));
            }
        }

        return path + ", blogs,news, blog, Entertainment, Lifestyle, Fashion, Beauty, Inspiration, politics, gossip, Events";
    }

    public void setSeoKeyword(String seoKeyword) {
        this.seoKeyword = seoKeyword;
    }
}
