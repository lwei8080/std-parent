$(function($) {
    var info = {},
    	dataCache = {},
    	consts = {
    		domain : 'manager',
    		ret_success : 'success',
    		ret_fail : 'fail'
    	},
        urls = {
    		managerPageQuery			  :   $._sysbus.host+'/manage/manager/pageQuery',
    		addOrUpdate					  :   $._sysbus.host+'/manage/manager/addOrUpdate'
        },
        initPageElement = function(){
            bindBtnClickEvent();
            $._pagination.init(consts.domain,{
            	request_url : urls.managerPageQuery,
            	request_params : {
            		page_no : 1
            	},
            	pagebar_container : $('.pagination'),
            	is_auto_request : true,
            	gather_request_params : function(domain){
            		var is_intercept = false;
            		$._pagination.domains[domain].request_params['account'] = $('#account').val();
            		$._pagination.domains[domain].request_params['mobile'] = $('#mobile').val();
            		$._pagination.domains[domain].request_params['state'] = $('#state').val();
            		$._pagination.domains[domain].request_params['name'] = $('#name').val();
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
                            '<td>',record.account,'</td>',
                            '<td>',record.mobile,'</td>',
                            '<td>',record.createDate,'</td>',
                            '<td>',record.updateDate,'</td>',
                            '<td><span class="badge bg-',(record.state==1?'green':'default'),'">',(record.state==1?'有效':'无效'),'</span></td>',
                            '<td><a class="btn bg-purple btn-flat btn-xs opt-edit">修改</a></td>',
                          '</tr>'
            			];
            			rowsHtml.push(dataRowHtml.join(''));
            		});
            		$('#manager-list tbody').empty().append(rowsHtml.length > 0 ? rowsHtml.join('') : "<tr class='text-center'><td colspan=7>暂无数据</td></tr>");
            		if(rowsHtml.length > 0){
            			$('#manager-list tbody .opt-edit').off('click').on('click',function(){
                        	$('#module-manager-query').removeClass('show').addClass('hide');
                        	$('#module-manager-add-edit').removeClass('hide').addClass('show');
            				initEditContainer(this);
            			});
            		}
            	}
            });
        },
        bindBtnClickEvent = function(){
            $('#managerQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
            $('#conditionReset').off('click').on('click',function(){
            	$('#account').val('');
            	$('#mobile').val('');
            	$('#state').val('');
            	$('#name').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-manager-add-edit').removeClass('show').addClass('hide');
            	$('#module-manager-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addManager').off('click').on('click',function(){
            	$('#module-manager-query').removeClass('show').addClass('hide');
            	$('#module-manager-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#editSave').off('click').on('click',function(){
                if(collectManagerInfo()==consts.ret_success){
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
        collectManagerInfo = function(){
            var returnMsg = consts.ret_success;
        	info['id'] = $('#edit-id').val();
        	info['name'] = $('#edit-name').val();
        	info['account'] = $('#edit-account').val();
        	info['pwd'] = $('#edit-pwd').val();
        	info['mobile'] = $('#edit-mobile').val();
        	info['state'] = $('#edit-state').val();
            //check
        	if(info['name'].length>20){
                $._sysbus.showAlert('姓名字符长度必须小于等于20！',null);
                returnMsg = consts.ret_fail;
        	}else if($.trim(info['account'])==''){
                $._sysbus.showAlert('账号不能为空！',null);
                returnMsg = consts.ret_fail;
            }else if(info['account'].length<5||info['account'].length>20){
                $._sysbus.showAlert('账号字符长度必须大于等于5且小于等于20！',null);
                returnMsg = consts.ret_fail;
            }else if($._sysbus.isEmpty(info['id'])&&$.trim(info['pwd'])==''){//新增密码不能为空
                $._sysbus.showAlert('密码不能为空！',null);
                returnMsg = consts.ret_fail;
            }else if($.trim(info['pwd'])!=''&&(info['pwd'].length<6||info['pwd'].length>12)){
                $._sysbus.showAlert('密码字符长度必须大于等于6且小于等于12！',null);
                returnMsg = consts.ret_fail;
            }else if(!$._sysbus.regexp.mobile.test(info['mobile'])){
                $._sysbus.showAlert('手机号码输入错误！',null);
                returnMsg = consts.ret_fail;
            }
            return returnMsg;
        },
        initAddContainer = function(){
        	cleanAddEditContainer();
        	$('#module-manager-add-edit .box-title').html('添加管理员');
        	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
        },
        initEditContainer = function(eventObject){
        	cleanAddEditContainer();
        	$('#module-manager-add-edit .box-title').html('修改管理员');
        	$('#edit-id').parent().parent().removeClass('hide').addClass('show');
        	var $tr = $(eventObject).parent().parent('tr');
        	var record = dataCache[$tr.attr('data-id')];
        	$('#edit-id').val(record['id']);
        	$('#edit-name').val(record['name']);
        	$('#edit-account').val(record['account']);
        	$('#edit-pwd').val('');
        	$('#edit-mobile').val(record['mobile']);
        	$('#edit-state').val(record['state']);
        },
        cleanAddEditContainer = function(){
        	$('#module-manager-add-edit .box-title').html('添加或修改管理员');
        	$('#edit-id').val('');
        	$('#edit-name').val('');
        	$('#edit-account').val('');
        	$('#edit-pwd').val('');
        	$('#edit-mobile').val('');
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