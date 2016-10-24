package com.aibi.push.controller.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenchaojian on 16/10/22.
 * 支持同一个用户开多个浏览器，当有服务端推送请求的时候，可以向多个浏览器／tab推送请求
 *
 */
public class Connectors {
    static Map<String,Set<AsyncContext>> map = new ConcurrentHashMap<String, Set<AsyncContext>>();
    static final long TIMEOUT = 300*1000;

    public static void put(final String uid,HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");



        //建立新连接，并缓存
        final AsyncContext asynCtx = request.startAsync(request, response);
        System.out.println("hashcode = " + asynCtx.hashCode());
        asynCtx.setTimeout(TIMEOUT);

        //maybe course threadsafe problem
        if(map.containsKey(uid)){
            map.get(uid).add(asynCtx);
        }
        else {
            Set set = Collections.newSetFromMap(new ConcurrentHashMap<AsyncContext,Boolean>());
            set.add(asynCtx);
            map.put(uid, set);
        }

        asynCtx.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                System.out.println("onComplete ^^");

            }

            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println("onTimeout");
                event.getAsyncContext().complete();

                map.get(uid).remove(event.getAsyncContext());
                //test
                System.out.println("after timeout map size "+map.get(uid).size());

            }

            public void onError(AsyncEvent event) throws IOException {
                System.out.println("onError");

            }

            public void onStartAsync(AsyncEvent event) throws IOException {
                System.out.println("onStartAsync");

            }
        });

    }


    /**
     *
     * @param uid
     * @param message
     * @return 0 推送成功
     *         1 对方不在线或者 对方所有浏览器已经关闭，网络异常
     *
     */
    public static int send(String uid,String message){
        Set<AsyncContext> set = map.get(uid);
        int counter = 0;
        if(set != null) {
            for (Iterator<AsyncContext> ite = set.iterator(); ite.hasNext(); ) {
                AsyncContext asyncContext = ite.next();

                if (write(uid, asyncContext, message) == 0) {
                    counter++;
                }
            }
        }

        //没有一个客户端写成功
        if(counter ==0){
            return 1;
        }else {
            return 0;
        }
    }



    private static int write(String uid,AsyncContext asyncContext,String message){
        final PrintWriter writer;
        try {
            writer = asyncContext.getResponse().getWriter();
            writer.println("data: " + message + "\n");
            writer.flush();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            //客户端已经断开连接，需要清理缓存
            map.get(uid).remove(asyncContext);
            return -1;
        }
    }

}
