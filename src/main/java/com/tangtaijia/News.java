package com.tangtaijia;

/**
 * 新闻类
 */
public class News {
    /**
     * 哈希值(唯一性)
     */
    private String hashkey;
    /**
     * 标题
     */
    private String title;
    /**
     * 链接
     */
    private String url;
    /**
     * 来源
     */
    private String source;
    /**
     * 创建时间
     */
    private String createTime;

    public String getHashkey() {
        return hashkey;
    }

    public void setHashkey(String hashkey) {
        this.hashkey = hashkey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}