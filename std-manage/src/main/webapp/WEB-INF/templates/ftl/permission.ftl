<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl"> 
  
</head>

<body class="hold-transition skin-blue sidebar-mini" page-id="permission">
<div class="wrapper">

  <#include "/ftl/assembly/body.main-header.ftl"> 
  <#include "/ftl/assembly/body.main-sidebar.ftl"> 

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
                权限
        <small>管理</small>
      </h1>
      <ol class="breadcrumb">
        <li class=""><a href="#"><i class="fa fa-circle-o"></i> 权限管理</a></li>
        <li class="active">欢迎！</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
      	<div class="col-md-12 hide" id="module-permission-add-edit">
          <!-- Horizontal Form -->
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">添加或修改权限</h3>
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
                  <label for="edit-name" class="col-sm-2 control-label">权限功能名</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-name" placeholder="权限功能名" maxlength="12">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-parent-id" class="col-sm-2 control-label">上级</label>
                  <div class="col-sm-10">
				  	<select class="form-control" id="edit-parent-id">
				      <option value="0" selected="selected">-----ROOT-----</option>
				    </select>
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-tag" class="col-sm-2 control-label">PAGE-ID</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-tag" placeholder="PAGE-ID" maxlength="20">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-url" class="col-sm-2 control-label">URL</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-url" placeholder="URL" maxlength="500">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-permissions" class="col-sm-2 control-label">权限</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-permissions" placeholder="权限" maxlength="250">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-order-num" class="col-sm-2 control-label">排序号</label>
                  <div class="col-sm-10">
                    <input type="text" class="form-control" id="edit-order-num" placeholder="排序号" maxlength="11">
                  </div>
                </div>
                <div class="form-group">
                  <label for="edit-type" class="col-sm-2 control-label">类型</label>
                  <div class="col-sm-10">
				  	<select class="form-control" id="edit-type">
				      <option value="" selected="selected">-----请选择-----</option>
				      <option value="0">目录</option>
				      <option value="1">菜单</option>
				      <option value="2">按钮</option>
				    </select>
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
      
        <div class="col-md-12 show" id="module-permission-query">
          <div class="box box-info">
            <div class="box-header with-border">
	            <!-- form start -->
	            <form class="form-horizontal">
		            <div class="form-group">
		              <label for="url" class="col-sm-2 control-label">URL</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="url" placeholder="URL">
		              </div>
		              
		              <label for="permissions" class="col-sm-2 control-label">授权</label>
		              <div class="col-sm-4">
		                <input type="text" class="form-control" id="permissions" placeholder="授权">
		              </div>
		            </div>
		            <div class="form-group">
		              <label for="type" class="col-sm-2 control-label">类型</label>
					  <div class="col-sm-4">
					  	<select class="form-control" id="type">
					      <option value="" selected="selected">-----全部-----</option>
					      <option value="0">目录</option>
					      <option value="1">菜单</option>
					      <option value="2">按钮</option>
					    </select>
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
						<button type="button" id="permissionQuery" class="btn btn-info btn-flat pull-left margin">查 询</button>
						<button type="button" id="conditionReset" class="btn btn-default btn-flat pull-left margin" title="仅重设默认查询条件">重 置</button>
						<button type="button" id="addPermission" class="btn btn-danger btn-flat pull-left margin">新 增</button>
					</div>
	            </form>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered" id="permission-list">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>功能名</th>
                    <th>上级功能名</th>
                    <th>功能标识</th>
                    <th>URL</th>
                    <th>要求权限</th>
                    <th>排序号</th>
                    <th>类型</th>
                    <th>创建时间</th>
                    <th>修改时间</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody v-if="items.length">
                  <tr v-for="(item,index) in items" :data-id="item.id">
                  	<td>{{item.no}}</td>
                  	<td>{{item.name}}</td>
                  	<td>{{item.parentName}}</td>
                  	<td>{{item.tag}}</td>
                  	<td>{{item.url}}</td>
                  	<td>{{item.permissions}}</td>
                  	<td>{{item.orderNum}}</td>
                  	<td>{{item.type}}</td>
                  	<td>{{item.createDate}}</td>
                  	<td>{{item.updateDate}}</td>
                  	<td>
                  		<span class="badge " :class="item.state==valid ? 'bg-green' : 'bg-default'">{{ item.state==valid ? '有效' : '无效' }}</span>
                  	</td>
                  	<td><a class="btn bg-purple btn-flat btn-xs opt-edit" @click="toEdit">修改</a></td>
                  </tr>
                </tbody>
                <tbody v-else>
                  <tr class='text-center' >
                  	<td colspan=13>暂无数据</td>
                  </tr>
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
<script src="${AdminLTE}/bower_components/vue/dist/vue.js"></script>
<script src="${Custom}/js/component-pagination.js"></script>
<script src="${Custom}/js/sys-permission.js"></script>
</body>
</html>