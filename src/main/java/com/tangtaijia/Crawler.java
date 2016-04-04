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
            System.out.println("数据库已连接");
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
            // 对record表中的所有链接进行爬取
            while(true) {
                // 获取url中的内容并入库
                synchronized (url) {
                    HttpGet.getByString(url,originUrl,source,conn);
                }
                Thread.sleep(2000);
                count++;

                // 如果record表中不存在该url，则插入
                sql = "SELECT * FROM record WHERE URL = '" + url + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                if (!rs.next()) {
                    sql = "INSERT INTO record (URL, crawled) VALUES ('" + url + "',0)";
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.executeUpdate(sql);
                }
                // 爬虫执行后对该url改为已爬取
                sql = "UPDATE record SET crawled = 1 WHERE URL = '" + url + "' AND crawled=0";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                logger.debug(sql);
                // 获取所有未爬取的记录
                sql = "SELECT * FROM record WHERE crawled = 0 AND URL LIKE '" + originUrl + "%'";
                stmt = conn.createStatement();
                System.out.println(sql);
                rs = stmt.executeQuery(sql);
                // 如果存在，则更新当前的url
                if (rs.next()) {
                    synchronized (url) {
                        url = rs.getString(2);
                    }
                } else {
                    System.out.println("爬虫停止：所有链接都跑完了");
                    break;
                }

                // 设置一个爬取上限，暂定为10000
                if (count > 10000 || url == null) {
                    System.out.println("爬虫停止:已达上限");
                    break;
                }
            }
            conn.close();
            conn = null;

            System.out.println("完成.");
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
        // 读取properties中的信息
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
         * 有几个url就启动几个爬虫线程
         */
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith("news")) {
                System.out.println(key + " -- " + prop.getProperty(key));
                String[] values = key.split("\\.");
                Runnable crawler = new Crawler(prop.getProperty(key), prop.getProperty(key), values[1]);
                Thread thread = new Thread(crawler);
                thread.start();
            }
        }
    }


}
