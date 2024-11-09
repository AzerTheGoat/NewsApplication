package com.example.myapplication.Services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.Logger;
import com.example.myapplication.assignment.ModelManager;
import com.example.myapplication.assignment.TrustModifier;
import com.example.myapplication.assignment.exceptions.AuthenticationError;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ArticleService {

    private static final String TAG = "ArticleService";

    // Method to fetch articles in a background thread
    public void fetchArticles(final String apiUrl, ModelManager mm, final OnArticlesFetchedListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Article> articles = getArticles(apiUrl, mm);

                    // Sort articles by updated_date in descending order (most recent first)
                    articles.sort(Comparator.comparing(Article::getUpdate_date).reversed());


                    // loop on each article to get the getThumbnail and if it contains a comma, then remove the photo from the article
                    for (Article article : articles) {
                        if (article.getThumbnail().contains(",")) {
                            article.setImage(null);
                            article.setThumbnail(null);
                        }
                    }

                    // Update UI thread with the result
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(articles);
                        }
                    });

                } catch (Exception e) {
                    // Handle the exception and notify the listener
                    Log.e(TAG, "Error fetching articles", e);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        }).start();
    }


    // Listener to notify the calling activity about success or error
    public interface OnArticlesFetchedListener {
        void onSuccess(List<Article> articles);
        void onError(Exception error);
    }

    public List<Article> getArticles(String request, ModelManager mm) throws ServerCommunicationError, IOException {
        List<Article> result = new ArrayList<Article>();
        try{
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setDoOutput(true);
            //connection.setDoInput(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches (false);

            int HttpResult =connection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                String res = parseHttpStreamResult(connection);
                List<JSONObject> objects = this.readRestResultFromList(res);
                for (JSONObject jsonObject : objects) {
                    result.add(new Article(mm,jsonObject));
                }
                System.out.println(result.get(0).getUpdate_date());
                Logger.log (Logger.INFO, objects.size() + " objects (Article) retrieved");
            }else{
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log (Logger.ERROR, "Listing articles :" + e.getClass() + " ( "+e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( "+e.getMessage() + ")");
        }

        return result;
    }

    private String parseHttpStreamResult(HttpURLConnection connection) throws UnsupportedEncodingException, IOException {
        String res = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(
                connection.getInputStream(),"utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            res += line + "\n";

        }
        br.close();
        return res;
    }

    private List<JSONObject> readRestResultFromList(String res) throws AuthenticationError {
        List<JSONObject> result = new ArrayList<JSONObject>() ;
        try {
            Object o = JSONValue.parseWithException(res);
            if (o instanceof JSONObject){
                JSONObject jsonResult = (JSONObject) JSONValue.parseWithException(res);
                @SuppressWarnings("unchecked")
                Set<Object> keys = jsonResult.keySet();
                for (Object keyRow : keys) {
                    JSONObject jsonObj = (JSONObject)jsonResult.get(keyRow);
                    result.add(jsonObj);
                }
            } else if (o instanceof JSONArray){
                JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(res);
                for (Object row : jsonArray) {
                    JSONObject jsonObj = (JSONObject)row;
                    result.add(jsonObj);
                }
            } else{
                throw new AuthenticationError("Result is not an Json Array nor Object");
            }
        } catch (ParseException e) {
            throw new AuthenticationError(e.getMessage());
        }
        return result;
    }

}
