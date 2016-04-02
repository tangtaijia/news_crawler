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
import java.nio.charset.Charset;
import java.sql.*;

/**
 * 解析类
 */
public class ParsePage {
    final static Logger logger = Logger.getLogger(ParsePage.class);

    /**
     * 解析并入库
     * @param content
     * @param conn
     * @param originUrl
     * @param source
     * @throws Exception
     */
    public static void parseFromString(String content, Connection conn,String originUrl,String source) throws Exception {
        Parser parser = new Parser(content);
        HasAttributeFilter filter = new HasAttributeFilter("href");

        try {
            NodeList list = parser.parse(filter);
            int count = list.size();

            //process every link on this page
            for(int i=0; i<count; i++) {
                Node node = list.elementAt(i);
                if(node instanceof LinkTag) {
                    LinkTag link = (LinkTag) node;
                    String nextlink = link.extractLink();
                    String nextTitle = link.getLinkText();
                    if(nextlink.startsWith(originUrl) && !Utils.isEmpty(nextTitle) && !nextlink.contains("script") && Utils.containsHanScript(nextTitle)) {
                        if(!nextlink.contains("http")) {
                            nextlink = originUrl + nextlink;
                        }
                        String sql = null;
                        ResultSet rs = null;
                        PreparedStatement pstmt = null;
                        Statement stmt = null;
                        String tag = null;

                        try {
                            //check if the link already exists in the database
                            sql = "SELECT * FROM record WHERE URL = '" + nextlink + "'";
                            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
                            rs = stmt.executeQuery(sql);

                            if(!rs.next()) {
                                //if the link does not exist in the database, insert it
                                sql = "INSERT INTO record (URL, crawled) VALUES ('" + nextlink + "',0)";
                                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                pstmt.execute();
                                System.out.print(sql);
                                System.out.println(nextlink.contains("comment"));
                                try {
                                    nextTitle = URLDecoder.decode(nextTitle,"UTF-8");
                                    if(nextlink.contains("sina"))
                                        nextTitle = new String(nextTitle.getBytes("GBK"), "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    nextTitle = null;
                                }
                                System.out.println(nextlink);
                                System.out.println(nextTitle);
                                System.out.println(nextlink.startsWith(originUrl));
                                if (nextlink.startsWith(originUrl) &&
                                        !nextlink.contains("comment") && nextTitle.trim().length() > 10
                                        ) {
                                    sql = "INSERT INTO news (hashkey,title,url,source) VALUES ('" +
                                            Utils.hash(nextlink) + "', '" + nextTitle + "', '" + nextlink + "', '" + source +
                                            "') ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)";

                                    pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                    //if the links are different from each other, the tags must be different
                                    //so there is no need to check if the tag already exists
                                    pstmt.execute();
                                    logger.debug("news saved : " + nextTitle);
                                    logger.debug(sql);
                                    System.out.print(sql);
                                }
                            }
                        } catch (SQLException e) {
                            //handle the exceptions
                            System.out.println("SQLException: " + e.getMessage());
                            System.out.println("SQLState: " + e.getSQLState());
                            System.out.println("VendorError: " + e.getErrorCode());
                        } finally {
                            //close and release the resources of PreparedStatement, ResultSet and Statement
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
