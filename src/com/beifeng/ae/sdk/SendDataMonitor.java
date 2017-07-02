package com.beifeng.ae.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 发送url数据的监控者，同时启动一个单独的线程来发送数据
 * Created by Administrator on 2017/6/29.
 */
public class SendDataMonitor {

    //日志对象
    private static final Logger log = Logger.getGlobal();

    //队列对象，用于存储url
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    //用于单例模式类对象
    private static SendDataMonitor instance = null;

    private SendDataMonitor() {
    }

    /**
     * 获取SendDataMonitor对象
     *
     * @return
     */
    public static SendDataMonitor getInstance() {
        //判断两次，用于多线程的情况
        if (instance == null) {
            synchronized (SendDataMonitor.class) {
                if (instance == null) {
                    instance = new SendDataMonitor();

                    //创建线程，用于发送url数据
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //线程中调用具体的处理方法
                            instance.run();
                        }
                    });

                    // 将该线程设置为守护线程，测试时不建议设置
                    //thread.setDaemon(true);

                    thread.start();
                }
            }
        }

        return instance;
    }

    /**
     * 添加一个url到队列中
     *
     * @param url
     * @throws InterruptedException
     */
    public static void addSendUrl(String url) throws InterruptedException {
        getInstance().queue.put(url);
    }

    /**
     * 具体执行发送url的方法
     */
    private void run() {
        while (true) {
            try {
                String url = this.queue.take();
                //正式发送url
                HttpRequestUtil.sendData(url);
            } catch (Exception e) {
                log.log(Level.WARNING, "发送URL异常");
            }
        }
    }

    /**
     * 内部类，用于发送数据的HTTP工具类
     */
    public static class HttpRequestUtil {

        /**
         * 具体发送url的方法
         *
         * @param url
         */
        public static void sendData(String url) throws IOException {

            HttpURLConnection conn = null;
            BufferedReader in = null;
            try {
                //创建URL对象
                URL obj = new URL(url);
                //打开URL连接
                conn = (HttpURLConnection) obj.openConnection();
                //设置连接参数
                //设置连接过期时间，单位为ms
                conn.setConnectTimeout(5000);
                //设置数据读取过期时间，单位为ms
                conn.setReadTimeout(5000);
                //设置请求类型，为GET
                conn.setRequestMethod("GET");

                System.out.println("发送url:" + url);
                //发送连接请求
                //conn.connect();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            }  finally {
                try {
                    if(in != null){
                        in.close();
                    }

                } catch (Exception e) {
                    //nothing
                }

                try{
                    if(conn != null){
                        conn.disconnect();
                    }
                }catch (Exception e){
                    //nothing
                }
            }
        }
    }
}
