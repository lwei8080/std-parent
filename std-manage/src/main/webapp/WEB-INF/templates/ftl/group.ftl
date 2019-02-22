<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
  <link rel="stylesheet" href="${AdminLTE}/bower_components/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
  <link rel="stylesheet" href="${Custom}/css/ztree-table.css" type="text/css">
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="group">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
               组织
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 组织管理</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>
    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-group-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">添加或修改组织</h3>
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
                  <label for="edit-name" class="col-sm-2 control-label">名称</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-name" placeholder="名称" maxlength="50">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-code" class="col-sm-2 control-label">编码</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-code" placeholder="编码" maxlength="50">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-parentName" class="col-sm-2 control-label">上级组织</label>
                  <div class="col-sm-10">
                    <div class="input-group">
                    	<span class="input-group-addon">ID:（<span id="edit-parentId">0</span>）</span>
                    	<input type="text" class="form-control" id="edit-parentName" placeholder="无" readonly="readonly">
                    </div>
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-orderNum" class="col-sm-2 control-label">序号</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-orderNum" placeholder="序号" maxlength="11">
                  </div>
                </div>
                <div class="form-group">
	              <label for="edit-state" class="col-sm-2 control-label">状态</label>
				  <div class="col-sm-10">
				  	<select class="form-control" id="edit-state">
				      <option value="1" selected="selected">有效</option>
				      <option value="0">无效</option>
				    </select>
				  </div>
                </div>
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
      
        <div class="col-md-12 show" id="module-group-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="name" class="col-sm-2 control-label">组织名称</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="name" placeholder="组织名称">
		              </div>
		              
		              <label for="state" class="col-sm-2 control-label">状态</label>
					  <div class="col-sm-4">
					  	<select class="form-control" id="state">
					      <option value="" selected="selected">-----全部-----</option>
					      <option value="1">有效</option>
					      <option value="0">无效</option>
					    </select>
					  </div>
		            </div>
					<div class="btn-group col-sm-offset-2">
						<button type="button" id="groupQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addGroup" class="btn btn-danger btn-flat pull-left margin">新 增</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <ul id="dataTree" class="ztree"></ul>
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
<div id="menuContent" class="menuContent" style="display:none; position: absolute;background-color:#FFFFFF;border:solid 1px #d2d6de">
	<ul id="menuTree" class="ztree" style="margin-top:0; width:180px;height:300px;"></ul>
</div>
<!-- Globle JS HTML -->
<#include "/ftl/assembly/body.common-bottom.ftl"> 
<!-- AdminLTE App -->
<script src="${AdminLTE}/dist/js/adminlte.min.js"></script>
<script src="${Custom}/js/component-pagination.js"></script>
<script type="text/javascript" src="${AdminLTE}/bower_components/zTree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="${AdminLTE}/bower_components/zTree/js/jquery.ztree.excheck.js"></script>
<script src="${Custom}/js/sys-group.js"></script>
</body>
</html>