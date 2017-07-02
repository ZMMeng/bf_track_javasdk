package com.beifeng.ae.sdk;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 分析引擎sdk java服务器端数据收集
 * Created by Administrator on 2017/6/29.
 */
public class AnalyticsEngineSDK {

    // 日志打印对象
    private static final Logger log = Logger.getGlobal();

    // 请求URL主体部分
    public static final String accessUrl = "http://hadoop/BfImg.gif";
    private static final String platformName = "java_server";
    private static final String sdkName = "jdk";
    private static final String version = "1";

    /**
     * 触发订单支付成功事件，发送事件数据到服务器
     *
     * @param orderId  订单支付id
     * @param memberId 订单支付会员id
     * @return 如果发送数据成功(加入到发送队列中)，返回true，否则返回false(参数异常或添加到发送队列失败)
     */
    public static boolean onChargeSuccess(String orderId, String memberId) {
        if (isEmpty(orderId) || isEmpty(memberId)) {
            log.log(Level.WARNING, "订单id或会员id不能为空！");
            return false;
        }

        // 订单id和会员id均非空
        Map<String, String> data = new HashMap<String, String>();
        data.put("u_mid", memberId);
        data.put("oid", orderId);
        data.put("c_time", String.valueOf(System.currentTimeMillis()));
        data.put("ver", version);
        data.put("en", "e_cs"); // 表示触发的是订单支付成功事件
        data.put("pl", platformName);
        data.put("sdk", sdkName);

        //创建url
        try {
            String url = buildUrl(data);

            //发送url，即将url加入到队列中
            //TODO:
            SendDataMonitor.addSendUrl(url);
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "发送数据异常", e);
            return false;
        }

    }

    /**
     * 触发订单退款事件，发送退款数据到服务器
     *
     * @param orderId  退款订单id
     * @param memberId 退款会员id
     * @return 如果发送数据成功，则返回true，否则返回false
     */
    public static boolean onChargeRefund(String orderId, String memberId) {
        if (isEmpty(orderId) || isEmpty(memberId)) {
            log.log(Level.WARNING, "退款订单id或退款会员id不能为空！");
            return false;
        }

        // 订单id和会员id均非空
        Map<String, String> data = new HashMap<String, String>();
        data.put("u_mid", memberId);
        data.put("oid", orderId);
        data.put("c_time", String.valueOf(System.currentTimeMillis()));
        data.put("ver", version);
        data.put("en", "e_cr"); // 表示触发的是退款事件
        data.put("pl", platformName);
        data.put("sdk", sdkName);

        //创建url
        try {
            String url = buildUrl(data);

            //发送url，即将url加入到队列中
            //TODO:
            SendDataMonitor.addSendUrl(url);
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "发送数据异常", e);
            return false;
        }
    }

    /**
     * 根据传入的参数构建url
     *
     * @param data 参数集，放置在Map中
     * @return
     */
    private static String buildUrl(Map<String, String> data) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append(accessUrl).append("?");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (isNotEmpty(entry.getKey()) && isNotEmpty(entry.getValue())) {
                sb.append(entry.getKey().trim()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
            }
        }
        // accessUrl?c_time=xxx&u_mid=yyy
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 判断字符串是否为空
     *
     * @param value
     * @return 如果为空，返回true，否则返回false
     */
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空
     *
     * @param value
     * @return 如果非空，返回true，否则返回false
     */
    private static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

}
