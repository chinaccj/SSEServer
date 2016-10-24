package com.aibi.push.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yumo on 16/10/18.
 * 一个简单的sse sample
 */
@Controller
@RequestMapping("/")

public class EventsController {
    private static Map<String,SseEmitter> sseEmitterMap= new ConcurrentHashMap<String,SseEmitter>();
    private static final long TIME_OUT = 30000L;

//    @RequestMapping(value ="/events/connect",method = RequestMethod.GET)
//    public SseEmitter openConnection(HttpSession session){
//
//        final String sessionId = session.getId();
//        System.out.println(sessionId);
//
//        if(sseEmitterMap.containsKey(sessionId)){
//            sseEmitterMap.get(sessionId).complete();
//            sseEmitterMap.remove(sessionId);
//        }
//
//        if(!sseEmitterMap.containsKey(sessionId)){
//            //服务端设置多少ms 回收连接
//            final SseEmitter sseEmitter = new SseEmitter(TIME_OUT);
//            sseEmitter.onCompletion(new Runnable() {
//                public void run() {
//                    System.out.println("remove sse emitter from map");
////                    sseEmitter.complete();
//                    sseEmitterMap.remove(sessionId);
//                    System.out.println("map size=" + sseEmitterMap.size());
//                }
//            });
//            sseEmitterMap.put(sessionId,sseEmitter);
//            System.out.println("sse created in map");
//        }
//
//        System.out.println(System.currentTimeMillis()/1000+"open connection");
//        return sseEmitterMap.get(sessionId);
//    }



    @RequestMapping(value ="/events/broadcast",method = RequestMethod.GET)
    public String broadcastMessage(Model model,@RequestParam("eventMsg") String msg)throws IOException{

        if(!sseEmitterMap.isEmpty()) {
            Set<String> set = sseEmitterMap.keySet();
            for(Iterator<String> ite=set.iterator();ite.hasNext();){
                String sid = ite.next();
                try {
                    //如果客户端掉线，发数据会抛网络异常
                 sseEmitterMap.get(sid).send(msg, MediaType.TEXT_PLAIN);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            model.addAttribute("successMsg", "Message boardcast");
        }else {
            model.addAttribute("successMsg", "client timeout");

        }
        return "publishEvent";
    }

    @RequestMapping(value ="events/subscribe",method = RequestMethod.GET)
    public ModelAndView showSubscribePage(Model model)throws IOException{
        return new ModelAndView("subscribeEvent");
    }

    @RequestMapping(value ="events/publish",method = RequestMethod.GET)
    public ModelAndView pulish(Model model)throws IOException{
        return new ModelAndView("publishEvent");
    }


//    @RequestMapping(path = "api/events", method = RequestMethod.GET)
//    public SseEmitter getEvents() throws Exception {
//        final SseEmitter emitter = new SseEmitter();
//        final ScheduledExecutorService pingThread = Executors.newScheduledThreadPool(1);
//        emitter.onCompletion(new Runnable() {
//            public void run() {
//                System.out.println("Complete");
//                pingThread.shutdown();
//            }
//        });
//
//        emitter.onTimeout(new Runnable() {
//            public void run() {
//                System.out.println("Complete");
//            }
//        });
//        pingThread.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                try {
//                    emitter.send(SseEmitter.event().data("Hello").name("ping"));
//                } catch (Exception e) {
//                    System.out.println("Unable to emit");
//                    throw new RuntimeException(e);
//                }
//            }
//        }, 1000, 1000, TimeUnit.MILLISECONDS);
//        return emitter;
//    }
}
