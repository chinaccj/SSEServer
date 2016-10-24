package com.aibi.push.controller.core;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yumo on 16/10/21.
 */

@WebServlet(urlPatterns={"/push"}, asyncSupported=true)
public class PushServlet extends HttpServlet {


    @Override
    protected void doGet(final HttpServletRequest request, HttpServletResponse response) throws ServletException {

      String message = request.getParameter("message");
      String sid = request.getParameter("sid");
      if(Connectors.send(sid, message)!=0){
          System.out.println("sid ="+sid+" not on line");
      }


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        doGet(request,response);
    }
}
