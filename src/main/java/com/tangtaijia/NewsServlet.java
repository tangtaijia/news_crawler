
package com.tangtaijia;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Enumeration;

import javax.servlet.http.*;
import javax.servlet.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class NewsServlet extends HttpServlet {
    public void doGet(HttpServletRequest req,
                      HttpServletResponse res)
            throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher("list.jsp");

        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/application.properties");
        prop.load(in);
        in.close();
        try {
            Class.forName(prop.getProperty("datasource.driverClassName"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(prop.getProperty("datasource.url"), prop.getProperty("datasource.username"), prop.getProperty("datasource.password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Statement stmt = null;
        String query = "SELECT * FROM news;";

        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            List<News> newsList = new ArrayList<News>();
            while (rs.next()) {
                News news = new News();
                news.setTitle(rs.getString("title"));
                news.setHashkey(rs.getString("hashkey"));
                news.setUrl(rs.getString("url"));
                news.setSource(rs.getString("source"));
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

        rd.forward(req, res);
    }
}