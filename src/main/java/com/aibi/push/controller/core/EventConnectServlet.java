package com.aibi.push.controller.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yumo on 16/10/21.
 */
@WebServlet(urlPatterns={"/eventconnect"}, asyncSupported=true)
public class EventConnectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        final String sessionId = request.getSession().getId();
        System.out.println(sessionId);
        Connectors.put(sessionId, request, response);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}