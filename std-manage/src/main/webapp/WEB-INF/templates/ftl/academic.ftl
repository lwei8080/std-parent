<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="academic">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
                导航页面生成
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 导航页面生成管理</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>
    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-academic-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">添加或修改网站导航</h3>
            </div>
            <!-- /.box-header -->
            <!-- form start -->
            <form class="form-horizontal">
              <div class="box-body">

              </div>
              <!-- /.box-body -->
              <div class="box-footer">
              	<div class="btn-group col-sm-offset-2">
              		<button type="button" id="editSave" class="btn btn-info pull-left btn-flat margin">保 存</button>
              		<button type="button" id="editReturn" class="btn btn-warning pull-left btn-flat margin">返 回</button>
              	</div>
              </div>
              <!-- /.box-footer -->
            </form>
          </div>
      	</div>
      
        <div class="col-md-12 show" id="module-academic-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="account" class="col-sm-1 control-label">网站名</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="account" placeholder="网站名">
		              </div>
		              
		              <label for="mobile" class="col-sm-1 control-label">网址</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="mobile" placeholder="网址">
		              </div>
		              
		              <label for="state" class="col-sm-1 control-label">状态</label>
					  <div class="col-sm-3">
					  	<select class="form-control" id="state">
					      <option value="" selected="selected">-----全部-----</option>
					      <option value="1">有效</option>
					      <option value="0">无效</option>
					    </select>
					  </div>
		            </div>
					<div class="btn-group col-sm-offset-1">
						<button type="button" id="academicQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addAcademic" class="btn btn-danger btn-flat pull-left margin">新 增</button>
						<button type="button" id="makeAcademic" class="btn bg-purple btn-flat pull-left margin">生 成</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered" id="academic-list">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>账号</th>
                    <th>手机号</th>
                    <th>创建时间</th>
                    <th>更新时间</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  
                </tbody>
              </table>
            </div>
            <!-- /.box-body -->
            <div class="box-footer clearfix">
              <ul class="pagination pagination-sm no-margin pull-right"></ul>
            </div>
          </div>
          <!-- /.box -->
        </div>
      </div>
      <!-- /.row -->
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
<script src="${Custom}/js/component-pagination.js"></script>
<script src="${Custom}/js/biz-academic.js"></script>
</body>
</html>