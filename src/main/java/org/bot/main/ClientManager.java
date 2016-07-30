package org.bot.main;

import com.google.common.io.Resources;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import com.sun.imageio.plugins.common.ImageUtil;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by Mostafa on 7/29/2016.
 */
public class ClientManager {
    private HashMap<User,String> USER_LANGUAGE=new HashMap<User, String>();
    private ArrayList<JSONObject> clientStates=new ArrayList<JSONObject>();
    private CoderMan coderMan=new CoderMan();
    private TelegramBot telegramBot=null;
    ImageGenerator imageGenerator=null;
    Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(
            new String[]{"java", "first row button2"},
            new String[]{"second row button1", "second row button2"})
            .oneTimeKeyboard(true)   // optional
            .resizeKeyboard(true)    // optional
            .selective(true);

    Keyboard replyKeyboard=new ReplyKeyboardMarkup(
            new String[]{"java","groovy","php","xml"},
            new String[]{"json","python","lua","matlab"},
            new String[]{"c","cpp","csharp","fsharp"},
            new String[]{"coffee-script","delphi","django"},
            new String[]{"jade","css","html"},
            new String[]{"haml","js","jsp"},
            new String[]{"objective-c","objective-c++"},
            new String[]{"plpgsql","python3","cobol"},
            new String[]{"yaml","vim","perl"})
            .oneTimeKeyboard(true)   // optional
            .resizeKeyboard(true)    // optional
            .selective(true);

    Keyboard keyboard = new ReplyKeyboardMarkup(
            new KeyboardButton[]{
                    new KeyboardButton("/run")
            }
    );

    public ClientManager(String bot_token) throws IOException {
        telegramBot= TelegramBotAdapter.build(bot_token);
        Keyboard forceReply = new ForceReply(true); // or just new ForceReply();
        Keyboard replyKeyboardHide = new ReplyKeyboardHide(); // new ReplyKeyboardHide(isSelective)
        imageGenerator=new ImageGenerator();
//        sender();
        getUpdater();

    }

    private void ImageDownloader(String IMAGE_URL,File outputfile) throws IOException {
        URL fetchImage = new URL(IMAGE_URL);
        byte[] imageAsArray = Resources.toByteArray(fetchImage);
        com.google.common.io.Files.write(imageAsArray, outputfile);
    }


    public void sender() throws IOException {
        String test="image_"+ String.valueOf(10)+".jpg";
        System.out.println(test);
        File file2=new File("testme1.png");
        file2.setReadable(true);
//        ImageDownloader("https://margo.convertio.me/p/eUsqDcRTRZd47mngEJ9OLw/181a2d6010fc2e2b7f032cb46da8265f/test.jpg",file2);
        ImageDownloader("https://lisa.convertio.me/p/JQWauUY-28K-jmIDE23i0g/181a2d6010fc2e2b7f032cb46da8265f/test.jpeg",file2);
        this.telegramBot.execute(new SendDocument(95232711,file2));

    }


    /*Deprecated
    private void sendPictureResponse(ArrayList<JSONObject> responses){
        for(JSONObject res:responses){
            if (res.has("beauty_code") && res.has("user")){
                File file=imageGenerator.generateImage(res.getString("beauty_code"));
                this.telegramBot.execute(new SendPhoto(res.get("user"),file));
            }
        }
    }
    */

    private void sendMakePictureRequest(ArrayList<JSONObject> response){
        for (JSONObject jsonObject:response){
            if (jsonObject.has("beauty_code") && jsonObject.has("user")){
                imageGenerator.generateJSONObject(jsonObject,jsonObject.get("beauty_code").toString());
            }
        }
    }


    private void getUpdater() throws IOException {
        Integer offset=171574644;//171574643
        while (true){
            GetUpdatesResponse updatesResponse = this.telegramBot.execute(new GetUpdates().offset(offset).limit(0).timeout(0));
            List<Update> updates = updatesResponse.updates();
            Iterator<Update> updateIterator=updates.iterator();
            int i=0;
            while (updateIterator.hasNext()){
                Update update=updateIterator.next();
                offset=update.updateId();
                runCommand(update.message());
                sendMakePictureRequest(coderMan.getResponse());
                sendImageResponse(imageGenerator.getImageResponses());
                i++;
            }
            if (i>0){
                offset++;
            }
            sendMakePictureRequest(coderMan.getResponse());
            sendImageResponse(imageGenerator.getImageResponses());
        }
    }

