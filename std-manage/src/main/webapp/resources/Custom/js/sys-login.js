$(function($) {
    var info = {},
    	consts = {
    		ret_success : 'success',
    		ret_fail : 'fail'
    	},
        urls = {
    		doLogin               :   $._sysbus.host+'/manage/doLogin',
    		viewIndex			  :   $._sysbus.host+'/manage/index',
    		captcha               :   $._sysbus.host+'/captcha'
        },
        initPageElement = function(){
    		$('#captchaImage').attr('src', urls.captcha);
            bindBtnClickEvent();
        },
        bindBtnClickEvent = function(){
            $('#doLoginBtn').off('click').on('click',function(){
                login();
            });
            $('.login-page').keydown(function(e){
                if(e.keyCode==13){
                    login();
                }
            });
            $('#captchaImage').off('click').on('click',function(){
            	$(this).attr('src', urls.captcha);
            });
        },
        login = function(){
            if(collectLoginInfo()==consts.ret_success){
                $._sysbus.request({
                    url : urls.doLogin,
                    type : 'post',
                    ajaxData : $.param(info,true),
                    callback : function(data){
                        if(data){
                            var retResult = data.result || consts.ret_fail;
                            if(retResult==consts.ret_success){
                                window.location = urls.viewIndex;
                            }else{
                            	//刷新验证码
                            	$('#captchaImage').attr('src', urls.captcha);
                                var retMessage = data.message || '';
                                $._sysbus.showAlert($._sysbus.isEmpty(retMessage)?'用户名或密码错误！':retMessage,null);
                            }
                        }
                    }
                });
            }
        },
        collectLoginInfo = function(){
            var returnMsg = consts.ret_success;
            info['username'] = $('#loginName').val();
            info['password'] = $('#loginPwd').val();
            info['inputCaptcha'] = $('#inputCaptcha').val();
            //check
            if($.trim(info['username'])==''){
                $._sysbus.showAlert('用户名不能为空！',null);
                returnMsg = consts.ret_fail;
            }else if($.trim(info['password'])==''){
                $._sysbus.showAlert('密码不能为空！',null);
                returnMsg = consts.ret_fail;
            }else if($.trim(info['inputCaptcha'])==''){
                $._sysbus.showAlert('验证码不能为空！',null);
                returnMsg = consts.ret_fail;
            }
            return returnMsg;
        }
    ;
    initPageElement();
});