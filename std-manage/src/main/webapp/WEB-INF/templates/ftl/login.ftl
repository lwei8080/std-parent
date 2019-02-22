<!DOCTYPE html>
<html>
<head>
  <#include "/ftl/assembly/head.common.ftl">
  <!-- iCheck -->
  <link rel="stylesheet" href="${AdminLTE}/plugins/iCheck/square/blue.css">
</head>
<body class="hold-transition login-page">
<div class="login-box">
  <div class="login-logo">
    <a href="#"><b>Admin</b>LTE</a>
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body">
    <p class="login-box-msg">系统管理员登录</p>

    <form action="#" method="post">
      <div class="form-group has-feedback">
        <input type="text" class="form-control" id="loginName" placeholder="账号">
        <span class="glyphicon glyphicon-user form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
        <input type="password" id="loginPwd" class="form-control" placeholder="密码">
        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
      </div>
      <div class="form-group row">
      	<div class="col-xs-8">
      	  <input type="text" id="inputCaptcha" class="form-control" placeholder="验证码">
      	</div>
        <div class="col-xs-4">
          <img alt="captcha" id="captchaImage" class="pull-right" style="height:34px;">
        </div>
      </div>
      <div class="row">
        <div class="col-xs-8">
          <div class="checkbox icheck hide">
            <label>
              <input type="checkbox"> Remember Me
            </label>
          </div>
        </div>
        <!-- /.col -->
        <div class="col-xs-4">
          <button type="button" id="doLoginBtn" class="btn btn-primary btn-block btn-flat">登 录</button>
        </div>
        <!-- /.col -->
      </div>
    </form>

    <div class="social-auth-links text-center hide">
      <p>- OR -</p>
      <a href="#" class="btn btn-block btn-social btn-facebook btn-flat"><i class="fa fa-facebook"></i> Sign in using
        Facebook</a>
      <a href="#" class="btn btn-block btn-social btn-google btn-flat"><i class="fa fa-google-plus"></i> Sign in using
        Google+</a>
    </div>
    <!-- /.social-auth-links -->

    <a href="#" class="hide">I forgot my password</a><br>
    <a href="#" class="text-center hide">Register a new membership</a>

  </div>
  <!-- /.login-box-body -->
</div>
<!-- /.login-box -->

<!-- Globle JS -->
<#include "/ftl/assembly/body.common-bottom.ftl"> 
<!-- iCheck -->
<script src="${AdminLTE}/plugins/iCheck/icheck.min.js"></script>
<script>
  $(function () {
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' /* optional */
    });
  });
</script>
<script src="${Custom}/js/sys-login.js"></script>
</body>
</html>
