package com.tangtaijia;

/**
 * 数据库对象
 */
 public class DBbean {
    /**
     * 驱动
     */
     private String driver;
    /**
     * 用户名
     */
     private String name;
    /**
     * 密码
     */
     private String pwd;
    /**
     * 数据库url
     */
     private String url;

     public String getDriver() {
         return driver;
     }

     public void setDriver(String driver) {
         this.driver = driver;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public String getPwd() {
         return pwd;
     }

     public void setPwd(String pwd) {
         this.pwd = pwd;
     }

     public String getUrl() {
         return url;
     }

     public void setUrl(String url) {
         this.url = url;
     }
 }