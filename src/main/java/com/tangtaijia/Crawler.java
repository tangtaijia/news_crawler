package com.tangtaijia;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 爬虫
 */
public class Crawler implements Runnable {
    private String url = "";
    private String originUrl = "";
    private String source = "";
    final static Logger logger = Logger.getLogger(Crawler.class);
    public Crawler() {}
    public Crawler(String url,String originUrl,String source) {
        this.url = url;
        this.originUrl = originUrl;
        this.source = source;
    }

    /**
     * 处理进程
     * @throws Exception
     */
    private void progress() throws Exception {
        Connection conn = null;

        //connect the MySQL database
        DBbean dBbean = Utils.getDBbean();
        try {
            Class.forName(dBbean.getDriver());
            conn = DriverManager
                    .getConnection(dBbean.getUrl(), dBbean.getName(), dBbean.getPwd());
            System.out.println("connection built");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String sql = null;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;

        if(conn != null) {
            //crawl every link in the database
            while(true) {
                //get page content of link "url"
                synchronized (url) {
                    HttpGet.getByString(url,originUrl,source,conn);
                }
                Thread.sleep(2000);
                count++;

                sql = "SELECT * FROM record WHERE URL = '" + url + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                if (!rs.next()) {
                    sql = "INSERT INTO record (URL, crawled) VALUES ('" + url + "',0)";
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.executeUpdate(sql);
                }
                //set boolean value "crawled" to true after crawling this page
                sql = "UPDATE record SET crawled = 1 WHERE URL = '" + url + "' AND crawled=0";
                stmt = conn.createStatement();
                int updatedrows = stmt.executeUpdate(sql);

                logger.debug(sql);
                //get the next page that has not been crawled yet
                sql = "SELECT * FROM record WHERE crawled = 0 AND URL LIKE '" + originUrl + "%'";
                stmt = conn.createStatement();
                System.out.println(sql);
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    synchronized (url) {
                        url = rs.getString(2);
                    }
                } else {
                    //stop crawling if reach the bottom of the list
                    System.out.println("at the bottom of list");
                    break;
                }

                //set a limit of crawling count
                if (count > 10000 || url == null) {
                    System.out.println("out of limit");
                    break;
                }
            }
            conn.close();
            conn = null;

            System.out.println("Done.");
            System.out.println(count);
        }
    }

    @Override
    public void run() {
        try {
            this.progress();
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Properties prop = new Properties();
        InputStream in = new Utils().getClass().getResourceAsStream("/application.properties");
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Enumeration e = prop.propertyNames();

        /**
         * 有几个url就启动几个线程
         */
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if(key.startsWith("news")) {
                System.out.println(key + " -- " + prop.getProperty(key));
                String[] values  =key.split("\\.");
                Runnable crawler = new Crawler(prop.getProperty(key),prop.getProperty(key),values[1]);
                Thread thread = new Thread(crawler);
                thread.start();
            }
        }
    }


}
