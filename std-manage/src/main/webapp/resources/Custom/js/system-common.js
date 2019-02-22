$(function($) {
	$._sysbus = $._sysbus || {};
	$.BasePath = '';
	$.extend($._sysbus, {
    	consts: {
    			session_status_401 : '401',
    			session_status_403 : '403',
    			session_status_500 : '500'
    	},
		host: document.location.protocol + '//' + document.location.host + $.BasePath,
	    urls: {
	    		logout               :   '/manage/logout',
	    		error				 :	 '/error'
	    },
	    regexp: {
	    	mobile : /^(0|86|(\\+86)|17951)?1[3,4,5,7,8][0-9]\d{8}$/,
	    	digital : /^\d{1,}$/
	    },
        request: function( opts ){
    		var type = opts.type || 'get',
    			url = opts.url,
    			async = typeof(opts.async) == 'undefined' ? true : opts.async,
    			callback = opts.callback,
    			ajaxData = opts.ajaxData || {},
    			errorcall = opts.errorcall;
    		$.ajax({
    			type: type,
    			url:  url,
    			async: async, 
    			dataType: 'json',
    			data: ajaxData,
    			cache: false,
    			success: function( data, textStatus, jqXHR ) {
    				var xSessionStatus = jqXHR.getResponseHeader('X-Session-Status');
    			    if(!$._sysbus.isEmpty(xSessionStatus)&&xSessionStatus==$._sysbus.consts.session_status_403){
    			    	$._sysbus.hideMask();
    			        $._sysbus.showAlert('身份信息失效，请重新登录！',function(){
    			        	$._sysbus.logout();
    			        });
    			        return false;
    			    }
    			    if(!$._sysbus.isEmpty(xSessionStatus)&&xSessionStatus==$._sysbus.consts.session_status_500){
    			    	$._sysbus.error();
    			        return false;
    			    }
    			    if(!$._sysbus.isEmpty(xSessionStatus)&&xSessionStatus==$._sysbus.consts.session_status_401){
    			    	$._sysbus.hideMask();
    			        $._sysbus.showAlert('请求资源未经授权！', null);
    			        return false;
    			    }
    				var bFlag = $.isEmptyObject( data ) ? false : true,
    					dataObj;
    				if( bFlag ){
    					dataObj = data || {};
    				}else{
    					dataObj = false;
    				}
    				if ($.isFunction(callback)) {
						callback.call(this,dataObj);
					}
    			},
    			error: function() {
    			    if ($.isFunction(errorcall)) {
    			        errorcall();
    			    }
    			}
    		});
    	},
    	isEmpty : function(str){
    	    if(typeof(str) == 'undefined' || str===null || str==='' || $.trim(str) === '' || (str==''&&str!=0) || ($.trim(str) == '' && $.trim(str)!=0)){
    	        return true;
    	    }
    	    return false;
    	},
    	error : function(){
    		window.location = $._sysbus.host+$._sysbus.urls.error;
    	},
    	logout : function(){
    		window.location = $._sysbus.host+$._sysbus.urls.logout;
    	},
    	showAlert : function(message,func){
        	$('#modal-alert .modal-body').empty().append(['<p class="text-center">',message,'</p>'].join(''));
        	if($.isFunction(func))
        		$('#modal-alert').off().on('hide.bs.modal',func);//hide.bs.modal当调用 hide 实例方法时触发;hidden.bs.modal当模态框完全对用户隐藏时触发
            $('#modal-alert').modal('show');
    	},
    	showConfirm : function(message,cancelFunc,confirmFunc){
    		$('#modal-confirm .modal-body').empty().append(['<p class="text-center">',message,'</p>'].join(''));
        	if($.isFunction(cancelFunc))
        		$('#modal-confirm').off().on('hide.bs.modal',cancelFunc);
        	if($.isFunction(confirmFunc))
        		$('#modal-confirm #btn-confirm').off('click').on('click',function(){
        			confirmFunc();
        			$('#modal-confirm').off().modal('hide');
        		});
        	$('#modal-confirm').modal('show');
    	},
    	showMask : function(isBackdrop){
    		$('#modal-mask').modal({
    			backdrop : isBackdrop,
    			show : true
    		});
    		$('#modal-mask').show();
    	},
    	hideMask : function(){
    		$('#modal-mask').off().modal('hide');
    		$('#modal-mask').hide();
    	}
	});
	(function commonInit(){
		$(".sidebar-menu li").removeClass("active");
		var pageId = $("body").attr("page-id");
		if(!$._sysbus.isEmpty(pageId)) {
			var cli = $(["li[page-id='",pageId,"']"].join(''));
			cli.parents(".treeview").addClass("active");
			cli.addClass("active");
		}
	})();
});