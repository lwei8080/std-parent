$(function($) {
    var info = {},
		dataCache = {},
		consts = {
			domain : 'permission',
			ret_success : 'success',
			ret_fail : 'fail'
		},
	    urls = {
    		permissionPageQuery			  :   $._sysbus.host+'/manage/permission/pageQuery',
			addOrUpdate					  :   $._sysbus.host+'/manage/permission/addOrUpdate',
			permissionParents			  :	  $._sysbus.host+'/manage/permission/parents'
	    },
	    vue_permission_list = new Vue({
	    	el : '#permission-list',
	    	data : {
	    		items : [],
	    		valid : 1
	    	},
	    	methods : {
	    		toEdit : function (event){
                	$('#module-permission-query').removeClass('show').addClass('hide');
                	$('#module-permission-add-edit').removeClass('hide').addClass('show');
    				initEditContainer(event.target);
	    		}
	    	}
	    }),
        initPageElement = function(){
            bindBtnClickEvent();
	        $._pagination.init(consts.domain,{
	        	request_url : urls.permissionPageQuery,
	        	request_params : {
	        		page_no : 1
	        	},
	        	pagebar_container : $('.pagination'),
	        	is_auto_request : true,
	        	gather_request_params : function(domain){
	        		var is_intercept = false;
	        		$._pagination.domains[domain].request_params['url'] = $('#url').val();
	        		$._pagination.domains[domain].request_params['permissions'] = $('#permissions').val();
	        		$._pagination.domains[domain].request_params['type'] = $('#type').val();
	        		$._pagination.domains[domain].request_params['state'] = $('#state').val();
	        		return is_intercept;
	        	},
	        	render_data : function(domain){
	        		cleanDataCache();
	        		var records = $._pagination.domains[domain].records || [];
	        		var rows = [];
	        		$.each(records, function(i, record){
	        			dataCache[record.id] = record;
	        			var row = {
	        				id : record.id,
	        				no : ($._pagination.domains[domain].request_params.page_no-1)*$._pagination.domains[domain].page_size+i+1,
	        				parentId : record.parentId,
	        				parentName : record.parentName,
	        				name : record.name,
	        				tag : record.tag,
	        				url : record.url,
	        				permissions : record.permissions,
	        				orderNum : record.orderNum,
	        				type : record.type=='0'?'目录':(record.type=='1'?'菜单':'按钮'),
	        				createDate : record.createDate,
	        				updateDate : record.updateDate,
	        				state : record.state
	        			};
	        			rows.push(row);
	        		});
	        		vue_permission_list.items = rows;
	        	}
	        });
        },
        bindBtnClickEvent = function(){
            $('#permissionQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
            $('#conditionReset').off('click').on('click',function(){
            	$('#url').val('');
            	$('#permissions').val('');
            	$('#type').val('');
            	$('#state').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-permission-add-edit').removeClass('show').addClass('hide');
            	$('#module-permission-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addPermission').off('click').on('click',function(){
            	$('#module-permission-query').removeClass('show').addClass('hide');
            	$('#module-permission-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#editSave').off('click').on('click',function(){
                if(collectPermissionInfo()==consts.ret_success){
                    $._sysbus.request({
                        url : urls.addOrUpdate,
                        type : 'post',
                        ajaxData : $.param(info,true),
                        callback : function(data){
                            if(data){
                                var retResult = data.result || consts.ret_fail;
                                var retMessage = data.message || '';
                                if(retResult==consts.ret_success){
                                	$._sysbus.showAlert($._sysbus.isEmpty(retMessage)?'保存成功！':retMessage,null);
                                }else{
                                    $._sysbus.showAlert($._sysbus.isEmpty(retMessage)?'保存失败！':retMessage,null);
                                }
                            }
                        }
                    });
                }
            });
        },
        collectPermissionInfo = function(){
            var returnMsg = consts.ret_success;
        	info['id'] = $('#edit-id').val();
        	info['name'] = $('#edit-name').val();
        	info['parentId'] = $('#edit-parent-id').val();
        	info['tag'] = $('#edit-tag').val();
        	info['url'] = $('#edit-url').val();
        	info['permissions'] = $('#edit-permissions').val();
        	info['orderNum'] = $('#edit-order-num').val();
        	info['type'] = $('#edit-type').val();
        	info['state'] = $('#edit-state').val();
            //check
            if($.trim(info['name'])==''){
                $._sysbus.showAlert('权限功能名不能为空！',null);
                returnMsg = consts.ret_fail;
            }else if(info['name'].length<1||info['name'].length>12){
                $._sysbus.showAlert('权限功能名字符长度必须大于等于1且小于等于12！',null);
                returnMsg = consts.ret_fail;
            }else if(info['tag'].length>20){
                $._sysbus.showAlert('PAGE-ID字符长度不能大于20！',null);
                returnMsg = consts.ret_fail;
            }else if(info['url'].length>500){
                $._sysbus.showAlert('URL字符长度不能大于500！',null);
                returnMsg = consts.ret_fail;
            }else if(info['permissions'].length>250){
                $._sysbus.showAlert('权限字符长度不能大于250！',null);
                returnMsg = consts.ret_fail;
            }else if(info['orderNum']!=''&&!$._sysbus.regexp.digital.test(info['orderNum'])){
                $._sysbus.showAlert('排序号只能为数字！',null);
                returnMsg = consts.ret_fail;
            }else if($.trim(info['type'])==''){
                $._sysbus.showAlert('请选择类型！',null);
                returnMsg = consts.ret_fail;
            }
            return returnMsg;
        },
        initAddContainer = function(){
        	$._sysbus.showMask(false);
        	initAddEditContainer(function(){
            	cleanAddEditContainer();
            	$('#module-permission-add-edit .box-title').html('添加权限');
            	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
        	});
        },
        initEditContainer = function(eventObject){
        	$._sysbus.showMask(false);
        	initAddEditContainer(function(){
            	cleanAddEditContainer();
            	$('#module-permission-add-edit .box-title').html('修改权限');
            	$('#edit-id').parent().parent().removeClass('hide').addClass('show');
            	var $tr = $(eventObject).parent().parent('tr');
            	var record = dataCache[$tr.attr('data-id')];
            	$('#edit-id').val(record['id']);
            	$('#edit-name').val(record['name']);
            	$('#edit-parent-id').val(record['parentId']);
            	$('#edit-tag').val(record['tag']);
            	$('#edit-url').val(record['url']);
            	$('#edit-permissions').val(record['permissions']);
            	$('#edit-order-num').val(record['orderNum']);
            	$('#edit-type').val(record['type']);
            	$('#edit-state').val(record['state']);
        	});
        },
        initAddEditContainer = function(finalFunc){
            $._sysbus.request({
                url : urls.permissionParents,
                async : true,
                callback : function(data){
                    if(data){
                        var retData = data.data;
                        var optionsHtml = ['<option value="0" selected="selected">-----ROOT-----</option>'];
                        $.each(retData, function(i,d){
                        	optionsHtml.push('<option value="'+d.id+'">'+d.name||''+'</option>');
                        });
                        $('#edit-parent-id').empty().append(optionsHtml.join(''));
                    }
                    finalFunc();
                    $._sysbus.hideMask();
                }
            });
        },
        cleanAddEditContainer = function(){
        	$('#module-permission-add-edit .box-title').html('添加或修改权限');
        	$('#edit-id').val('');
        	$('#edit-name').val('');
        	$('#edit-parent-id').val('0');
        	$('#edit-tag').val('');
        	$('#edit-url').val('');
        	$('#edit-permissions').val('');
        	$('#edit-order-num').val('');
        	$('#edit-type').val('');
        	$('#edit-state').val('1');
        },
        cleanDataCache = function(){
        	// 数据缓存 每当超过100个key则清理
        	if(Object.keys(dataCache).length > 100){
        		dataCache = {};
        	}
        }
    ;
    initPageElement();
});