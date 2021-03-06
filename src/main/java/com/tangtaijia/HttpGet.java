package com.tangtaijia;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Connection;

/**
 * url访问类
 */
public class HttpGet {
    /**
     * 处理url
     * @param url 链接
     * @param originUrl 根链接
     * @param source 来源
     * @param conn 数据库链接
     * @throws Exception
     */
    public final static void getByString(String url,String originUrl,String source, Connection conn) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            org.apache.http.client.methods.HttpGet httpget = new org.apache.http.client.methods.HttpGet(url);
            System.out.println("爬取链接： " + httpget.getURI());
            // 设置header
            Header header = new BasicHeader(
                    "User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;  .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)");
            httpget.addHeader(header);

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            // 执行爬取并返回页面内容
            String responseBody = httpclient.execute(httpget, responseHandler);
            /*
            //print the content of the page
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");
            */
            // 解析页面内容并入库
            ParsePage.parseFromString(responseBody,conn,originUrl,source);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }
}
