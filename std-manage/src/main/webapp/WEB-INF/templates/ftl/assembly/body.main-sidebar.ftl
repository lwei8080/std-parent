  <!-- Left side column. contains the logo and sidebar -->
  <aside class="main-sidebar">

    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">

      <!-- Sidebar user panel (optional) -->
      <div class="user-panel">
        <div class="pull-left image">
          <img src="${AdminLTE}/dist/img/user2-160x160.jpg" class="img-circle" alt="User Image">
        </div>
        <div class="pull-left info">
          <p>${Session["realname"]}</p>
          <!-- Status -->
          <a href="#"><i class="fa fa-circle text-success"></i> ${.now?date}</a>
        </div>
      </div>

      <!-- search form (Optional) -->
      <!--
      <form action="#" method="get" class="sidebar-form">
        <div class="input-group">
          <input type="text" name="q" class="form-control" placeholder="Search...">
          <span class="input-group-btn">
              <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i>
              </button>
            </span>
        </div>
      </form>
      -->
      <!-- /.search form -->

      <!-- Sidebar Menu -->
      <ul class="sidebar-menu" data-widget="tree">
        <li class="header">工具栏-菜单</li>
        <!-- Optionally, you can add icons to the links -->
        <@shiro.hasPermission name="manage:index">
        <li page-id="index"><a href="${Session["basePath"]}/manage/index"><i class="fa fa-home"></i> <span>后台首页</span></a></li>
        </@shiro.hasPermission>
        
        <@shiro.hasPermission name="manage:academic:page">
        <li><a href="${Session["basePath"]}/manage/academic"><i class="fa fa-file-code-o"></i> <span>导航数据维护</span></a></li>
        </@shiro.hasPermission>
        
        <@shiro.hasAnyPermission name="manage:manager:page or manage:role:page or manage:permission:page or manage:manager-role:page or manage:role-permission:page or manage:group:page or manage:manager-group:page">
        <li class="treeview">
          <a href="#">
            <i class="fa fa-cogs"></i> <span>系统管理</span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <@shiro.hasPermission name="manage:manager:page">
            <li page-id="manager"><a href="${Session["basePath"]}/manage/manager"><i class="fa fa-user"></i> 账号管理</a></li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="manage:role:page">
            <li page-id="role"><a href="${Session["basePath"]}/manage/role"><i class="fa fa-cubes"></i> 角色管理</a></li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="manage:permission:page">
            <li page-id="permission"><a href="${Session["basePath"]}/manage/permission"><i class="fa fa-unlock-alt"></i> 权限管理</a></li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="manage:role-permission:page">
            <li page-id="role-permission"><a href="${Session["basePath"]}/manage/role-permission"><i class="fa fa-link"></i> 角色权限</a></li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="manage:manager-role:page">
            <li page-id="manager-role"><a href="${Session["basePath"]}/manage/manager-role"><i class="fa fa-user-md"></i> 账号角色</a></li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="manage:group:page">
            <li page-id="group"><a href="${Session["basePath"]}/manage/group"><i class="fa fa-institution"></i> 组织管理</a></li>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="manage:manager-group:page">
            <li page-id="manager-group"><a href="${Session["basePath"]}/manage/manager-group"><i class="fa fa-group"></i> 组织人员</a></li>
            </@shiro.hasPermission>
          </ul>
        </li>
        </@shiro.hasAnyPermission>
        
        <@shiro.hasRole name="admin">
        <li>
          <a href="${Session["basePath"]}/druid/index.html" target="_blank">
            <i class="fa fa-database"></i> <span>Druid监控</span>
            <span class="pull-right-container">
              <small class="label pull-right bg-green">超</small>
            </span>
          </a>
        </li>
        </@shiro.hasRole>
        
        
      </ul>
      <!-- /.sidebar-menu -->
    </section>
    <!-- /.sidebar -->
  </aside>