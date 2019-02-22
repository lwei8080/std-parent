$(function($) {
    var info = {},
        treeSearch,
        lastValue = "", 
        nodeList = [], 
        fontCss = {},
    	dataCache = {},
    	consts = {
    		domain : 'role-permission',
    		permission_tree_id : 'tree',
    		ret_success : 'success',
    		ret_fail : 'fail'
    	},
        urls = {
    		rolePermissionPageQuery			  :   $._sysbus.host+'/manage/role-permission/pageQuery',
    		addOrUpdate					  	  :   $._sysbus.host+'/manage/role-permission/addOrUpdate',
    		allRoles                          :   $._sysbus.host+'/manage/role/all',
    		allPermisions                     :   $._sysbus.host+'/manage/permission/all',
    		allPermisionsByRole               :   $._sysbus.host+'/manage/permission/byRole'
        },
        initPageElement = function(){
	        //Initialize Select2 Elements
	        $('.select2').select2();
            bindBtnClickEvent();
            $._pagination.init(consts.domain,{
            	request_url : urls.rolePermissionPageQuery,
            	request_params : {
            		page_no : 1
            	},
            	pagebar_container : $('.pagination'),
            	is_auto_request : true,
            	gather_request_params : function(domain){
            		var is_intercept = false;
            		$._pagination.domains[domain].request_params['roleName'] = $('#roleName').val();
            		$._pagination.domains[domain].request_params['permissionName'] = $('#permissionName').val();
            		$._pagination.domains[domain].request_params['url'] = $('#url').val();
            		$._pagination.domains[domain].request_params['permissions'] = $('#permissions').val();
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
                            '<td>',record.role.name,'</td>',
                            '<td>',record.role.description,'</td>',
                            '<td>',record.permission.name,'</td>',
                            '<td>',record.permission.url,'</td>',
                            '<td>',record.permission.permissions,'</td>',
                            '<td><span class="badge bg-',(record.permission.state==1?'green':'default'),'">',(record.permission.state==1?'有效':'无效'),'</span></td>',
                            '<td><a class="btn bg-purple btn-flat btn-xs opt-edit">设置</a></td>',
                          '</tr>'
            			];
            			rowsHtml.push(dataRowHtml.join(''));
            		});
            		$('#role-permission-list tbody').empty().append(rowsHtml.length > 0 ? rowsHtml.join('') : "<tr class='text-center'><td colspan=8>暂无数据</td></tr>");
            		if(rowsHtml.length > 0){
            			$('#role-permission-list tbody .opt-edit').off('click').on('click',function(){
                        	$('#module-role-permission-query').removeClass('show').addClass('hide');
                        	$('#module-role-permission-add-edit').removeClass('hide').addClass('show');
            				initEditContainer(this);
            			});
            		}
            	}
            });
        },
        bindBtnClickEvent = function(){
            $('#rolePermissionQuery').off('click').on('click',function(){
            	$._pagination.to_page(consts.domain,{
                	request_params : {
                		page_no : 1
                	}
            	});
            });
            $('#conditionReset').off('click').on('click',function(){
            	$('#roleName').val('');
            	$('#permissionName').val('');
            	$('#url').val('');
            	$('#permissions').val('');
            	$('#state').val('');
            });
            $('#editReturn').off('click').on('click',function(){
            	$('#module-role-permission-add-edit').removeClass('show').addClass('hide');
            	$('#module-role-permission-query').removeClass('hide').addClass('show');
            	cleanAddEditContainer();
            	$._pagination.refresh_page(consts.domain);
            });
            $('#addRolePermission').off('click').on('click',function(){
            	$('#module-role-permission-query').removeClass('show').addClass('hide');
            	$('#module-role-permission-add-edit').removeClass('hide').addClass('show');
            	initAddContainer();
            });
            $('#edit-role').on('change',function(){
            	var roleId = $(this).val();
            	if(roleId===""){
            		$._sysbus.showAlert("请选择角色！",null);
            	}else{
            		requestAllPermisionsByRole(roleId);
            	}
            });
            $('#editSave').off('click').on('click',function(){
                if(collectRolePermissionInfo()==consts.ret_success){
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
        requestAllPermisionsByRole = function(roleId){
        	var zTree = $.fn.zTree.getZTreeObj(consts.permission_tree_id);
    		zTree.checkAllNodes(false);
    		//zTree.expandAll(false);
    		//zTree.expandNode(zTree.getNodeByParam("id", "0"), true, null, null, false);
            $._sysbus.request({
                url : urls.allPermisionsByRole,
                ajaxData : $.param({"roleId":roleId},true),
                callback : function(data){
                    if(data){
                        var retData = data.data;
                        $.each(retData, function(i,d){
                        	var node = zTree.getNodeByParam("id", d.id);
                        	if(node.isParent){
                        		zTree.expandNode(node, true, null, null, false);
                        	}else{
                        		zTree.checkNode(node,true,true,false);
                        	}
                        });
                    }
                }
            });
        },
        collectRolePermissionInfo = function(){
            var returnMsg = consts.ret_success;
            info = {};
            info['roleId'] = $('#edit-role').val();
            if($._sysbus.isEmpty(info['roleId'])){
            	$._sysbus.showAlert("请选择角色！",null);
            	returnMsg = consts.ret_fail;
            }
            var zTree = $.fn.zTree.getZTreeObj(consts.permission_tree_id);
            var nodes = zTree.getCheckedNodes(true);
            $.each(nodes, function(i,node){
            	var permissionId = node.id;
            	if(permissionId!=0){
            		if($._sysbus.isEmpty(info['permissionIds']))
            			info['permissionIds'] = ""+permissionId;
            		else
            			info['permissionIds'] = info['permissionIds']+"|"+permissionId;
            	}
            });
            return returnMsg;
        },
        initAddContainer = function(){
        	$._sysbus.showMask(false);
        	initAddEditContainer(function(){
            	cleanAddEditContainer();
            	$('#module-role-permission-add-edit .box-title').html('设置角色权限');
        	});
        },
        initEditContainer = function(eventObject){
        	$._sysbus.showMask(false);
        	initAddEditContainer(function(){
            	cleanAddEditContainer();
            	$('#module-role-permission-add-edit .box-title').html('设置角色权限');
            	var $tr = $(eventObject).parent().parent('tr');
            	var record = dataCache[$tr.attr('data-id')];
            	var roleId = record.roleId;
            	$('#edit-role').val(roleId);
            	requestAllPermisionsByRole(roleId);
        	});
        },
        initAddEditContainer = function(finalFunc){
            $._sysbus.request({
                url : urls.allRoles,
                async : true,
                callback : function(data){
                    if(data){
                        var retData = data.data;
                        var optionsHtml = ['<option value="" disabled="disabled" selected="selected">-----选择角色-----</option>'];
                        $.each(retData, function(i,d){
                        	optionsHtml.push('<option value="'+d.id+'">'+d.name||''+'</option>');
                        });
                        $('#edit-role').empty().append(optionsHtml.join(''));
                    }
                    $._sysbus.request({
                        url : urls.allPermisions,
                        async : true,
                        callback : function(data){
                            if(data){
                                var retData = data.data;
                                $.each(retData, function(i,d){
                                	if(!$._sysbus.isEmpty(d.url))
                                		d.name = d.name + "【"+d.url+"】";
                                	d.url = "";
                                });
                                retData.push({"id":0,"parentId":"","name":"全部","permissions":""});
                                buildPermissionTree(consts.permission_tree_id,"id","parentId",retData);
                                treeSearch = $("#tree-search-node");
                                $("#tree-search-node").bind("focus", focusKey).bind("blur", blurKey).bind("propertychange", searchNode).bind("input", searchNode);
                            }
                            finalFunc();
                            $._sysbus.hideMask();
                        }
                    });
                }
            });
        },
        cleanAddEditContainer = function(){
        	$('#module-manager-add-edit .box-title').html('设置角色权限');
        	
        },
        cleanDataCache = function(){
        	// 数据缓存 每当超过100个key则清理
        	if(Object.keys(dataCache).length > 100){
        		dataCache = {};
        	}
        },
		focusKey = function(e) {
			if (treeSearch.hasClass("empty")) {
				treeSearch.removeClass("empty");
			}
		},
		blurKey = function(e) {
			if (treeSearch.get(0).value === "") {
				treeSearch.addClass("empty");
			}
		},
		updateNodes = function(highlight) {
			var zTree = $.fn.zTree.getZTreeObj(consts.permission_tree_id);
			for( var i=0, l=nodeList.length; i<l; i++) {
				nodeList[i].highlight = highlight;
				zTree.updateNode(nodeList[i]);
			}
		},
		getFontCss = function(treeId, treeNode) {
			return (!!treeNode.highlight) ? {color:"#A60000", "font-weight":"bold"} : {color:"#333", "font-weight":"normal"};
		},
		searchNode = function(e) {
			var zTree = $.fn.zTree.getZTreeObj(consts.permission_tree_id);
			var value = $.trim(treeSearch.get(0).value);
			var keyType = "name";
			
			if (treeSearch.hasClass("empty")) {
				value = "";
			}
			
			if (value==="") value = "$~$";
			
			if (lastValue === value) return;
			lastValue = value;
			if (value === "") return;
			updateNodes(false);

			nodeList = zTree.getNodesByParamFuzzy(keyType, value);
			
			updateNodes(true);

			for( var i=0, l=nodeList.length; i<l; i++) {
				expandNodeParents(nodeList[i]);
			}
			
		},
		expandNodeParents = function(treeNode){
            var zTree = $.fn.zTree.getZTreeObj(consts.permission_tree_id);
            var pNode = treeNode;
			while(pNode!=null){
				if(pNode.isParent){
					zTree.expandNode(pNode, true, null, null, false);
				}
				pNode = pNode.getParentNode();
			}
		},
        buildPermissionTree = function(elmId,idKey,pIdKey,zNodes){
            var setting = {
                check: {
                    enable: true,
                    chkStyle: "checkbox",
                    chkboxType: { "Y": "ps", "N": "ps" },
                    nocheckInherit: true,
                    chkDisabledInherit: true
                },
                view: {
                    dblClickExpand: false,
                    showLine: true,
                    selectedMulti: false,
                    showIcon: false,
                    fontCss: getFontCss
                },
                data: {
                    key: {
                    	name: "name",
                        title:"permissions"
                    },
                    simpleData: {
                        enable:true,
                        idKey: idKey,
                        pIdKey: pIdKey,
                        rootPId: ""
                    }
                },
                callback: {
                    beforeClick: function(treeId, treeNode) {
                        var zTree = $.fn.zTree.getZTreeObj(elmId);
                        if (treeNode.isParent) {
                            zTree.expandNode(treeNode);
                            return false;
                        } else {
                            
                            return true;
                        }
                    }
                }
            };
            $.fn.zTree.init($("#"+elmId), setting, zNodes);
            var zTree = $.fn.zTree.getZTreeObj(elmId);
            var node = zTree.getNodeByParam("id", "0");
            zTree.expandNode(node, true, null, null, false);
        }
    ;
    initPageElement();
});