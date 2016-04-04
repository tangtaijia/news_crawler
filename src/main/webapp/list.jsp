<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title> 爬虫新闻列表 </title>
    <link href="static/bootstrap.min.css" rel="stylesheet">
    <link href="static/qunit-1.11.0.css" rel="stylesheet">
    <script src="static/jquery.min.js"></script>
    <script src="static/bootstrap.min.js"></script>
    <script src="static/bootstrap-paginator.js"></script>
    <script src="static/qunit-1.11.0.js"></script>
    <script type="text/javascript">
        $(function(){
            var pagination = $('.pager');
            var listElement = $('#newStuff');
            var perPage = 8;
            var numItems = listElement.children().size();
            var numPages = Math.ceil(numItems / perPage);
            listElement.children().hide().slice( 0,perPage ).show();

            var options = {
                bootstrapMajorVersion:3,
                numberOfPages: 5,
                totalPages:numPages,
                onPageClicked: function(e,originalEvent,type,page){
                    var to_page = page - 1;
                    listElement.children().hide().slice(to_page * perPage, to_page * perPage + perPage).show();
                }
            }
            pagination.bootstrapPaginator(options);
        });


    </script>
    <style>
        .pagination li a {
            cursor: pointer;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="col-md-12">
        <div class = "page-header">
            <div class="text-center">
                <h2>爬虫列表</h2>
                <form class="form-horizontal" action="news" method="get" role="form">
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="keyword">关键字:</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="keyword" id="keyword" value="${param.keyword}" placeholder="请输入关键字...">
                        </div>
                        <div class="col-sm-3 text-left">
                            <button type="submit" class="btn btn-default">搜索</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <table class="table table-hover">
            <thead>
            <tr>
                <th style="width:35%;">哈希</th>
                <th style="width:35%;">标题</th>
                <th style="width:15%;">来源</th>
                <th style="width:15%;">时间</th>
            </tr>
            </thead>
            <tbody id="newStuff">
            <c:forEach var="news" items="${newsList}">
                <tr>
                    <td>${news.hashkey}</td>
                    <td><a target="_blank" href="${news.url}">${news.title}</a></td>
                    <td>${news.source}</td>
                    <td>${news.createTime}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div class="text-center">
            <div class="pagination pagination-large">
                <ul class="pager"></ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>