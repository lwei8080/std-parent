<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
  
</head>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
      	Error
      </h1>
      <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-chain-broken"></i> Error</a></li>
        <li class="active">访问资源错误</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="error-page">
        <div class="error-content">
          <h2><i class="fa fa-warning text-yellow"></i> 访问资源不存在或出现异常.</h2>
          <p>
                        如需帮助，请联系系统管理员。<a href="${Session["basePath"]}/manage/index">返回首页</a>
          </p>
        </div>
        <!-- /.error-content -->
      </div>
      <!-- /.error-page -->
    </section>
    <!-- /.content -->
  </div>
  <!-- /.content-wrapper -->

  <#include "/ftl/assembly/body.main-footer.ftl"> 
  <#include "/ftl/assembly/body.control-sidebar.ftl"> 
</div>
<!-- ./wrapper -->

<!-- Globle JS HTML -->
<#include "/ftl/assembly/body.common-bottom.ftl"> 
<!-- AdminLTE App -->
<script src="${AdminLTE}/dist/js/adminlte.min.js"></script>

</body>
</html>