<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
  <link rel="stylesheet" href="${AdminLTE}/bower_components/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="role-permission">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
                角色权限
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 角色权限</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-role-permission-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">设置角色权限</h3>
            </div>
            <!-- /.box-header -->
            <!-- form start -->
            <form class="form-horizontal">
              <div class="box-body">
                <div class="form-group">
                  <div class="col-sm-12">
                    <div class="col-sm-12">
	                  <label for="edit-role">角色</label>
	                  <select class="form-control select2" style="width: 100%;" id="edit-role">
	                    <option value="" disabled="disabled" selected="selected">-----选择角色-----</option>
	                  </select>
                    </div>
                    <div class="col-sm-12">
                      <label>功能权限</label>
				      <blockquote>
				        <input type="text" class="form-control" id="tree-search-node" placeholder="按名称或url查询节点">
				        <ul id="tree" class="ztree"></ul>
				      </blockquote>
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
      
        <div class="col-md-12 show" id="module-role-permission-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="roleName" class="col-sm-1 control-label">角色名</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="roleName" placeholder="角色名">
		              </div>
		              
		              <label for="permissionName" class="col-sm-1 control-label">功能名</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="permissionName" placeholder="功能名">
		              </div>
		              
		              <label for="url" class="col-sm-1 control-label">功能URL</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="url" placeholder="功能URL">
		              </div>
		            </div>
		            <div class="form-group">
		              <label for="permissions" class="col-sm-1 control-label">权限</label>
		              <div class="col-sm-3">
		                <input type="text" class="form-control" id="permissions" placeholder="权限">
		              </div>
		              
		              <label for="state" class="col-sm-1 control-label">权限状态</label>
					  <div class="col-sm-3">
					  	<select class="form-control" id="state">
					      <option value="" selected="selected">-----全部-----</option>
					      <option value="1">有效</option>
					      <option value="0">无效</option>
					    </select>
					  </div>
		            </div>
					<div class="btn-group col-sm-offset-1">
						<button type="button" id="rolePermissionQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addRolePermission" class="btn btn-danger btn-flat pull-left margin">设 置</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered" id="role-permission-list">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>角色名</th>
                    <th>角色描述</th>
                    <th>功能名</th>
                    <th>功能URL</th>
                    <th>权限</th>
                    <th>权限状态</th>
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
<!-- AdminLTE App -->
<script src="${AdminLTE}/dist/js/adminlte.min.js"></script>
<script src="${Custom}/js/component-pagination.js"></script>
<script type="text/javascript" src="${AdminLTE}/bower_components/zTree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="${AdminLTE}/bower_components/zTree/js/jquery.ztree.excheck.js"></script>
<script src="${Custom}/js/sys-role-permission.js"></script>
</body>
</html>