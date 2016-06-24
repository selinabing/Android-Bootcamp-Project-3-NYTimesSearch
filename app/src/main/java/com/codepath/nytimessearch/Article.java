package com.codepath.nytimessearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by selinabing on 6/20/16.
 */

@Parcel
public class Article {

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    String webUrl;
    String headline;
    String thumbNail;

    public Article() {

    }

    public Article(JSONObject jsonObject, boolean topStory) {
        try {
            if(topStory){
                this.webUrl = jsonObject.getString("url");
                this.headline = jsonObject.getString("title");
            } else {
                this.webUrl = jsonObject.getString("web_url");
                this.headline = jsonObject.getJSONObject("headline").getString("main");
            }

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                if(topStory)
                    this.thumbNail = multimediaJson.getString("url");
                else
                    this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            }
            else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array, boolean topStory){
        ArrayList<Article> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                Article newArticle = new Article(array.getJSONObject(x),topStory);
                results.add(newArticle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
