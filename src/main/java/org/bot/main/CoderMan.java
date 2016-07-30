package org.bot.main;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Created by Mostafa on 7/28/2016.
 */
public class CoderMan {

    private ArrayList<JSONObject> response_body=new ArrayList<JSONObject>();
    private String[] languages= new String[]{
            "abap","as","as3","ada","antlr","antlr-as","antlr-csharp","antlr-cpp",
            "antlr-java","antlr-objc","antlr-perl","antlr-python","antlr-ruby",
            "apacheconf","applescript","aspectj","aspx-cs","aspx-vb","asy","ahk",
            "autoit","awk","basemake","bash","console","bat","bbcode","befunge",
            "blitzmax","boo","brainfuck","bro","bugs","c","csharp","cpp","c-objdump",
            "ca65","cbmbas","ceylon","cfengine3","cfs","cheetah","clojure","cmake",
            "cobol","cobolfree","coffee-script","cfm","common-lisp","coq","cpp-objdump",
            "croc","css","css+django","css+genshitext","css+lasso","css+mako","css+myghty",
            "css+php","css+erb","css+smarty","cuda","cython","d","d-objdump","dpatch","dart",
            "control","sourceslist","delphi","dg","diff","django","dtd","duel","dylan","dylan-console",
            "dylan-lid","ec","ecl","elixir","iex","ragel-em","erb","erlang","erl","evoque","factor",
            "fancy","fan","felix","fortran","Clipper","fsharp","gas","genshi","genshitext","pot","Cucumber",
            "glsl","gnuplot","go","gooddata-cl","gosu","gst","groff","groovy","haml","haskell","hx","html",
            "html+cheetah","html+django","html+evoque","html+genshi","html+lasso","html+mako","html+myghty",
            "html+php","html+smarty","html+velocity","http","haxeml","hybris","idl","ini","io","ioke","irc",
            "jade","jags","java","jsp","js","js+cheetah","js+django","js+genshitext","js+lasso","js+mako",
            "js+myghty","js+php","js+erb","js+smarty","json","julia","jlcon","kconfig","koka","kotlin","lasso",
            "lighty","lhs","live-script","llvm","logos","logtalk","lua","make","mako","maql","mason",
            "matlab","matlabsession","minid","modelica","modula2","trac-wiki","monkey","moocode","moon",
            "mscgen","mupad","mxml","myghty","mysql","nasm","nemerle","newlisp","newspeak","nginx","nimrod",
            "nsis","numpy","objdump","objective-c","objective-c++","objective-j","ocaml","octave","ooc","opa",
            "openedge","perl","php","plpgsql","psql","postgresql","postscript","pov","powershell","prolog",
            "properties","protobuf","puppet","pypylog","python","python3","py3tb","pycon","pytb","qml","racket",
            "ragel","ragel-c","ragel-cpp","ragel-d","ragel-java","ragel-objc","ragel-ruby","raw","rconsole","rd",
            "rebol","redcode","registry","rst","rhtml","RobotFramework","spec","rb","rbcon","rust","splus","sass",
            "scala","ssp","scaml","scheme","scilab","scss","shell-session","smali","smalltalk","smarty","snobol",
            "sp","sql","sqlite3","squidconf","stan","sml","systemverilog","tcl","tcsh","tea","tex","text","treetop",
            "ts","urbiscript","vala","vb.net","velocity","verilog","vgl","vhdl","vim","xml","xml+cheetah","xml+django",
            "xml+evoque","xml+lasso","xml+mako","xml+myghty","xml+php","xml+erb","xml+smarty","xml+velocity",
            "xquery","xslt","xtend","yaml"
    };

    public boolean supportLanguage(String language){
        for(String str:languages){
            if(str.equals(language)){
                return true;
            }
        }
        return false;
    }

    public String runCodePrettier(String url, String code, String language_syntax, Integer userid){
        if(language_syntax!=null){
            if(this.supportLanguage(language_syntax)==true){
                this.postRequest(url,code,language_syntax,userid);
            }
            else {
                return "Not Support This Language";
            }
        }
        return "Required Language";

    }

    private void updateResponseBodyList(){
        ArrayList<JSONObject> mustRemove=new ArrayList<JSONObject>();
        for (JSONObject jsonobj:response_body){
            if(jsonobj.has("beauty_code")){
                if (jsonobj.get("sent").equals(true)){
                    mustRemove.add(jsonobj);
                }
            }
        }
        response_body.removeAll(mustRemove);
    }

    public ArrayList<JSONObject> getResponse(){
        ArrayList<JSONObject> backward=new ArrayList<JSONObject>();
        for (JSONObject obj:response_body){
            if(obj.get("sent").equals(false)){
                backward.add(obj);
                obj.put("sent",true);
            }
        }
        this.updateResponseBodyList();
        return backward;
    }

    private void postRequest(String url, String data,String language, final Integer user) {
        Future<HttpResponse<String>> future = Unirest.post(url)
                .header("accept", "application/json")
                .field("code",data)
                .field("lexer",language)
                .asStringAsync(new Callback<String>() {
                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }
                    public void completed(HttpResponse<String> response) {
                        int code = response.getStatus();
                        Headers headers = response.getHeaders();
                        String body = response.getBody();
                        InputStream rawBody = response.getRawBody();
                        System.out.println(body);
                        putInResponseBodyList(user, body);
                    }
                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }
                }
        );
    }

    private void putInResponseBodyList(Integer user,String body){
        JSONObject result=new JSONObject();
        result.put("beauty_code",body);
        result.put("user",user);
        result.put("sent",false);
        response_body.add(result);
    }

}
