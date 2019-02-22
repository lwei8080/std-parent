<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
  <link rel="stylesheet" href="${AdminLTE}/bower_components/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="manager-group">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
                组织人员
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 组织人员</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-manager-group-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">设置组织人员</h3>
            </div>
            <!-- /.box-header -->
            <!-- form start -->
            <form class="form-horizontal">
              <div class="box-body">
                <div class="row">
	              <div class="col-sm-12">
	                <div class="form-group">
	                  <label for="edit-id" class="col-sm-1 control-label">ID</label>
	                  <div class="col-sm-11">
		                  <input type="text" class="form-control" id="edit-id" placeholder="ID" readonly="readonly">
	                  </div>
	                </div>
	              </div>
	              <div class="col-sm-12">
	                <div class="form-group">
	                  <label for="edit-manager" class="col-sm-1 control-label">账号</label>
	                  <div class="col-sm-11">
		                  <select class="form-control select2" style="width: 100%;" id="edit-manager">
		                    <option value="" disabled="disabled" selected="selected">-----选择账号-----</option>
		                  </select>
	                  </div>
	                </div>
	              </div>
	              <div class="col-sm-12">
	                <div class="form-group">
	                  <label for="edit-groupName" class="col-sm-1 control-label">组织</label>
	                  <div class="col-sm-11">
		                  <div class="input-group">
		                	<span class="input-group-addon">ID:（<span id="edit-groupId">0</span>）</span>
		                	<input type="text" class="form-control" id="edit-groupName" placeholder="" readonly="readonly">
		                  </div>
	                  </div>
	                </div>
	              </div>
	              <div class="col-sm-12">
	                <div class="form-group">
	                  <label for="edit-show" class="col-sm-1 text-right">是否显示</label>
	                  <div class="col-sm-11">
		                  <div class="input-group">
				              <!-- radio -->
				              <label><input type="radio" name="edit-show" value="1" class="minimal-green" checked> 是</label>&nbsp;&nbsp;
				              <label><input type="radio" name="edit-show" value="0" class="minimal-green"> 否</label>
			              </div>
		              </div>
	                </div>
	              </div>
	              <div class="col-sm-12">
	                <div class="form-group">
	                  <label for="edit-master" class="col-sm-1 text-right">是否负责人</label>
	                  <div class="col-sm-11">
		                  <div class="input-group">
				              <!-- radio -->
			                  <label><input type="radio" name="edit-master" value="1" class="minimal-green"> 是</label>&nbsp;&nbsp;
			                  <label><input type="radio" name="edit-master" value="0" class="minimal-green" checked> 否</label>
		                  </div>
	                  </div>
	                </div>
	              </div>
				  <div class="col-sm-12">
				    <div class="form-group">
				  	  <label for="edit-state" class="col-sm-1 control-label">状态</label>
				  	  <div class="col-sm-11">
					  	  <select class="form-control" id="edit-state">
					        <option value="1" selected="selected">有效</option>
					        <option value="0">无效</option>
					      </select>
				      </div>
				    </div>
				  </div>
                </div>
              </div>
              <!-- /.box-body -->
              <div class="box-footer">
              	<div class="btn-group  col-sm-offset-1">
              		<button type="button" id="editSave" class="btn btn-info btn-flat margin">保 存</button>
              		<button type="button" id="editReturn" class="btn btn-warning btn-flat margin">返 回</button>
              	</div>
              </div>
              <!-- /.box-footer -->
            </form>
          </div>
      	</div>
      	
      	<div class="col-md-12 hide" id="module-manager-group-tree">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">树形视图</h3>
            </div>
            <!-- /.box-header -->
            <!-- form start -->
            <form class="form-horizontal">
              <div class="box-body">
                <div class="row">
	              <div class="col-sm-12">
	                <div class="form-group">
	                  <label for="tree-left" class="col-sm-1 control-label">&nbsp;</label>
	                  <div class="col-sm-11">
		                  <ul id="viewTree" class="ztree"></ul>
	                  </div>
	                </div>
	              </div>
                </div>
              </div>
              <!-- /.box-body -->
              <div class="box-footer">
              	<div class="btn-group  col-sm-offset-1">
              		<button type="button" id="treeViewReturn" class="btn btn-warning btn-flat margin">返 回</button>
              	</div>
              </div>
              <!-- /.box-footer -->
            </form>
          </div>
      	</div>
      
        <div class="col-md-12 show" id="module-manager-group-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="account" class="col-sm-2 control-label">账号</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="account" placeholder="账号">
		              </div>
		              <label for="name" class="col-sm-2 control-label">姓名</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="name" placeholder="姓名">
		              </div>
					</div>
					<div class="form-group">
		              <label for="groupName" class="col-sm-2 control-label">组织名称</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="groupName" placeholder="组织名称">
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
						<button type="button" id="managerGroupQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addManagerGroup" class="btn btn-danger btn-flat pull-left margin">新 增</button>
						<button type="button" id="treeStructView" class="btn btn-primary btn-flat pull-left margin">树形视图</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered" id="manager-group-list">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>账号</th>
                    <th>姓名</th>
                    <th>组织名</th>
                    <th>是否展示</th>
                    <th>是否负责人</th>
                    <th>状态</th>
                    <th>创建时间</th>
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
<div id="menuContent" class="menuContent" style="display:none; position: absolute;background-color:#FFFFFF;border:solid 1px #d2d6de">
	<ul id="menuTree" class="ztree" style="margin-top:0; width:180px;height:300px;"></ul>
</div>
<!-- Globle JS HTML -->
<#include "/ftl/assembly/body.common-bottom.ftl"> 
<!-- Select2 -->
<script src="${AdminLTE}/bower_components/select2/dist/js/select2.full.min.js"></script>
<!-- iCheck 1.0.1 -->
<script src="${AdminLTE}/plugins/iCheck/icheck.min.js"></script>
<!-- AdminLTE App -->
<script src="${AdminLTE}/dist/js/adminlte.min.js"></script>
<script src="${Custom}/js/component-pagination.js"></script>
<script type="text/javascript" src="${AdminLTE}/bower_components/zTree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="${AdminLTE}/bower_components/zTree/js/jquery.ztree.excheck.js"></script>
<script src="${Custom}/js/sys-manager-group.js"></script>
</body>
</html>