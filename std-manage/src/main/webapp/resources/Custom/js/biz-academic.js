$(function($) {
    var info = {},
    	dataCache = {},
    	consts = {
    		domain : 'academic',
    		ret_success : 'success',
    		ret_fail : 'fail'
    	},
        urls = {
    		academicPageQuery			  :   $._sysbus.host+'/manage/academic/pageQuery',
    		addOrUpdate					  :   $._sysbus.host+'/manage/academic/addOrUpdate',
    		makeMainPage                      :   $._sysbus.host+'/manage/academic/page-make'
        },
        initPageElement = function(){
            bindBtnClickEvent();
            /**
            $._pagination.init(consts.domain,{
            	request_url : urls.academicPageQuery,
            	request_params : {
            		page_no : 1
            	},
            	pagebar_container : $('.pagination'),
            	is_auto_request : true,
            	gather_request_params : function(domain){
            		var is_intercept = false;
            		//$._pagination.domains[domain].request_params['account'] = $('#account').val();
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
            		$('#academic-list tbody').empty().append(rowsHtml.length > 0 ? rowsHtml.join('') : "<tr class='text-center'><td colspan=7>暂无数据</td></tr>");
            		if(rowsHtml.length > 0){
            			$('#academic-list tbody .opt-edit').off('click').on('click',function(){
                        	$('#module-academic-query').removeClass('show').addClass('hide');
                        	$('#module-academic-add-edit').removeClass('hide').addClass('show');
            				initEditContainer(this);
            			});
            		}
            	}
            });
            */
        },
        bindBtnClickEvent = function(){
            $('#academicQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
            $('#conditionReset').off('click').on('click',function(){
            	//$('#account').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-academic-add-edit').removeClass('show').addClass('hide');
            	$('#module-academic-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addAcademic').off('click').on('click',function(){
            	$('#module-academic-query').removeClass('show').addClass('hide');
            	$('#module-academic-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#makeAcademic').off('click').on('click',function(){
                $._sysbus.request({
                    url : urls.makeMainPage,
                    callback : function(data){
                        if(data){
                            var retResult = data.result || consts.ret_fail;
                            var retMessage = data.message || '';
                            if(retResult==consts.ret_success){
                            	$._sysbus.showAlert('生成成功！',null);
                            }else{
                            	$._sysbus.showAlert('生成失败！',null);
                            }
                        }
                    }
                });
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

            return "";
        },
        initAddContainer = function(){
        	cleanAddEditContainer();
        	$('#module-academic-add-edit .box-title').html('添加网站导航');
        	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
        },
        initEditContainer = function(eventObject){
        	cleanAddEditContainer();
        	$('#module-academic-add-edit .box-title').html('修改网站导航');
        	$('#edit-id').parent().parent().removeClass('hide').addClass('show');
        	var $tr = $(eventObject).parent().parent('tr');
        	var record = dataCache[$tr.attr('data-id')];
        	//$('#edit-id').val(record['id']);
        	//$('#edit-account').val(record['account']);
        	//$('#edit-pwd').val('');
        	//$('#edit-mobile').val(record['mobile']);
        	//$('#edit-state').val(record['state']);
        },
        cleanAddEditContainer = function(){
        	$('#module-academic-add-edit .box-title').html('添加或修改网站导航');

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