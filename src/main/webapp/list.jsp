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
    <script src="static/jquery.min.js"></script>
    <link href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {

            var listElement = $('#newStuff');
            var perPage = 8;
            var numItems = listElement.children().size();
            var numPages = Math.ceil(numItems / perPage);

            $('.pager').data("curr", 0);

            var curr = 0;
            $('<li class="previous"><a href="javascript:;">&laquo;</a></li>').appendTo('.pager');
            if($('#more_page').val()) {
                while (numPages > curr) {
                    $('<li><a href="javascript:;" class="page_link">' + (curr + 1) + '</a></li>').appendTo('.pager');
                    curr++;
                }
            }
            $('<li class="next"><a href="javascript:;">&raquo;</a></li>').appendTo('.pager');

            $('.pager .page_link:first').addClass('active');

            listElement.children().hide();
            listElement.children().slice(0, perPage).show();

            $('.pager li').not(".previous,.next").find("a").click(function () {
                var clickedPage = $(this).html().valueOf() - 1;
                goTo(clickedPage, perPage);
            });


            $('.pager li.previous a').click(function () {
                var goToPage = parseInt($('.pager').data("curr")) - 1;
                if (goToPage>-1)
                    goTo(goToPage);
            });

            $('.pager li.next a').click(function () {
                var goToPage = parseInt($('.pager').data("curr")) + 1;
                if (goToPage<numPages)
                    goTo(goToPage);
            });

            function goTo(page) {
                var startAt = page * perPage,
                        endOn = startAt + perPage;

                listElement.children().hide().slice(startAt, endOn).show();
                $('.pager').data("curr", page);
                var current_page = $('.pager li').filter(function (index) {
                    return $(this).find('a').html() == page + 1;
                });
                current_page.closest('ul').find('a').removeClass("active");
                current_page.find('a').addClass('active');
            }
        });
    </script>
    <style>
        .active {
            background-color: #286090 !important;
            border-color: #204d74 !important;
            color:#fff!important;
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
                            <input type="text" class="form-control" name="keyword" id="keyword" placeholder="请输入关键字...">
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
                <th style="width:33%;">哈希</th>
                <th style="width:33%;">标题</th>
                <th style="width:33%;">来源</th>
            </tr>
            </thead>
            <tbody id="newStuff">
            <c:forEach var="news" items="${newsList}">
                <tr>
                    <td>${news.hashkey}</td>
                    <td><a target="_blank" href="${news.url}">${news.title}</a></td>
                    <td>${news.source}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div class="text-center">
            <div class="pagination pagination-large row-fluid">
                <ul class="pager"></ul><div class="">共8页</div>
            </div>
        </div>
        <input type="hidden" id="more_page" value="${param.keyword}">
    </div>
</div>
</body>
</html>