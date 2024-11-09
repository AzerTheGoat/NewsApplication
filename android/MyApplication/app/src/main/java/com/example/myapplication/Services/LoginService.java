package com.example.myapplication.Services;

import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.assignment.Logger;
import com.example.myapplication.assignment.TrustModifier;
import com.example.myapplication.assignment.exceptions.AuthenticationError;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class LoginService {

    private String idUser;
    private String authType;
    private String apikey ;

    private boolean isAdministrator = false;


    public void login(String request, String username, String password) throws AuthenticationError {
        String res = "";
        try{
            String parameters =  "";

            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            //connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            connection.setUseCaches (false);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", username);
            jsonParam.put("passwd", password);

            writeJSONParams(connection, jsonParam);

            int HttpResult =connection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                res = parseHttpStreamResult(connection);

                JSONObject userJsonObject = readRestResultFromSingle(res);
                idUser = userJsonObject.get("user").toString();
                authType = userJsonObject.get("Authorization").toString();
                apikey = userJsonObject.get("apikey").toString();
                isAdministrator = userJsonObject.containsKey("administrator");

                LoginDataHolder.getInstance().setLoginData(idUser, authType, apikey, username);

            }else{
                Logger.log(Logger.ERROR,connection.getResponseMessage());

                throw new AuthenticationError(connection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            throw new AuthenticationError(e.getMessage());
        }
        catch (IOException e) {
            //e.printStackTrace();
            throw new AuthenticationError(e.getMessage());
        }
        catch (Exception e) {
            //e.printStackTrace();
            throw new AuthenticationError(e.getMessage());
        }

    }

    private void writeJSONParams(HttpURLConnection connection, JSONObject json) throws IOException{
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
    private JSONObject readRestResultFromSingle(String res) throws ParseException {
        return (JSONObject) JSONValue.parseWithException(res);
    }



}
