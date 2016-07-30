package org.bot.main;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mostafa on 7/28/2016.
 */
public class Main {
    public static void main(String argv[]) throws IOException {
//        CoderMan coderMan=new CoderMan();
/*
        String test="<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<body>\n" +
                "  <!-- HTML generated using hilite.me --><div style=\"background: #000000; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;\"><table><tr><td><pre style=\"margin: 0; line-height: 125%\">1\n" +
                "2\n" +
                "3\n" +
                "4\n" +
                "5</pre></td><td><pre style=\"margin: 0; line-height: 125%\"><span style=\"color: #00cd00\">public</span> <span style=\"color: #00cd00\">class</span> <span style=\"color: #00cdcd\">Main</span><span style=\"color: #3399cc\">{</span>\n" +
                "   <span style=\"color: #00cd00\">public</span> <span style=\"color: #00cd00\">void</span> <span style=\"color: #cccccc\">main</span><span style=\"color: #3399cc\">(</span><span style=\"color: #cccccc\">String</span> <span style=\"color: #cccccc\">argv</span><span style=\"color: #3399cc\">[]){</span>\n" +
                "      <span style=\"color: #cccccc\">System</span><span style=\"color: #3399cc\">.</span><span style=\"color: #cccccc\">out</span><span style=\"color: #3399cc\">.</span><span style=\"color: #cccccc\">println</span><span style=\"color: #3399cc\">(</span><span style=\"color: #cd0000\">&quot;Hello telegram&quot;</span><span style=\"color: #3399cc\">);</span>\n" +
                "   <span style=\"color: #3399cc\">}</span>\n" +
                "<span style=\"color: #3399cc\">}</span>\n" +
                "</pre></td></tr></table></div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
*/


        ClientManager clientManager=new ClientManager("257991269:AAHocjVp_bKeejV3EFJ-rS_a1ymzo3glG14");

//        ImageGenerator imageGenerator=new ImageGenerator();
//        imageGenerator.generateJSONObject(new JSONObject().put("id",10),test);
//        while (true){
//            imageGenerator.getImageResponses();
//        }

//        String html ="";
//        ImageGenerator imageGenerator=new ImageGenerator();
//        imageGenerator.generateImage(html);
    }
}
