package com.dmplayer.chatserver;


import android.content.Context;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD{

    ArrayList<String> log;
    Context mContext;
    String allDir;

    public Server(int port, Context context) {
        super("localhost", port);
        log=new ArrayList<String>();
        mContext = context;
        allDir=mContext.getExternalFilesDir(null).getPath();
//        start(NanoHTTPD.SOCKET_READ_TIMEOUT,false);
    }


    @Override
    public Response serve(IHTTPSession session) {

        StringBuilder page= new StringBuilder();
        if(session.getCookies().read("username")==null)
            if(session.getParms().get("username") !=null)
                session.getCookies().set("username", session.getParms().get("username"), -1);
        page.append("<!DOCTYPE html>");
        page.append("");
        page.append("<html>");
        page.append("  <head>");
        page.append("    <meta charset=\"utf-8\">");
        page.append("    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        page.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        page.append("    <title>Chat</title>");
        page.append("    <link href=\"");
        page.append(allDir);
        page.append("/bootstrap.min.css\" rel=\"stylesheet\">");
        page.append("	<link href=\"");
        page.append(allDir);
        page.append("/chat.css\" rel=\"stylesheet\">");
        page.append("    <!--[if lt IE 9]>");
        page.append("      <script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script>");
        page.append("      <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>");
        page.append("    <![endif]-->");
        page.append("  </head>");
        page.append("  <body>");
        page.append("	<div class=\"container\">");
        page.append("    <div class=\"row\">");
        page.append("	<div class=\"col-xs-2\"></div>");
        page.append("        <div class=\"col-xs-8\">");
        page.append("            <div class=\"panel panel-primary\">");
        page.append("                <div class=\"panel-body\">");
        page.append("                    <ul class=\"chat\">");
        Map<String, String> parms = session.getParms();
        if (parms.get("usermsg") != null) {
            StringBuilder message = new StringBuilder("                        <li class=\"clearfix\">");
            message.append("                            <div class=\"chat-body clearfix\">");
            message.append("<span class=\"time\">[");
            message.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            message.append("]</span> <strong style = \"color:red\" class=\"primary-font\">");
            message.append(session.getCookies().read("username"));
            message.append("</strong>:");
            message.append("                                <span>");
            message.append(parms.get("usermsg"));
            message.append("</span>");
            message.append("                            </div>");
            message.append("                        </li>");
            log.add(message.toString());
        }
        for (String s : log){
            page.append(s);
            page.append('\n');
        }
        page.append("                    </ul>");
        page.append("                </div>");
        page.append("                <div class=\"panel-footer\">");
        page.append("                    <div class=\"input-group\">");
        page.append("<form method = \"get\" name=\"message\" action=\"\">");
        page.append("                        <input id=\"btn-input\" type=\"text\" class=\"form-control input-sm\" placeholder=\"Type your message here...\" name=\"usermsg\" />");
        page.append("                        <span class=\"input-group-btn\">");
        page.append("                            <button class=\"btn btn-warning btn-sm\" name=\"submitmsg\" id=\"btn-chat\">");
        page.append("                                Send</button>");
        page.append("                        </span>");
        page.append("</form>");
        page.append("                    </div>");
        page.append("                </div>");
        page.append("            </div>");
        page.append("        </div>");
        page.append("    </div>");
        page.append("	<div class=\"col-xs-2\"></div>");
        page.append("</div>");
        page.append("");
        page.append("    <script src=\"");
        page.append(allDir);
        page.append("/jquery.min.js\"></script>");
        page.append("    <script src=\"");
        page.append(allDir);
        page.append("/bootstrap.min.js\"></script>");
        page.append("  </body>");
        page.append("</html>");
        return newFixedLengthResponse( page.toString() );
    }
}
