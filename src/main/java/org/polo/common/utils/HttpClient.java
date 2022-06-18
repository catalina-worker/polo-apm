package org.polo.common.utils;


import org.polo.common.logger.Log;
import org.polo.common.logger.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @Auther: Tommy
 * @Date: 2018\10\5 0005
 * @Description:
 */
public class HttpClient {
    static Log logger = LogFactory.getLog(HttpClient.class);

    public static String execHttp(String url, Map<String, String> params) throws IOException {
        // url 编码
        StringBuffer param = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (param.length() > 0) {
                param.append("&");
            }
            param.append(entry.getKey());
            param.append("=");
            param.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } finally { //使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex);
            }
        }


    }
}
