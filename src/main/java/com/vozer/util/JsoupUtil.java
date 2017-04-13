package com.vozer.util;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by vqnguyen on 7/5/2016.
 * Util to get HTML data from a page url by using configuration file.
 */
public class JsoupUtil {

    /**
     * @param pageUrl url of page use to get html
     * @param configFile file config selector for jsoup
     *        File structure:
     *                 attribute -> CSS or jquery-like selector syntax -> element attribute
     *        Example: eventUrls	a[href].contenttype-fhnweventitem	href
     *                 eventImages	img.contentLeadImage	src
     *                 eventTitles	a[href].contenttype-fhnweventitem	text
     * @return JsonObject contains key match with attributes in config file and values from webpage that are selected
     */
    public static JSONObject getDataFromPage (String pageUrl, InputStream configFile) throws Exception {

        Document doc = Jsoup.connect(pageUrl).header("Accept-Encoding", "gzip, deflate")
                .userAgent("Chrome")
                .referrer("http://www.google.com")
                .followRedirects(true)
                .timeout(600000).get();

        JSONObject returnJson = getDataFromPage(doc, pageUrl, configFile);

        return returnJson;
    }

    /**
     *
     * @param doc
     * @param pageUrl
     * @param configFile
     * @return
     * @throws Exception
     */
    public static JSONObject getDataFromPage (Document doc, String pageUrl, InputStream configFile) throws Exception {
        System.setProperty("ui4j.headless", "true");
        JSONObject returnJson = new JSONObject();
        Scanner scanner = new Scanner(configFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] attrs = line.split("\t");
            String objectKey = attrs[0];
            String elementSelector = attrs[1];
            String atrributeType = attrs[2];

            List<String> elementValues = new ArrayList<>();
            Elements elements = doc.select(elementSelector);

            switch (atrributeType) {
                case "text":
                    for (Element element : elements) {
                        elementValues.add(element.text());
                    }
                    break;
                case "html":
                    for (Element element : elements) {
                        elementValues.add(element.html());
                    }
                case "googleMapEmbed":
                    String iframeSrc = doc.select(elementSelector).attr("src");
                    if (iframeSrc != null && !iframeSrc.isEmpty()) {
                        elementValues.add(getGoogleMapLinkFromIframe(pageUrl, doc.select(elementSelector).attr("src")));
                    }
                    break;
                case "script":
                    for (Element element : elements) {
                        for (Node node: element.childNodes()) {
                            elementValues.add(node.attr("data"));
                        }
                    }
                    break;
                default:
                    for (Element element : elements) {
                        elementValues.add(element.attr(atrributeType));
                    }
            }

            if (elementValues.size() > 1) {
                JSONArray listValue = new JSONArray();
                for (String value: elementValues) {
                    listValue.add(value);
                }
                returnJson.put(objectKey,listValue);
            } else {
                returnJson.put(objectKey, elementValues.size() == 0 ? null : elementValues.get(0));
            }
        }
        return returnJson;
    }

    private static String getGoogleMapLinkFromIframe(String pageUrl, String iframeSrc) {
        String googleMapLink = null;
        try (Page page = BrowserFactory.getWebKit().navigate(pageUrl)) {
            for (com.ui4j.api.dom.Element iframe : page.getDocument().queryAll("iframe")) {
                if(iframeSrc.equals(iframe.getAttribute("src").get())) {
                    googleMapLink = iframe.getContentDocument().get().query("a[href*='https://maps.google.com/maps?ll=']")
                            .get().getAttribute("href").get();
                }
            }
            return googleMapLink;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param htmlSource
     * @param pageUrl
     * @param configFile
     * @return
     * @throws Exception
     */
    public static JSONObject getDataFromPage (String htmlSource, String pageUrl, InputStream configFile) throws Exception {
        Document doc = Jsoup.parse(htmlSource);
        JSONObject returnJson = getDataFromPage(doc, pageUrl, configFile);
        return returnJson;
    }
}
