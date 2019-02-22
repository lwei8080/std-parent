<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="referrer" content="always">
    <meta name="keywords" content="上网导航,网址大全,网址导航,化学网址导航,化学网址,化学导航,化学网址大全,化学活动">
    <meta name="description" content="学习X，从Y开始。">
    <title>Bootstrap ${title} Template</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

    <!--[if lt IE 9]>
      <script src="https://cdn.jsdelivr.net/npm/html5shiv@3.7.3/dist/html5shiv.min.js"></script>
      <script src="https://cdn.jsdelivr.net/npm/respond.js@1.4.2/dest/respond.min.js"></script>
    <![endif]-->
    
    <style type="text/css">
		a:hover {
		    color: #f50;
		    text-decoration: underline;
		}
		a {
		    color: #333;
		    text-decoration: none;
		}
		a {
		    color: #333;
		    text-decoration: none;
		}
		img.link-icon {
		    width: 16px;
		    height: 16px;
		}
		div.nav-link {
		    margin-bottom: 5px;
		}
    </style>
  </head>
  <body>
    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Academic Project</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <form class="navbar-form navbar-right" id="search-form-baidu" action="https://www.baidu.com/s" target="_blank">
		    <div class="form-group">
		      <a title="百度首页" href="http://www.baidu.com/?tn=siteacademic" target="_blank">
		        <img src="https://gss0.bdstatic.com/5bVWsj_p_tVS5dKfpU_Y_D3/res/r/image/2017-09-26/352f1d243122cf52462a2e6cdcb5ed6d.png" width="128px" height="32px">
		      </a>
		    </div>
		    
		    <div class="input-group input-group">
		      <input class="form-control" type="text" maxlength="100" name="word" placeholder="搜你想搜的">
		      <span class="input-group-btn">
		         <button type="submit" class="btn btn-info btn-flat">百度一下</button>
		      </span>
		    </div>
		    <input id="search_tn" type="hidden" value="siteacademic" name="tn">
		    <input type="hidden" value="utf-8" name="ie">
          </form>
        </div><!--/.navbar-collapse -->
      </div>
    </nav>
  
    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
        <h1>Professional site</h1>
        <p>This is a template for a simple marketing or informational website. It includes a large callout called a jumbotron and three supporting pieces of content. Use it as a starting point to create something more unique.</p>
        <p><a class="btn btn-primary btn-lg hide" href="#" role="button">See more &raquo;</a></p>
      </div>
    </div>

    <div class="container">
      <!-- Example row of columns -->
      <div class="row">
        <div class="col-xs-3 nav-link">
          <a href="#"><img src="https://gss1.bdstatic.com/5bVXsj_p_tVS5dKfpU_Y_D3/urlicon/8838.png" class="link-icon" /></a>
          <a href="#">凤 凰 网</a>
          <span class="text-muted"></span>
        </div>
        <div class="col-xs-3 nav-link">
          <a href="#"><img src="https://gss0.bdstatic.com/5bVYsj_p_tVS5dKfpU_Y_D3/urlicon/106051.png" class="link-icon" /></a>
          <a href="#">优 酷 网</a>
          <span class="text-muted">(荐)</span>
        </div>
        <div class="col-xs-3 nav-link">
          <a href="#">学 信 网</a>
          <span class="text-muted"></span>
        </div>
        <div class="col-xs-3 nav-link">
          <a href="#"><img src="https://gss0.bdstatic.com/5bVWsj_p_tVS5dKfpU_Y_D3/res/r/image/2017-10-01/6f2a8ddfb429e5bb3eba1e6b248ca1c8.png" class="link-icon" /></a>
          <a href="#">瓜子二手车</a>
          <span class="text-muted"></span>
        </div>
        <div class="col-xs-3 nav-link">
          <a href="#"><img src="https://gss1.bdstatic.com/5bVXsj_p_tVS5dKfpU_Y_D3/urlicon/8838.png" class="link-icon" /></a>
          <a href="#">
            fa-facebook-f
          </a>
          <span class="text-muted"></span>
        </div>
        <div class="col-xs-3 nav-link"><a href="#">更多>></a></div>
      </div>

      <hr>

      <footer>
        <p>&copy; 2016 Company, Inc.</p>
      </footer>
    </div> <!-- /container -->

    <script src="https://cdn.jsdelivr.net/npm/jquery@1.12.4/dist/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js"></script>
  </body>
</html>