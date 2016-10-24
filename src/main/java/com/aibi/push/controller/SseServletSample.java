package com.aibi.push.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yumo on 16/10/20.
 * 一个简单的sse sample
 */
@WebServlet(urlPatterns={"/asyncservlet2"}, asyncSupported=true)
public class SseServletSample extends HttpServlet {
    private Executor executor = Executors.newSingleThreadExecutor();
    private static AtomicBoolean isRunning = new AtomicBoolean(true);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        final AsyncContext asynCtx = request.startAsync(request, response);
        asynCtx.setTimeout(15000L);

        if(isRunning.get() == false){
            isRunning.set(true);
        }
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");
        executor.execute(new Runnable() {
            public void run() {
                boolean run = true;
                try {
                    while (run && isRunning.get()) {
                        final ServletResponse resp = asynCtx.getResponse();
                        run = resp != null;

                        if (resp != null) {
                            System.out.println("pushing a server event.");
                            final PrintWriter writer = asynCtx.getResponse().getWriter();
                            writer.println("data: {time: " + System.currentTimeMillis() + "}\n");
                            writer.flush();
                        } else {
                            System.out.println("stopping beeper, no response object available anymore.");
                            break; // do not run anymore, we got no response
                        }

                        Thread.sleep(2000);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });

        asynCtx.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                System.out.println("onComplete(...)");
            }

            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                // this will close the request and the context gracefully
                // and the net:ERR is gone.
                asyncEvent.getAsyncContext().complete();
                isRunning.set(false);
                System.out.println("onTimeout(...)");
            }

            public void onError(AsyncEvent asyncEvent) throws IOException {
                System.out.println("onError(...)");
            }

            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                System.out.println("onStart(...)");


            }
        });
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}