    private void sendImageResponse(ArrayList<JSONObject> responses){
        for (JSONObject res:responses){
            if (res.has("lastfile")){
                File photo=new File(res.get("lastfile").toString());
                if (photo.exists()){
                    System.out.println(res.get("chatid"));
                    System.out.println(res.get("lastfile"));
                    String name=photo.getName();
                    this.telegramBot.execute(new SendMessage(res.get("chatid"),"Sending Code Photo..."));
                    this.telegramBot.execute(new SendDocument(res.get("chatid"),photo));
                    photo.delete();
                }
            }
        }
    }


    public void setReplyKeyboardMarkup(Message message,SendResponse sendResponse){
        //handle keyboard
    }
    private void cleanClientCache(Integer userID){
        User tempUser=null;
        for(User user:this.USER_LANGUAGE.keySet()){
            if(user.id().equals(userID)){
                tempUser=user;
                break;
            }
        }
        if(tempUser!=null){
            this.USER_LANGUAGE.remove(tempUser);
        }
    }

    public void runCommand(Message message){
        if(message.text().equals("/run")){
            cleanClientCache(message.from().id());
            setRunState(message);
            sendResponse(message,"Please Send Your Programming Language",this.replyKeyboard);
        }
        else {
            JSONObject temp=this.findUser(message);
            if (temp!=null){
                if (temp.has("state")){
                    if (temp.get("state").equals("run") && coderMan.supportLanguage(message.text())){
                        setCodeState(message);
                    }
                    else {
                        prettifyCode(message);
                    }
                }
            }
        }
    }

    private void setCodeState(Message message){
        if(getUserState(message).equals("run")){
            JSONObject user=this.findUser(message);
            user.put("state","code");
            user.put("language",message.text());
            this.clientStates.add(user);
            sendResponse(message,"Please Send Your Codes",null);
        }
        else {
            this.sendResponse(message,"Please send /run to run again",this.replyKeyboardMarkup);
        }
    }

    private void prettifyCode(Message message){
        JSONObject user=this.findUser(message);
        if (user!=null){
            if(user.has("language")){
                if(getUserState(message).equals("code")){
                    coderMan.runCodePrettier("http://hilite.me/api"
                            ,message.text()
                            ,user.get("language").toString()
                            ,message.chat().id().intValue());
                }
                else {
                    sendResponse(message,"Please send /run to run again :(",this.replyKeyboardMarkup);
                }
            }
            else {
                System.err.println("Internal error:Language not been set");
            }
        }
        else {
            sendResponse(message,"Please Send /run command to start again",this.replyKeyboardMarkup);
        }
    }

    private void setRunState(Message message){
        String state=getUserState(message);
        if(state.equals("")){
            JSONObject userState=new JSONObject();
            userState.put("state","run");
            userState.put("id",message.chat().id());
            this.clientStates.add(userState);
        }
        else {
            JSONObject user=findUser(message);
            user.put("state","run");
        }
    }

    private JSONObject findUser(Message message){
        for(JSONObject user:this.clientStates){
            if(user.has("id")){
                if (user.get("id").equals(message.chat().id())){
                    return user;
                }
            }
        }
        return null;
    }

    private String getUserState(Message message){
        for(JSONObject user:this.clientStates){
            if(user.has("id")){
                if (user.get("id").equals(message.chat().id())){
                    if(user.has("state")){
                        if(user.get("state").equals("run")){
                            return "run";
                        }
                        if (user.get("state").equals("code")){
                            return "code";
                        }
                        if(user.get("state").equals("getpic")){
                            return "getpic";
                        }
                    }
                }
            }
        }
        return "";

    }

    private void sendResponse(Message request,String text,Keyboard replyKeyboard){
        SendResponse sendResponse=null;
        if(replyKeyboard==null){
            sendResponse = this.telegramBot.execute(
                    new SendMessage(request.chat().id(),text));
        }
        else {
            sendResponse = this.telegramBot.execute(
                    new SendMessage(request.chat().id(),text)
                            .replyMarkup(replyKeyboard));
        }
    }

}
