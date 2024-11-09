package com.example.myapplication.Services;

import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.Logger;
import com.example.myapplication.assignment.ModelManager;
import com.example.myapplication.assignment.TrustModifier;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class FetchArticleDetailsService {
    public Article getArticle(String authType, String apikey, String request, int idArticle, ModelManager mm) throws ServerCommunicationError {

        Article result = null;
        try{
            URL url = new URL(request + idArticle);
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
                JSONObject object = this.readRestResultFromGetObject(res);
                result = new Article(mm,object);
                Logger.log (Logger.INFO, " object (Article) retrieved");
            }else{
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log (Logger.ERROR, "Getting article :" + e.getClass() + " ( "+e.getMessage() + ")");
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

    @SuppressWarnings("unchecked")
    private JSONObject readRestResultFromGetObject(String res) throws ParseException, ServerCommunicationError {
        Object o = JSONValue.parseWithException(res);
        if (o instanceof JSONObject){
            JSONObject jsonResult = (JSONObject) JSONValue.parseWithException(res);
            return jsonResult;
        }else{
            throw new ServerCommunicationError("Error: No json returned");
        }
    }

}
