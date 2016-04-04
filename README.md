# news_crawler
a simple crawler for news with httpclient, htmlarser, servlet, jsp and jdbc(mysql), just for fun.

# 环境：

1. tomcat 安装好后把crawlerweb.war放到tomcat下的webapps文件下
2. mysql 安装好后，把设置的数据库用户名和密码在项目中的application.properties相应的修改一下，
         也可以对mysql设置为application.properties文件中一样的用户名密码。
         完了后把db.sql中大语句在mysql中跑一遍建立下数据库和表
3. maven 安装一下
4. eclipse 安装一下，并把crawlerweb.tar.gz解压后作为项目引入

# 步骤：

1. 先执行2.mysql步骤，安装并初始化数据库(最好和application.properties的用户名密码一致)
2. 再执行3.maven的安装
3. 在执行4.eclipse的安装，并跑下下Crawler.java的main方法执行网页爬去与入库
4. 执行1.tomcat的安装与部署web项目，完成后访问http://localhost:8080/crawlerweb/news就能看到
  爬虫爬取的结果了，可以根据关键字搜索想要的新闻。
