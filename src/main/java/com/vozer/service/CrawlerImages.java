package com.vozer.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import com.ui4j.api.browser.PageConfiguration;
import com.ui4j.api.dom.Element;
import com.ui4j.api.event.DocumentListener;
import com.ui4j.api.event.DocumentLoadEvent;
import com.ui4j.api.interceptor.Interceptor;
import com.ui4j.api.interceptor.Request;
import com.ui4j.api.interceptor.Response;
import com.vozer.dao.ImageDao;
import com.vozer.model.Post;
import com.vozer.util.JsoupUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vqnguyen on 4/5/2017.
 */
@Service
public class CrawlerImages {

    @Autowired
    ImageDao imageDao;

    @Autowired
    UploadFile uploadFile;

    final String url9gag = "http://9gag.com/gif?ref=9nav";

    @Scheduled(cron = "30 21 1,7,13,19 * * *")
    public void crawler9gag() throws Exception {
        System.out.println("#### Start Crawler 9gag job ####");

        JSONObject jsonObject = JsoupUtil.getDataFromPage(url9gag, getConfigSelectorFile("crawler-config/9gag.crawler"));

        int numberOfItem = ((JSONArray) jsonObject.get("title")).size();
        List<Post> listImages = new ArrayList<>();
        Map<String, String> webmFiles = new HashMap<>();
        Map<String, String> mp4Files = new HashMap<>();
        Map<String, String> gifFiles = new HashMap<>();
        Map<String, String> thumbFiles = new HashMap<>();

        for (int i = 0; i < numberOfItem; i++) {
            Post image = new Post();
            String webmLink = ((JSONArray) jsonObject.get("webm")).get(i).toString();
            String mp4Link = ((JSONArray) jsonObject.get("mp4")).get(i).toString();
            String gifLink = ((JSONArray) jsonObject.get("gif")).get(i).toString();
            String thumbLink = ((JSONArray) jsonObject.get("thumb")).get(i).toString();

            String fileName = StringUtils.delimitedListToStringArray(webmLink, "/")[4].substring(0, 7);

            image.setStory(((JSONArray) jsonObject.get("title")).get(i).toString());
            image.setPic(fileName);
            image.setMediafile(fileName + ".webm");
            image.setTags("gif");
            image.setUrl(gifLink);
            listImages.add(image);

            webmFiles.put(fileName + ".webm", webmLink);
            mp4Files.put(fileName + ".mp4", mp4Link);
            gifFiles.put("l-" + fileName + ".gif", gifLink);
            thumbFiles.put("l-" + fileName + ".jpg", thumbLink);
        }

        imageDao.insertImages(listImages);
        uploadFile.upload(webmFiles, "video");
        uploadFile.upload(mp4Files, "video");
        uploadFile.upload(gifFiles, "gif");
        uploadFile.upload(thumbFiles, "image");
        System.out.println("#### Finish Crawler 9gag job ####");
    }


    private InputStream getConfigSelectorFile(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            return classLoader.getResourceAsStream(URLDecoder.decode(filePath, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
