<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
  
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="role">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
                角色
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 角色管理</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-role-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">添加或修改角色</h3>
            </div>
            <!-- /.box-header -->
            <!-- form start -->
            <form class="form-horizontal">
              <div class="box-body">
                <div class="form-group">
                  <label for="edit-id" class="col-sm-2 control-label">ID</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-id" placeholder="ID" readonly="readonly">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-name" class="col-sm-2 control-label">角色名</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-name" placeholder="角色名" maxlength="20">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-description" class="col-sm-2 control-label">描述</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-description" placeholder="描述" maxlength="20">
                  </div>
                </div>
              </div>
              <!-- /.box-body -->
              <div class="box-footer">
              	<div class="btn-group col-sm-offset-2">
              		<button type="button" id="editSave" class="btn btn-info pull-left btn-flat margin">保 存</button>
              		<button type="button" id="editReturn" class="btn btn-warning pull-left btn-flat margin">返 回</button>
              	</div>
              	<div class="col-sm-offset-2">
              		<p class="text-red">注：保存后不可修改，请谨慎操作！</p>
              	</div>
              </div>
              <!-- /.box-footer -->
            </form>
          </div>
      	</div>
      
        <div class="col-md-12 show" id="module-role-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="name" class="col-sm-2 control-label">角色名</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="name" placeholder="角色名">
		              </div>
		              
		              <label for="description" class="col-sm-2 control-label">描述</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="description" placeholder="描述">
		              </div>
		            </div>
					<div class="btn-group col-sm-offset-2">
						<button type="button" id="roleQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addRole" class="btn btn-danger btn-flat pull-left margin">新 增</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered" id="role-list">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>角色名</th>
                    <th>描述</th>
                    <th>创建时间</th>
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
<script src="${Custom}/js/sys-role.js"></script>
</body>
</html>