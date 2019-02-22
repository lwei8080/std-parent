$(function($) {
    var info = {},
    	dataCache = {},
    	consts = {
    		domain : 'manager-group',
    		ret_success : 'success',
    		ret_fail : 'fail'
    	},
        urls = {
    		managerGroupPageQuery			   :   $._sysbus.host+'/manage/manager-group/pageQuery',
    		addOrUpdate					  	   :   $._sysbus.host+'/manage/manager-group/addOrUpdate',
    		validQuery                         :   $._sysbus.host+'/manage/manager-group/validQuery',
    		groupAll                           :   $._sysbus.host+'/manage/group/all',
    		allManagers                        :   $._sysbus.host+'/manage/manager/all'
        },
        initPageElement = function(){
	        //Initialize Select2 Elements
	        $('.select2').select2();
	        //Red color scheme for iCheck
	        $('input[type="checkbox"].minimal-green, input[type="radio"].minimal-green').iCheck({
	          checkboxClass: 'icheckbox_minimal-green',
	          radioClass   : 'iradio_minimal-green'
	        });
            bindBtnClickEvent();
            $._pagination.init(consts.domain,{
            	request_url : urls.managerGroupPageQuery,
            	request_params : {
            		page_no : 1
            	},
            	pagebar_container : $('.pagination'),
            	is_auto_request : true,
            	gather_request_params : function(domain){
            		var is_intercept = false;
            		$._pagination.domains[domain].request_params['account'] = $('#account').val();
            		$._pagination.domains[domain].request_params['name'] = $('#name').val();
            		$._pagination.domains[domain].request_params['groupName'] = $('#groupName').val();
            		$._pagination.domains[domain].request_params['state'] = $('#state').val();
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
                            '<td>',record.manager.name,'</td>',
                            '<td>',record.group.name,'</td>',
                            '<td>',(record.show=='1'?'是':'否'),'</td>',
                            '<td>',(record.master=='1'?'是':'否'),'</td>',
                            '<td><span class="badge bg-',(record.state==1?'green':'default'),'">',(record.state==1?'有效':'无效'),'</span></td>',
                            '<td>',record.createDate,'</td>',
                            '<td><a class="btn bg-purple btn-flat btn-xs opt-edit">修改</a></td>',
                          '</tr>'
            			];
            			rowsHtml.push(dataRowHtml.join(''));
            		});
            		$('#manager-group-list tbody').empty().append(rowsHtml.length > 0 ? rowsHtml.join('') : "<tr class='text-center'><td colspan=9>暂无数据</td></tr>");
            		if(rowsHtml.length > 0){
            			$('#manager-group-list tbody .opt-edit').off('click').on('click',function(){
                        	$('#module-manager-group-query').removeClass('show').addClass('hide');
                        	$('#module-manager-group-add-edit').removeClass('hide').addClass('show');
            				initEditContainer(this);
            			});
            		}
            	}
            });
        },
        bindBtnClickEvent = function(){
            $('#managerGroupQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
    		$('#edit-groupName').parent('div').off('click').on('click', function() {
    			showMenu(); 
    			return false;
    		});
            $('#conditionReset').off('click').on('click',function(){
            	$('#account').val('');
            	$('#name').val('');
            	$('#groupName').val('');
            	$('#state').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-manager-group-add-edit').removeClass('show').addClass('hide');
            	$('#module-manager-group-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addManagerGroup').off('click').on('click',function(){
            	$('#module-manager-group-query').removeClass('show').addClass('hide');
            	$('#module-manager-group-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#treeStructView').off('click').on('click',function(){
            	$('#module-manager-group-query').removeClass('show').addClass('hide');
            	$('#module-manager-group-tree').removeClass('hide').addClass('show');
            	buildViewTree();
            });
            $('#treeViewReturn').off('click').on('click',function(){
            	$('#module-manager-group-tree').removeClass('show').addClass('hide');
            	$('#module-manager-group-query').removeClass('hide').addClass('show');
            });
            $('#editSave').off('click').on('click',function(){
                if(collectManagerGroupInfo()==consts.ret_success){
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
        collectManagerGroupInfo = function(){
            var returnMsg = consts.ret_success;
            info = {};
            info['id'] = $('#edit-id').val();
            info['managerId'] = $('#edit-manager').val();
            if($._sysbus.isEmpty(info['managerId'])){
            	$._sysbus.showAlert("请选择账号！",null);
            	returnMsg = consts.ret_fail;
            }
            info['groupId'] = $('#edit-groupId').html();
            if($._sysbus.isEmpty(info['groupId']) || info['groupId']=='0'){
            	$._sysbus.showAlert("请选择组织！",null);
            	returnMsg = consts.ret_fail;
            }
            info['show'] = $('input[name="edit-show"]:checked').val();
            info['master'] = $('input[name="edit-master"]:checked').val();
            info['state'] = $('#edit-state').val();
            return returnMsg;
        },
        initAddContainer = function(){
        	$._sysbus.showMask(false);
        	cleanAddEditContainer();
        	$('#module-manager-group-add-edit .box-title').html('新增组织人员');
        	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
        	initAddEditContainer(function(){});
        },
        initEditContainer = function(eventObject){
        	$._sysbus.showMask(false);
        	cleanAddEditContainer();
        	$('#module-manager-group-add-edit .box-title').html('修改组织人员');
        	$('#edit-id').parent().parent().removeClass('hide').addClass('show');
        	initAddEditContainer(function(){
            	var $tr = $(eventObject).parent().parent('tr');
            	var record = dataCache[$tr.attr('data-id')];
            	$('#edit-id').val(record.id);
            	$('#edit-manager').val(record.manager.id);
            	$('#edit-groupId').html(record.group.id);
            	$('#edit-groupName').val(record.group.name);
            	$('input[name="edit-show"][value="'+record.show+'"]').iCheck('check');
            	$('input[name="edit-master"][value="'+record.master+'"]').iCheck('check');
            	$('#edit-state').val(record.state);
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
                        finalFunc();
                        $._sysbus.hideMask();
                    }
                }
            });
        },
        cleanAddEditContainer = function(){
        	$('#module-manager-group-add-edit .box-title').html('设置组织人员');
        	$('#edit-id').val('');
        	$('#edit-manager').select2().val('').trigger('change');
        	$('#edit-groupName').val('');
        	$('#edit-groupId').html('0');
        	$('input[name="edit-show"]').first().iCheck('check');
        	$('input[name="edit-master"]').last().iCheck('check');
        	$('#edit-state').val('1');
        },
        cleanDataCache = function(){
        	// 数据缓存 每当超过100个key则清理
        	if(Object.keys(dataCache).length > 100){
        		dataCache = {};
        	}
        },
		expandNodeParents = function(treeNode,treeFlag){
	        var zTree = $.fn.zTree.getZTreeObj(treeFlag);
	        var pNode = treeNode;
			while(pNode!=null){
				if(pNode.isParent){
					zTree.expandNode(pNode, true, null, null, false);
				}
				pNode = pNode.getParentNode();
			}
		}, 
		showMenu = function(){
			var setting = {
					view: {
						dblClickExpand: false
					},
					data : {
		                key: {
		                	name: "name"
		                },
						simpleData : {
							enable : true,
		                    idKey: "id",
		                    pIdKey: "parentId",
		                    rootPId: '0'
						}
					},
					callback: {
						beforeClick: beforeClick,
						onClick: onClick
					}
			};
	        $._sysbus.request({
	            url : urls.groupAll,
	            ajaxData : $.param({},true),
	            callback : function(data){
	                if(data){
	                    var retData = data.data;
	            		//初始化树
	            		$.fn.zTree.init($("#menuTree"), setting, retData);
	            		var groupNameObj = $("#edit-groupName");
	            		var groupNameOffset = $("#edit-groupName").offset();
	            		$("#menuContent").css({left:(groupNameOffset.left-76) + "px", top:groupNameOffset.top + groupNameObj.outerHeight() + "px"}).slideDown("fast");
	            		$("body").bind("mousedown", onBodyDown);
	            		var zTree = $.fn.zTree.getZTreeObj("menuTree");
	            		var node = zTree.getNodeByParam("id", $("#edit-groupId").html());
	            		expandNodeParents(node,'menuTree');
	            		zTree.selectNode(node);
	                }
	            }
	        });
		}, beforeClick = function(treeId, treeNode){
			
		}, onClick = function(e, treeId, treeNode){
			var zTree = $.fn.zTree.getZTreeObj("menuTree");
			var nodes = zTree.getSelectedNodes();
			if(nodes.length>0){
				$("#edit-groupId").html(nodes[0].id);
				$("#edit-groupName").val(nodes[0].name);
			}
			hideMenu();
		}, hideMenu = function(){
			$("#menuContent").fadeOut("fast");
			$("body").unbind("mousedown", onBodyDown);
		}, onBodyDown = function(event){
			if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
				hideMenu();
			}
		}, buildViewTree = function(){
			$._sysbus.showMask(false);
			var treeId = "viewTree";
			var setting = {
					view: {
						dblClickExpand: false
					},
					data : {
		                key: {
		                	name: "name"
		                },
						simpleData : {
							enable : true,
		                    idKey: "id",
		                    pIdKey: "parentId",
		                    rootPId: ''
						}
					}
			};
			var treeData = [];
			treeData.push({"id":'0',"parentId":"","name":"全部"});
	        $._sysbus.request({
	            url : urls.groupAll,
	            ajaxData : $.param({},true),
	            callback : function(data){
	                if(data){
	                    var retData = data.data;
	                    
	                    //人员挂载数据
	        	        $._sysbus.request({
	        	            url : urls.validQuery,
	        	            ajaxData : $.param({},true),
	        	            callback : function(idata){
	        	                if(idata){
	        	                    var iretData = idata.data;
	        	            		//数据整理
	                                $.each(iretData, function(i,d){
	                                	var manager = {};
	                                	if(d.state=='1'&&d.show=='1'){
	                                		manager['id'] = 'MG:' + d.id + 'M:' + d.managerId;
	                                		manager['parentId'] = d.groupId;
	                                		manager['name'] = $._sysbus.isEmpty(d.manager.name)?d.manager.account:d.manager.name;
	                                		if(d.master=='1')
	                                			manager['name'] = manager['name'] + '（负责人）';
	                                		manager['icon'] = '/resources/Custom/img/employee.png';
	                                		treeData.push(manager);
	                                	}
	                                });
	                                
	                                treeData = treeData.concat(retData);
	        	            		//初始化组织及人员树形结构
	        	            		$.fn.zTree.init($("#"+treeId), setting, treeData);
	        	            		var zTree = $.fn.zTree.getZTreeObj(treeId);
	        	                    var node = zTree.getNodeByParam("id", "0");
	        	                    zTree.expandNode(node, true, null, null, false);
	        	            		
	        	                }
	        	                
	        	                $._sysbus.hideMask();
	        	            }
	        	        });
	                }
	            }
	        });
		};
    ;
    initPageElement();
});