<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="manager-role">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
                账号角色
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 账号角色</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-manager-role-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">设置账号角色</h3>
            </div>
            <!-- /.box-header -->
            <!-- form start -->
            <form class="form-horizontal">
              <div class="box-body">
                <div class="row">
                  <div class="col-sm-12">
                    <div class="col-sm-12">
	                  <label for="edit-manager">账号</label>
	                  <select class="form-control select2" style="width: 100%;" id="edit-manager">
	                    <option value="" disabled="disabled" selected="selected">-----选择账号-----</option>
	                  </select>
                    </div>
                    <div class="col-sm-12">
                      <label>角色</label>
                      <div class="box box-default box-solid">
	                    <div id="edit-role-array" class="box-body"></div>
	                  </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- /.box-body -->
              <div class="box-footer">
              	<div class="btn-group col-sm-12">
              		<button type="button" id="editSave" class="btn btn-info btn-flat margin">保 存</button>
              		<button type="button" id="editReturn" class="btn btn-warning btn-flat margin">返 回</button>
              	</div>
              </div>
              <!-- /.box-footer -->
            </form>
          </div>
      	</div>
      
        <div class="col-md-12 show" id="module-manager-role-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="account" class="col-sm-1 control-label">账号</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="account" placeholder="账号">
		              </div>

		              <label for="roleName" class="col-sm-1 control-label">角色名</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="roleName" placeholder="角色名">
		              </div>
		              
		              <label for="managerState" class="col-sm-1 control-label">状态</label>
					  <div class="col-sm-3">
					  	<select class="form-control" id="managerState">
					      <option value="" selected="selected">-----全部-----</option>
					      <option value="1">有效</option>
					      <option value="0">无效</option>
					    </select>
					  </div>
		            </div>
					<div class="btn-group col-sm-offset-1">
						<button type="button" id="managerRoleQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addManagerRole" class="btn btn-danger btn-flat pull-left margin">设 置</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered" id="manager-role-list">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>账号</th>
                    <th>账号状态</th>
                    <th>角色名</th>
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
<!-- Select2 -->
<script src="${AdminLTE}/bower_components/select2/dist/js/select2.full.min.js"></script>
<!-- iCheck 1.0.1 -->
<script src="${AdminLTE}/plugins/iCheck/icheck.min.js"></script>
<!-- AdminLTE App -->
<script src="${AdminLTE}/dist/js/adminlte.min.js"></script>
<script src="${Custom}/js/component-pagination.js"></script>
<script src="${Custom}/js/sys-manager-role.js"></script>
</body>
</html>