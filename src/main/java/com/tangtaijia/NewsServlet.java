
package com.tangtaijia;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;
import javax.servlet.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * 新闻servlet
 */
public class NewsServlet extends HttpServlet {
    public void doGet(HttpServletRequest req,
                      HttpServletResponse res)
            throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher("list.jsp");

        DBbean dBbean = Utils.getDBbean();
        try {
            Class.forName(dBbean.getDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(dBbean.getUrl(), dBbean.getName(), dBbean.getPwd());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Statement stmt = null;
        String keyword = req.getParameter("keyword"); // 关键字
        // 获取所有数据并按照id逆序
        String query = "SELECT * FROM news";
        if(keyword != null && keyword != "")
            query += " WHERE title like '%"+keyword+"%'";
        query += " ORDER BY id DESC";
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            List<News> newsList = new ArrayList<News>();
            // 组装新闻list
            while (rs.next()) {
                News news = new News();
                news.setTitle(rs.getString("title"));
                news.setHashkey(rs.getString("hashkey"));
                news.setUrl(rs.getString("url"));
                news.setSource(rs.getString("source"));
                news.setCreateTime(rs.getString("create_time"));
                newsList.add(news);
            }
            req.setAttribute("newsList",newsList);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(stmt != null)
                    stmt.close();
                if(connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 把新闻传到jsp前台
        rd.forward(req, res);
    }

}