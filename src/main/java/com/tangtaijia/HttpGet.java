package com.tangtaijia;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Connection;

/**
 * Created by taijia on 4/1/16.
 */
public class HttpGet {
    public final static void getByString(String url, Connection conn) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            org.apache.http.client.methods.HttpGet httpget = new org.apache.http.client.methods.HttpGet(url);
            System.out.println("executing request " + httpget.getURI());

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
            String responseBody = httpclient.execute(httpget, responseHandler);
            /*
            //print the content of the page
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");
            */
            ParsePage.parseFromString(responseBody,conn);

        } finally {
            httpclient.close();
        }
    }
}
