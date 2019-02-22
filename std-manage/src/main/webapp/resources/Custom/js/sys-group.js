$(function($) {
	var info = {}, dataCache = {}, consts = {
		domain : 'group',
		ret_success : 'success',
		ret_fail : 'fail',
		table_column : {
			c1 : '40%', c2 : '10%', c3 : '10%', c4 : '10%', c5 : '10%', c6 : '20%'
		}
	}, urls = {
		groupAll : $._sysbus.host + '/manage/group/all',
		addOrUpdate : $._sysbus.host + '/manage/group/addOrUpdate'
	}, initPageElement = function() {
		bindBtnClickEvent();
		queryGroups();
	}, queryGroups = function(finalFunc){
		var queryParams = {};
		queryParams["name"] = $('#name').val();
		queryParams["state"] = $('#state').val();
        $._sysbus.request({
            url : urls.groupAll,
            ajaxData : $.param(queryParams,true),
            callback : function(data){
                if(data){
                    var retData = data.data;
                    buildZTreeTable(retData);
                    if(finalFunc)
                    	finalFunc();
                }
            }
        });
	}, bindBtnClickEvent = function() {
		$('#groupQuery').off('click').on('click', function() {
			queryGroups();
		});
		$('#conditionReset').off('click').on('click', function() {
        	$('#state').val('');
        	$('#name').val('');
		});
		$('#edit-parentName').parent('div').off('click').on('click', function() {
			showMenu(); 
			return false;
		});
		$('#editReturn').off('click').on('click', function() {
        	$('#module-group-add-edit').removeClass('show').addClass('hide');
        	$('#module-group-query').removeClass('hide').addClass('show');
        	var nodeId = $('#edit-id').val();
        	var parentNodeId = $('#edit-parentId').html();
        	cleanAddEditContainer();
        	queryGroups(function(){
                var zTree = $.fn.zTree.getZTreeObj('dataTree');
                var node = zTree.getNodeByParam("id", nodeId);
                if(node!=null){
                	expandNodeParents(node,'dataTree');
                	zTree.selectNode(node);
                }else{
                	node = zTree.getNodeByParam("id", parentNodeId);
                	if(node!=null){
                    	expandNodeParents(node,'dataTree');
                    	zTree.selectNode(node);
                	}
                }

        	});
		});
		$('#addGroup').off('click').on('click', function() {
        	$('#module-group-query').removeClass('show').addClass('hide');
        	$('#module-group-add-edit').removeClass('hide').addClass('show');
        	initAddContainer();
		});
		$('#editSave').off('click').on('click', function() {
            if(collectGroupInfo()==consts.ret_success){
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
	}, addDiyDom = function(treeId, treeNode) {
		//重绘第一列
		var spaceWidth = 15;
		var liObj = $("#" + treeNode.tId);
		var aObj = $("#" + treeNode.tId + "_a");
		var switchObj = $("#" + treeNode.tId + "_switch");
		var icoObj = $("#" + treeNode.tId + "_ico");
		var spanObj = $("#" + treeNode.tId + "_span");
		aObj.attr('title', '');
		aObj.append('<div class="divTd swich fnt" style="width:'+consts.table_column.c1+'"></div>');
		var div = $(liObj).find('div').eq(0);
		//从默认的位置移除
		switchObj.remove();
		spanObj.remove();
		icoObj.remove();
		//在指定的div中添加
		div.append(switchObj);
		div.append(spanObj);
		//隐藏了层次的span
		var spaceSpan = "<span style='height:1px;display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
		switchObj.before(spaceSpan);
		//图标垂直居中
		if(!treeNode.isParent){
			//icoObj.css("margin-top", "9px");
		}else{
			//icoObj.css("margin-top", "9px");
		}
		switchObj.after(icoObj);
		
		//绘制diy列 ，宽度需要和表头保持一致
		var htmlDiyColumn = [
			'<div class="divTd" style="width:'+consts.table_column.c2+'">',treeNode.id,'</div>',
			'<div class="divTd" style="width:'+consts.table_column.c3+'">',treeNode.code,'</div>',
			'<div class="divTd" style="width:'+consts.table_column.c4+'">',treeNode.orderNum,'</div>',
			'<div class="divTd" style="width:'+consts.table_column.c5+'">',(treeNode.state == '1' ? '有效' : '无效'),'</div>',
			'<div class="divTd" style="width:'+consts.table_column.c6+'">',buildButton(treeNode),'</div>'
		];
		aObj.append(htmlDiyColumn.join(''));
		if(treeNode.state != '1'){
			aObj.css('background-color','#F0FFFF');
		}
		
		//绑定事件
		$('.opt-subadd').off('click').on('click', function() {
            var zTree = $.fn.zTree.getZTreeObj('dataTree');
            var node = zTree.getNodeByParam("id", $(this).attr('data-node-id'));
        	$('#module-group-query').removeClass('show').addClass('hide');
        	$('#module-group-add-edit').removeClass('hide').addClass('show');
			initSubAddContainer(node);
		});
		$('.opt-edit').off('click').on('click', function() {
            var zTree = $.fn.zTree.getZTreeObj('dataTree');
            var node = zTree.getNodeByParam("id", $(this).attr('data-node-id'));
        	$('#module-group-query').removeClass('show').addClass('hide');
        	$('#module-group-add-edit').removeClass('hide').addClass('show');
			initEditContainer(node);
		});
	}, buildZTreeTable = function(zTreeNodes) {
		var setting = {
			view : {
				dblClickExpand: true,
				showLine : false,
				txtSelectedEnable: true,
				addDiyDom : addDiyDom
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
			}
		};
		//初始化树
		$.fn.zTree.init($("#dataTree"), setting, zTreeNodes);
		//添加表头
		var htmlHead = [
			'<li class="head">',
			  '<a>',
			    '<div class="divTd" style="width:'+consts.table_column.c1+'">部门名称</div>',
			    '<div class="divTd" style="width:'+consts.table_column.c2+'">部门ID</div>',
			    '<div class="divTd" style="width:'+consts.table_column.c3+'">部门编码</div>',
			    '<div class="divTd" style="width:'+consts.table_column.c4+'">排序号</div>',
			    '<div class="divTd" style="width:'+consts.table_column.c5+'">数据状态</div>',
			    '<div class="divTd" style="width:'+consts.table_column.c6+'">操作</div>',
			  '</a>',
			'</li>'
		].join('');
		var rows = $("#dataTree").find('li');
		if (rows.length > 0) {
			rows.eq(0).before(htmlHead)
		} else {
			$("#dataTree").append(htmlHead);
			$("#dataTree").append('<li ><div style="text-align: center;line-height: 30px;" >暂无数据</div></li>')
		}

	}, buildButton = function(treeNode) {
		var htmlBtn = [
			'<a class="opt-subadd" data-node-id="',treeNode.id,'">添加子级</a>',
			'<a class="opt-edit" style="margin-left:5px;" data-node-id="',treeNode.id,'">修改</a>'
		];
		return htmlBtn.join('');

	}, collectGroupInfo = function() {
		var returnMsg = consts.ret_success;
    	info['id'] = $('#edit-id').val();
    	info['name'] = $('#edit-name').val();
    	info['code'] = $('#edit-code').val();
    	info['parentId'] = $('#edit-parentId').html();
    	info['orderNum'] = $('#edit-orderNum').val();
    	info['state'] = $('#edit-state').val();
        //check
    	if(info['name'].length>50){
            $._sysbus.showAlert('名称字符长度必须小于等于50！',null);
            returnMsg = consts.ret_fail;
    	}else if($.trim(info['name'])==''){
            $._sysbus.showAlert('名称不能为空！',null);
            returnMsg = consts.ret_fail;
        }else if(info['code'].length>50){
            $._sysbus.showAlert('编码字符长度必须小于等于50！',null);
            returnMsg = consts.ret_fail;
        }else if($.trim(info['code'])==''){
            $._sysbus.showAlert('编码不能为空！',null);
            returnMsg = consts.ret_fail;
        }else if(info['orderNum']!=''&&!$._sysbus.regexp.digital.test(info['orderNum'])){
            $._sysbus.showAlert('排序号只能为数字！',null);
            returnMsg = consts.ret_fail;
        }
		return returnMsg;
	}, initAddContainer = function() {
		cleanAddEditContainer();
    	$('#module-group-add-edit .box-title').html('添加组织');
    	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
	}, initEditContainer = function(editNode) {
		cleanAddEditContainer();
    	$('#module-group-add-edit .box-title').html('修改组织');
    	$('#edit-id').parent().parent().removeClass('hide').addClass('show');
    	$('#edit-id').val(editNode.id);
    	$('#edit-name').val(editNode.name);
    	$('#edit-code').val(editNode.code);
    	$('#edit-parentId').html(editNode.parentId);
    	var parentName = '';
    	if(editNode.parentId!='0'){
            var zTree = $.fn.zTree.getZTreeObj('dataTree');
            var node = zTree.getNodeByParam("id", editNode.parentId);
            parentName = node.name;
    	}
    	$('#edit-parentName').val(parentName);
    	$('#edit-orderNum').val(editNode.orderNum);
    	$('#edit-state').val(editNode.state);
	}, initSubAddContainer = function(editNode) {
		cleanAddEditContainer();
    	$('#module-group-add-edit .box-title').html('添加子级组织');
    	$('#edit-id').parent().parent().removeClass('show').addClass('hide');
    	$('#edit-parentId').html(editNode.id);
    	$('#edit-parentName').val(editNode.name);
	}, cleanAddEditContainer = function() {
    	$('#module-group-add-edit .box-title').html('添加或修改组织');
    	$('#edit-id').val('');
    	$('#edit-name').val('');
    	$('#edit-code').val('');
    	$('#edit-parentId').html('0');
    	$('#edit-parentName').val('');
    	$('#edit-orderNum').val('');
    	$('#edit-state').val('1');
	}, cleanDataCache = function() {
		// 数据缓存 每当超过100个key则清理
		if (Object.keys(dataCache).length > 100) {
			dataCache = {};
		}
	}, expandNodeParents = function(treeNode,treeFlag){
        var zTree = $.fn.zTree.getZTreeObj(treeFlag);
        var pNode = treeNode;
		while(pNode!=null){
			if(pNode.isParent){
				zTree.expandNode(pNode, true, null, null, false);
			}
			pNode = pNode.getParentNode();
		}
	}, showMenu = function(){
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
            		var parentNameObj = $("#edit-parentName");
            		var parentNameOffset = $("#edit-parentName").offset();
            		$("#menuContent").css({left:(parentNameOffset.left-76) + "px", top:parentNameOffset.top + parentNameObj.outerHeight() + "px"}).slideDown("fast");
            		$("body").bind("mousedown", onBodyDown);
            		var zTree = $.fn.zTree.getZTreeObj("menuTree");
            		var node = zTree.getNodeByParam("id", $("#edit-parentId").html());
            		expandNodeParents(node,'menuTree');
            		zTree.selectNode(node);
                }
            }
        });
	}, beforeClick = function(treeId, treeNode){
		var editId = $('#edit-id').val();
		var chooseId = treeNode.id;
		// 检测treeNode的所有上级节点是否路由editId
		if(checkNodeParents(treeNode,editId,'menuTree')){
			return false;
		}
	}, onClick = function(e, treeId, treeNode){
		var zTree = $.fn.zTree.getZTreeObj("menuTree");
		var nodes = zTree.getSelectedNodes();
		if(nodes.length>0){
			$("#edit-parentId").html(nodes[0].id);
			$("#edit-parentName").val(nodes[0].name);
		}
		hideMenu();
	}, checkNodeParents = function(treeNode,routingId,treeFlag){
		var isRouting = false;
        var zTree = $.fn.zTree.getZTreeObj(treeFlag);
        var pNode = treeNode;
		while(pNode!=null){
			if(pNode.id==routingId){
				isRouting = true;
				break;
			}	
			pNode = pNode.getParentNode();
		}
		return isRouting;
	}, hideMenu = function(){
		$("#menuContent").fadeOut("fast");
		$("body").unbind("mousedown", onBodyDown);
	}, onBodyDown = function(event){
		if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
			hideMenu();
		}
	};
	initPageElement();
});