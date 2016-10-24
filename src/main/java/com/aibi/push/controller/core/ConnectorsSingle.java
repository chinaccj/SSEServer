package com.aibi.push.controller.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenchaojian on 16/10/22.
 * 该实现仅支持 ：同一个用户开多个浏览器，当有服务端推送请求的时候，只能向一个浏览器／tab推送（随机）
 * 如果需要支持，服务点向同一个用户打开多浏览器／tab的情形，需要将connectors 存储AsyncContext修改成
 *    map<String,linklist<AsyncContext> > 结构。
 */
public class ConnectorsSingle {
    static Map<String,AsyncContext> map = new ConcurrentHashMap<String, AsyncContext>();
    static final long TIMEOUT = 30*60*1000;

    public static void put(final String uid,HttpServletRequest request, HttpServletResponse response){

        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");


        //如果浏览器刷新，需要删除之前的连接，把新的连接存起来
        connectCheck(uid);

        //建立新连接，并缓存
        final AsyncContext asynCtx = request.startAsync(request, response);
        System.out.println("hashcode = "+asynCtx.hashCode());
        asynCtx.setTimeout(TIMEOUT);
        map.put(uid, asynCtx);

        asynCtx.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                System.out.println("onComplete ^^");

            }

            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println("onTimeout");
                event.getAsyncContext().complete();
                map.remove(uid);

            }

            public void onError(AsyncEvent event) throws IOException {
                System.out.println("onError");

            }

            public void onStartAsync(AsyncEvent event) throws IOException {
                System.out.println("onStartAsync");

            }
        });

    }


    public static synchronized void connectCheck(String uid){
        if(map.containsKey(uid)){
            map.get(uid).complete();
            map.remove(uid);
            System.out.println("old connect exist ");
        }
    }

    /**
     *
     * @param uid
     * @param message
     * @return 0 推送成功
     *         1 对方不在线
     *         2 对方浏览器已经关闭，网络异常
     */
    public static int send(String uid,String message){
        AsyncContext asyncContext = map.get(uid);
        if(asyncContext != null){
            final PrintWriter writer;
            try {
                writer = asyncContext.getResponse().getWriter();
                writer.println("data: " + message + "\n");
                writer.flush();
                return 0;
            } catch (IOException e) {
               //客户端已经断开连接，需要清理缓存
                map.remove(uid);

                return 2;
            }
        }
        else {
            return 1;
        }

    }


}
