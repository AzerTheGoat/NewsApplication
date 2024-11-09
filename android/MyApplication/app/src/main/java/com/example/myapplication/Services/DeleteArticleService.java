package com.example.myapplication.Services;

import com.example.myapplication.assignment.Logger;
import com.example.myapplication.assignment.TrustModifier;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteArticleService {
    public void deleteArticle(String authType, String apikey, String request, int idArticle) throws ServerCommunicationError {
        try{
            URL url = new URL(request + idArticle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Authorization", getAuthTokenHeader(authType,apikey ));
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches (false);

            int HttpResult =connection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK  || HttpResult ==HttpURLConnection.HTTP_NO_CONTENT){
                Logger.log (Logger.INFO,"Article (id:"+idArticle+") deleted with status "+HttpResult+":"+parseHttpStreamResult(connection));
            }else{
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Deleting article (id:"+idArticle+") : " + e.getClass() + " ( "+e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( "+e.getMessage() + ")");
        }
    }

    private  String getAuthTokenHeader(String authType, String apikey){
        String authHeader = authType + " apikey=" + apikey;
        return authHeader;
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
}
