package com.gx.mydairy.dairy.Util;


import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by GX on 2016/10/11.
 */

public class HttpRequestUtil {

    /**
     * 发送GET请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendGetRequest(String url, Map<String, String> params, Map<String, String> headers)
            throws Exception {
        StringBuilder buf = new StringBuilder(url);
        Set<Map.Entry<String, String>> entrys = null;
        // 如果是GET请求，则请求参数在URL中
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            entrys = params.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(buf.toString());
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("GET");
        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.getResponseCode();
        return conn;
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendPostRequest(String url, Map<String, String> params, Map<String, String> headers)
            throws Exception {
        StringBuilder buf = new StringBuilder();
        Set<Map.Entry<String, String>> entrys = null;
        // 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
        if (params != null && !params.isEmpty()) {
            entrys = params.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();
        out.write(buf.toString().getBytes("UTF-8"));
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (conn.getResponseCode() == HttpStatus.SC_OK) {
            // 为了发送成功
            return conn;
        }
        return null;
    }

    /**
     * 将输入流转为字符串
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static String readString(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return new String(outSteam.toByteArray(), "UTF-8");
    }

    /*
                try {

                    //创建一个JSON对象

                    JSONObject jsonObject = new JSONObject(result.toString()).getJSONObject("parent");

                    //获取某个对象的JSON数组

                    JSONArray jsonArray = jsonObject.getJSONArray("children");


                    StringBuilder builder = new StringBuilder();


                    for (int i = 0; i < jsonArray.length(); i++) {

                        //新建一个JSON对象，该对象是某个数组里的其中一个对象

                        JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);


                        builder.append(jsonObject2.getString("id")); //获取数据

                        builder.append(jsonObject2.getString("name"));

                    }

                    Toast.makeText(getApplicationContext(), builder.toString(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                }
            */
    /*
            HttpGet httpRequest = new HttpGet(UtilToos.toISO_8859_1(params[0].toString()));

            try {

                HttpClient httpClient = new DefaultHttpClient();

                HttpResponse httpResponse = httpClient.execute(httpRequest);


                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    String strResult = EntityUtils.toString(httpResponse.getEntity());

                    return strResult;

                } else {

                    return "请求出错";

                }

            } catch (IOException e) {

                return "请求出错";

            }
            */


}
