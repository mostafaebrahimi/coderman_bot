package org.bot.main;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.pengrad.telegrambot.model.Message;
import gui.ava.html.image.generator.HtmlImageGenerator;
import okhttp3.Protocol;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Created by Mostafa on 7/29/2016.
 */
public class ImageGenerator {

//    private String apikey="40aeff7c07ba433bff6eec589a0597da";
    private String apikey="626368bc20fdc51e3ea06de65877d7a5";
    private HtmlImageGenerator htmlImageGenerator;
    private String before="<!DOCTYPE html>\n<html>\n<body>";
    private String after="</body>\n</html>\n";
    private ArrayList<JSONObject> responseFromConverter=new ArrayList<JSONObject>();

    public ImageGenerator(){
        htmlImageGenerator=new HtmlImageGenerator();
    }

    public File generateImage(String html){
        File file=new File("test1.jpg");
        this.htmlImageGenerator.loadHtml(html);
        this.htmlImageGenerator.saveAsImage(file);
        return file;
    }

    public void generateJSONObject(JSONObject info, String html){
        String completeText=this.before+html+this.after;
        if(info.has("user")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", this.apikey);
            jsonObject.put("input", "raw");
            jsonObject.put("file", completeText);
            jsonObject.put("filename", "index.html");
            jsonObject.put("outputformat", "jpg");
            this.sendPOST(jsonObject,(Integer) info.get("user"));
        }
    }


    public ArrayList<JSONObject> getImageResponses() throws IOException {
        ArrayList<JSONObject> response=new ArrayList<JSONObject>();
        for(JSONObject json:this.responseFromConverter){
            if(json.has("sent")){
                if (json.get("sent").equals(false)){
                    response.add(json);
                    json.put("sent",true);
                }
            }
            else {
                if (!json.has("lastfile")){
                    getImagesURL(json);
                    if (json.has("image")){
                        downloadImage(json.get("image").toString(),json);
                    }
                }
            }
        }
        this.responseFromConverter.removeAll(response);
        return response;
    }

    private void downloadImage(String stringurl,JSONObject target) throws IOException {
        File file=getFileName();
        URL url=new URL(stringurl);
        FileUtils.copyURLToFile(url,file);
        target.put("sent",false);
        target.put("lastfile",file);
    }

    private File getFileName() throws IOException {
        File folder = new File("./");
        File[] listOfFiles = folder.listFiles();
        String filename=null;
        if (listOfFiles!=null){
            filename="./"+"image"+(listOfFiles.length+1)+".jpg";
        }
        else {
            filename="./"+"image"+0+".jpg";
        }
        File file=new File(filename);
        if(file.createNewFile()){
            System.out.println(file.getName()+"was created");
        }
        return file;
    }


    private void sendPOST(JSONObject jsonObject,final Integer id){
        Future<HttpResponse<String>> future = Unirest.post("http://api.convertio.co/convert")
                .header("accept", "application/json")
                .body(jsonObject)
                .asStringAsync(new Callback<String>() {
                                   public void failed(UnirestException e) {
                                       System.out.println("The request has failed");
                                   }
                                   public void completed(HttpResponse<String> response) {
                                       int code = response.getStatus();
                                       String body = response.getBody();
                                       InputStream rawBody = response.getRawBody();
                                       System.out.println(body);
                                       JSONObject jsonObject=new JSONObject(body);
                                       jsonObject.put("chatid",id);
                                       responseFromConverter.add(jsonObject);
                                   }
                                   public void cancelled() {
                                       System.out.println("The request has been cancelled");
                                   }
                               }
                );
    }


    private void getImagesURL(final JSONObject jsonObject){

        if(jsonObject.has("data")){
            final JSONObject output=(JSONObject)jsonObject.get("data");
            if (output.has("id")){
                String url="https://api.convertio.co/convert/"+output.get("id").toString()+"/status";
                Future<HttpResponse<String>> future = Unirest.get(url)
                        .header("accept", "application/json")
                        .asStringAsync(new Callback<String>() {
                            public void failed(UnirestException e) {
                                System.out.println("The request has failed");
                            }

                            public void completed(HttpResponse<String> response) {
                                int code = response.getStatus();
                                String body = response.getBody();
//                                InputStream rawBody = response.getRawBody();
                                System.out.println("test body");
                                System.out.println(body);
                                JSONObject res = new JSONObject(body);
                                System.out.println(res);
                                if (res.has("data")) {
                                    JSONObject data =new JSONObject(res.get("data").toString());
                                    System.out.println(data);
                                    if (data.has("output")){
                                        JSONObject output=(JSONObject) data.get("output");
                                        addJOSNObject(jsonObject,output);
                                    }
                                    else {
                                        System.err.println("has not output");
                                    }
                                }
                                else {
                                    System.out.println("don't have data object");
                                }
                            }
                            public void cancelled() {
                                System.out.println("The request has been cancelled");
                            }
                        }
                );
            }
        }
    }

    private void addJOSNObject(JSONObject listobject,JSONObject output){
        JSONObject target=null;
        for(JSONObject jsonObject:this.responseFromConverter){
            if (jsonObject.has("chatid") && listobject.has("chatid")){
                if (jsonObject.get("chatid").equals(listobject.get("chatid"))){
                    target=jsonObject;
                }
            }
        }
        if(target!=null){
            if (output.has("url")){
                target.put("image",output.get("url"));
            }
            System.out.println(target);
        }
        else {
            System.out.println("Can not find object target");
        }
    }
}
