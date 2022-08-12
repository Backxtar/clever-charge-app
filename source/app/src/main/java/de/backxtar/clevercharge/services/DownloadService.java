package de.backxtar.clevercharge.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.backxtar.clevercharge.data.APIResponse;
import de.backxtar.clevercharge.data.Article;
import de.backxtar.clevercharge.data.ChargingStation;

/**
 * DownloadService of the app.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class DownloadService {

    /**
     * Docker for station arrayList.
     * @return array of station objects
     */
    public static ArrayList<ChargingStation> getStations() {
        Gson gson = new Gson();
        final Type return_type = new TypeToken<ArrayList<ChargingStation>>() {}.getType();
        final String json = getJson("https://api.aurora-theogenia.de/chargingstations/charging_stations.json");
        return gson.fromJson(json, return_type);
    }

    /**
     * Get response from api for list requests.
     * @param endpoint as string
     * @param params as stringArray
     * @return new APIResponse class
     */
    public static APIResponse getResponse(String endpoint, String[] params) {
        String conn = "https://api.aurora-theogenia.de/clever_charge/" + endpoint +
                "/?access_token=xoMLoIxPaQtHUt2k3ROjQRf3CV0WoSo7" + "&";

        for (int i = 0; i < params.length; i++) {
            conn += params[i];
            if (i < (params.length - 1)) conn += "&";
        }

        Gson gson = new Gson();
        final Type return_type = new TypeToken<APIResponse>() {}.getType();
        final String json = getJson(conn);
        return gson.fromJson(json, return_type);
    }

    /**
     * Get response from api for non list requests.
     * @param endpoint as string
     * @return new APIResponse class
     */
    public static APIResponse getResponse(String endpoint) {
        String conn = "https://api.aurora-theogenia.de/clever_charge/" + endpoint +
                "/?access_token=xoMLoIxPaQtHUt2k3ROjQRf3CV0WoSo7";

        Gson gson = new Gson();
        final Type return_type = new TypeToken<APIResponse>() {}.getType();
        final String json = getJson(conn);
        return gson.fromJson(json, return_type);
    }

    /**
     * Get articles from api.
     * @return an article arrayList
     */
    public static ArrayList<Article> getArticles() {
        Gson gson = new Gson();
        final Type return_type = new TypeToken<ArrayList<Article>>() {}.getType();
        final String json = getJson("https://api.aurora-theogenia.de/clever_charge/news/news.json");
        ArrayList<Article> articles = gson.fromJson(json, return_type);
        return articles;
    }


    /**
     * Download json from api.
     * @return json data string
     * @param endpoint api endpoint
     * @link https://api.aurora-theogenia.de/chargingstations/charging_stations.json
     */
    public static String getJson(String endpoint) {
        BufferedReader reader = null;
        try {
            final URL url = new URL(endpoint);
            reader = new BufferedReader(new InputStreamReader(read(url)));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create InputStream with connection timeout (for big files).
     * @param url api url
     * @return Configured InputStream object
     * @throws IOException if url is bad or doesn't exist
     */
    private static InputStream read(URL url) throws IOException {
        final HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        httpCon.setConnectTimeout(10000);
        httpCon.setReadTimeout(10000);
        httpCon.setRequestMethod("GET");
        return httpCon.getInputStream();
    }
}
