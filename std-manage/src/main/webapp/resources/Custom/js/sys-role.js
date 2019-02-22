$(function($) {
    var info = {},
		dataCache = {},
		consts = {
			domain : 'role',
			ret_success : 'success',
			ret_fail : 'fail'
		},
        urls = {
    		rolePageQuery			  :   $._sysbus.host+'/manage/role/pageQuery',
    		addOrUpdate					  :   $._sysbus.host+'/manage/role/addOrUpdate'
        },
        initPageElement = function(){
	        bindBtnClickEvent();
	        $._pagination.init(consts.domain,{
	        	request_url : urls.rolePageQuery,
	        	request_params : {
	        		page_no : 1
	        	},
	        	pagebar_container : $('.pagination'),
	        	is_auto_request : true,
	        	gather_request_params : function(domain){
	        		var is_intercept = false;
	        		$._pagination.domains[domain].request_params['name'] = $('#name').val();
	        		$._pagination.domains[domain].request_params['description'] = $('#description').val();
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
	                        '<td>',record.name,'</td>',
	                        '<td>',record.description,'</td>',
	                        '<td>',record.createDate,'</td>',
	                      '</tr>'
	        			];
	        			rowsHtml.push(dataRowHtml.join(''));
	        		});
	        		$('#role-list tbody').empty().append(rowsHtml.length > 0 ? rowsHtml.join('') : "<tr class='text-center'><td colspan=4>暂无数据</td></tr>");
	        	}
	        });
        },
        bindBtnClickEvent = function(){
            $('#roleQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
            $('#conditionReset').off('click').on('click',function(){
            	$('#name').val('');
            	$('#description').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-role-add-edit').removeClass('show').addClass('hide');
            	$('#module-role-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addRole').off('click').on('click',function(){
            	$('#module-role-query').removeClass('show').addClass('hide');
            	$('#module-role-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#editSave').off('click').on('click',function(){
                if(collectRoleInfo()==consts.ret_success){
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
        collectRoleInfo = function(){
            var returnMsg = consts.ret_success;
        	info['id'] = $('#edit-id').val();
        	info['name'] = $('#edit-name').val();
        	info['description'] = $('#edit-description').val();
            //check
            if($.trim(info['name'])==''){
                $._sysbus.showAlert('角色名不能为空！',null);
                returnMsg = consts.ret_fail;
            }else if(info['name'].length<5||info['name'].length>20){
                $._sysbus.showAlert('角色名字符长度必须大于等于5且小于等于20！',null);
                returnMsg = consts.ret_fail;
            }else if(info['description'].length>20){
                $._sysbus.showAlert('角色描述字符长度必须小于等于20！',null);
                returnMsg = consts.ret_fail;
            }
            return returnMsg;
        },
        initAddContainer = function(){
        	cleanAddEditContainer();
        	$('#module-role-add-edit .box-title').html('添加角色');
        	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
        },
        cleanAddEditContainer = function(){
        	$('#module-role-add-edit .box-title').html('添加或修改角色');
        	$('#edit-id').val('');
        	$('#edit-name').val('');
        	$('#edit-description').val('');
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