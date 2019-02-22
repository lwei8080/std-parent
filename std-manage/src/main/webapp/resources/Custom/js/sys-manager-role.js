$(function($) {
    var info = {},
    	dataCache = {},
    	consts = {
    		domain : 'manager-role',
    		ret_success : 'success',
    		ret_fail : 'fail'
    	},
        urls = {
    		managerRolePageQuery			  :   $._sysbus.host+'/manage/manager-role/pageQuery',
    		addOrUpdate					  	  :   $._sysbus.host+'/manage/manager-role/addOrUpdate',
    		allManagers                       :   $._sysbus.host+'/manage/manager/all',
    		allRoles                          :   $._sysbus.host+'/manage/role/all',
    		allRolesByAccount                 :   $._sysbus.host+'/manage/role/byAccount'
        },
        initPageElement = function(){
	        //Initialize Select2 Elements
	        $('.select2').select2();
	        //Red color scheme for iCheck
	        $('input[type="checkbox"].minimal-red, input[type="radio"].minimal-red').iCheck({
	          checkboxClass: 'icheckbox_minimal-red',
	          radioClass   : 'iradio_minimal-red'
	        });
            bindBtnClickEvent();
            $._pagination.init(consts.domain,{
            	request_url : urls.managerRolePageQuery,
            	request_params : {
            		page_no : 1
            	},
            	pagebar_container : $('.pagination'),
            	is_auto_request : true,
            	gather_request_params : function(domain){
            		var is_intercept = false;
            		$._pagination.domains[domain].request_params['account'] = $('#account').val();
            		$._pagination.domains[domain].request_params['roleName'] = $('#roleName').val();
            		$._pagination.domains[domain].request_params['managerState'] = $('#managerState').val();
            		return is_intercept;
            	},
            	render_data : function(domain){
            		cleanDataCache();
            		var records = $._pagination.domains[domain].records || [];
            		var rowsHtml = [];
            		$.each(records, function(i, record){
            			dataCache[record.id] = record;
            			var dataRowHtml = [
                          '<tr data-id="',record.id,'">',
                            '<td>',($._pagination.domains[domain].request_params.page_no-1)*$._pagination.domains[domain].page_size+i+1,'</td>',
                            '<td>',record.manager.account,'</td>',
                            '<td><span class="badge bg-',(record.manager.state==1?'green':'default'),'">',(record.manager.state==1?'有效':'无效'),'</span></td>',
                            '<td>',record.role.name,'</td>',
                            '<td><a class="btn bg-purple btn-flat btn-xs opt-edit">设置</a></td>',
                          '</tr>'
            			];
            			rowsHtml.push(dataRowHtml.join(''));
            		});
            		$('#manager-role-list tbody').empty().append(rowsHtml.length > 0 ? rowsHtml.join('') : "<tr class='text-center'><td colspan=5>暂无数据</td></tr>");
            		if(rowsHtml.length > 0){
            			$('#manager-role-list tbody .opt-edit').off('click').on('click',function(){
                        	$('#module-manager-role-query').removeClass('show').addClass('hide');
                        	$('#module-manager-role-add-edit').removeClass('hide').addClass('show');
            				initEditContainer(this);
            			});
            		}
            	}
            });
        },
        bindBtnClickEvent = function(){
            $('#managerRoleQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
            $('#conditionReset').off('click').on('click',function(){
            	$('#account').val('');
            	$('#roleName').val('');
            	$('#managerState').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-manager-role-add-edit').removeClass('show').addClass('hide');
            	$('#module-manager-role-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addManagerRole').off('click').on('click',function(){
            	$('#module-manager-role-query').removeClass('show').addClass('hide');
            	$('#module-manager-role-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#edit-manager').on('change',function(){
            	$(this).find("option:selected").text();
            	var account = $(this).find("option:selected").text();
            	if(account===""){
            		$._sysbus.showAlert("请选择账号！",null);
            	}else{
            		requestAllRolesByManager(account);
            	}
            });
            $('#editSave').off('click').on('click',function(){
                if(collectManagerRoleInfo()==consts.ret_success){
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
        requestAllRolesByManager = function(account){
        	$('#edit-role-array :checkbox').iCheck('uncheck');
            $._sysbus.request({
                url : urls.allRolesByAccount,
                ajaxData : $.param({"account":account},true),
                callback : function(data){
                    if(data){
                        var retData = data.data;
                        $.each(retData, function(i,d){
                        	$('#edit-role-array input:checkbox[value="'+d.id+'"]').iCheck('check');
                        });
                    }
                }
            });
        },
        collectManagerRoleInfo = function(){
            var returnMsg = consts.ret_success;
            info = {};
            info['managerId'] = $('#edit-manager').val();
            if($._sysbus.isEmpty(info['managerId'])){
            	$._sysbus.showAlert("请选择账号！",null);
            	returnMsg = consts.ret_fail;
            }
            var checkedRoles = $('#edit-role-array input:checkbox:checked');
            $.each(checkedRoles, function(i,cb){
            	var roleId = $(cb).val();
            	if(roleId!=0){
            		if($._sysbus.isEmpty(info['roleIds']))
            			info['roleIds'] = ""+roleId;
            		else
            			info['roleIds'] = info['roleIds']+"|"+roleId;
            	}
            });
            return returnMsg;
        },
        initAddContainer = function(){
        	$._sysbus.showMask(false);
        	initAddEditContainer(function(){
            	cleanAddEditContainer();
            	$('#module-manager-role-add-edit .box-title').html('设置账号角色');
        	});
        },
        initEditContainer = function(eventObject){
        	$._sysbus.showMask(false);
        	initAddEditContainer(function(){
            	cleanAddEditContainer();
            	$('#module-manager-role-add-edit .box-title').html('设置账号角色');
            	var $tr = $(eventObject).parent().parent('tr');
            	var record = dataCache[$tr.attr('data-id')];
            	$('#edit-manager').val(record.manager.id);
            	requestAllRolesByManager(record.manager.account);
        	});
        },
        initAddEditContainer = function(finalFunc){
            $._sysbus.request({
                url : urls.allManagers,
                async : true,
                callback : function(data){
                    if(data){
                        var retData = data.data;
                        var optionsHtml = ['<option value="" disabled="disabled" selected="selected">-----选择账号-----</option>'];
                        $.each(retData, function(i,d){
                        	optionsHtml.push('<option value="'+d.id+'">'+d.account||''+'</option>');
                        });
                        $('#edit-manager').empty().append(optionsHtml.join(''));
                    }
                    $._sysbus.request({
                        url : urls.allRoles,
                        async : true,
                        callback : function(data){
                            if(data){
                                var retData = data.data;
                                buildRoleArray(retData);
                            }
                            finalFunc();
                            $._sysbus.hideMask();
                        }
                    });
                }
            });
        },
        cleanAddEditContainer = function(){
        	$('#module-manager-role-add-edit .box-title').html('设置账号角色');
        },
        cleanDataCache = function(){
        	// 数据缓存 每当超过100个key则清理
        	if(Object.keys(dataCache).length > 100){
        		dataCache = {};
        	}
        },
		buildRoleArray = function(roles){
			var roleCheckboxHtml = [];
            $.each(roles, function(i,d){
            	roleCheckboxHtml.push(['<div class="col-sm-2"><label><input type="checkbox" class="minimal-blue" value="',d.id,'" /><em style="margin-left: 0.75rem !important">',d.name,'</em></label></div>'].join(''));
            });
            $('#edit-role-array').empty().append(roleCheckboxHtml.join(''));
	        //Red color scheme for iCheck
	        $('input[type="checkbox"].minimal-blue, input[type="radio"].minimal-blue').iCheck({
	          checkboxClass: 'icheckbox_minimal-blue',
	          radioClass   : 'iradio_minimal-blue'
	        });
		}
    ;
    initPageElement();
});