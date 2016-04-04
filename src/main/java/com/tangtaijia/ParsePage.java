package com.tangtaijia;

import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.net.URLDecoder;
import java.sql.*;

/**
 * 解析类
 */
public class ParsePage {
    final static Logger logger = Logger.getLogger(ParsePage.class);

    /**
     * 解析并入库
     * @param content 内容
     * @param conn 数据库链接
     * @param originUrl 根url
     * @param source 来源
     * @throws Exception
     */
    public static void parseFromString(String content, Connection conn,String originUrl,String source) throws Exception {
        Parser parser = new Parser(content);
        HasAttributeFilter filter = new HasAttributeFilter("href"); // 过滤所有链接

        try {
            NodeList list = parser.parse(filter);
            int count = list.size();

            // 解析所有链接
            for (int i = 0; i < count; i++) {
                Node node = list.elementAt(i);
                if(node instanceof LinkTag) {
                    LinkTag link = (LinkTag) node;
                    String nextlink = link.extractLink(); // 下一个链接
                    String nextTitle = link.getLinkText(); // 下一个链接文字
                    // 以根链接为准进去过滤，链接非空不含script和乱码
                    if(nextlink.startsWith(originUrl) && !Utils.isEmpty(nextTitle) && !nextlink.contains("script") && Utils.containsHanScript(nextTitle)) {
                        // 如果链接没有http则补上
                        if(!nextlink.contains("http")) {
                            nextlink = originUrl + nextlink;
                        }
                        String sql = null;
                        ResultSet rs = null;
                        PreparedStatement pstmt = null;
                        Statement stmt = null;

                        try {
                            // 检查链接是否存在于record表，否则插入
                            sql = "SELECT * FROM record WHERE URL = '" + nextlink + "'";
                            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
                            rs = stmt.executeQuery(sql);

                            if(!rs.next()) {
                                // 不存在则插入
                                sql = "INSERT INTO record (URL, crawled) VALUES ('" + nextlink + "',0)";
                                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                pstmt.execute();
                                System.out.print(sql);
                                System.out.println(nextlink.contains("comment"));
                                try {
                                    nextTitle = URLDecoder.decode(nextTitle,"UTF-8"); // 把标题转为UTF8编码
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    nextTitle = null;
                                }
                                System.out.println(nextlink);
                                System.out.println(nextTitle);
                                if (nextlink.startsWith(originUrl) &&
                                        !nextlink.contains("comment") && nextTitle.trim().length() > 10
                                        ) {
                                    sql = "INSERT INTO news (hashkey,title,url,source,create_time) VALUES ('" +
                                            Utils.hash(nextlink) + "', '" + nextTitle + "', '" + nextlink + "', '" + source +
                                            "',CURRENT_TIMESTAMP ) ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)";

                                    pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                    //if the links are different from each other, the tags must be different
                                    //so there is no need to check if the tag already exists
                                    pstmt.execute();
                                    logger.debug("存入新闻 : " + nextTitle);
                                    logger.debug(sql);
                                    System.out.print(sql);
                                }
                            }
                        } catch (SQLException e) {
                            // 处理异常
                            System.out.println("SQLException: " + e.getMessage());
                            System.out.println("SQLState: " + e.getSQLState());
                            System.out.println("VendorError: " + e.getErrorCode());
                        } finally {
                            // 关闭资源
                            if(pstmt != null) {
                                try {
                                    pstmt.close();
                                } catch (SQLException e2) {}
                            }
                            pstmt = null;

                            if(rs != null) {
                                try {
                                    rs.close();
                                } catch (SQLException e1) {}
                            }
                            rs = null;

                            if(stmt != null) {
                                try {
                                    stmt.close();
                                } catch (SQLException e3) {}
                            }
                            stmt = null;
                        }

                    }
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
