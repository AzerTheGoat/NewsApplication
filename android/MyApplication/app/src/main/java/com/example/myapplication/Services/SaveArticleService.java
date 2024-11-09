package com.example.myapplication.Services;

import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.Logger;
import com.example.myapplication.assignment.TrustModifier;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class SaveArticleService {
    public int saveArticle(String authType, String apikey, String request, Article a) throws ServerCommunicationError {
        try{
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", getAuthTokenHeader(authType, apikey));
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches (false);

            writeJSONParams(connection, a.toJSON());

            int HttpResult =connection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                String res = parseHttpStreamResult(connection);
                // get id from status ok when saved
                //int id = readRestResultFromInsert(res);
                return 0;
            }else{
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR,"Inserting article ["+a+"] : " + e.getClass() + " ( "+e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( "+e.getMessage() + ")");
        }
    }

    private  String getAuthTokenHeader(String authType, String apikey){
        String authHeader = authType + " apikey=" + apikey;
        return authHeader;
    }
    private void writeJSONParams(HttpURLConnection connection, JSONObject json) throws IOException {
        // Send POST output.

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
        wr.writeBytes(json.toJSONString());//(URLEncoder.encode(json.toJSONString(),"UTF-8"));
        wr.flush();
        wr.close();
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

    private int readRestResultFromInsert(String res) throws ParseException, ServerCommunicationError {
        Object o = JSONValue.parseWithException(res);
        if (o instanceof JSONObject){
            JSONObject jsonResult = (JSONObject) JSONValue.parseWithException(res);
            Set<String> keys = jsonResult.keySet();
            if (keys.contains("id"))
                return Integer.parseInt((String) jsonResult.get("id"));
            else{
                throw new ServerCommunicationError("Error: No id in json returned");
            }
        }else{
            throw new ServerCommunicationError("Error: No json returned");
        }
    }

}
