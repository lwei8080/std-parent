$(function($) {
	$._pagination = $._pagination || {
		domains : {},
		keys : {
			data_page_info : 'pageInfo',
			data_page_data : 'pageData',
			page_info_total_page : 'totalPage',
			page_info_total_record : 'totalRecord',
			page_info_page_size : 'pageSize'
		},
		no_exp_reg : /^\d{1,}$/,
	    default_settings : {
	    	// 请求数据地址
	    	request_url : '',
	    	// 请求参数
	    	request_params : {
	    		page_no : 1 //默认页码
	    	},
	    	is_auto_request : false,
			/** 示例
			 * 1 2 3 4 
			 * 1 2 3 4 5 6 7 [8]
			 * 1 2 3 [4] 5 6 ... 99 100
			 * 1 2 3 4 5 [6] 7 8 ... 99 100
			 * 1 2 ... 4 5 6 [7] 8 9 ... 99 100
			 * 1 2 ... 93 94 95 [96] 97 98 99 100
			 * 1 2 ... 92 93 94 [95] 96 97 ... 99 100
			 */
	    	// 活跃页码[]左侧连续页码个数
			active_left_count : 3,
	    	// 活跃页码[]右侧连续页码个数
			active_right_count : 2,
	    	// 首部页码个数
			start_count : 2,
	    	// 尾部页码个数
			end_count : 2,
			// 分页控件条所在容器
			pagebar_container : undefined,
			// 总页数
			total_page : 0,
			// 总记录数
			total_record : 0,
			// 每页记录数
			page_size : 0,
			// 返回记录
			records : [],
			// 采集请求参数
			gather_request_params : function(domain){},
			// 数据渲染
			render_data : function(domain){}
	    },
		init : function(domain,settings){
			if($.isEmptyObject($._pagination.domains[domain])){
				$._pagination.domains[domain] = {};
				$.extend($._pagination.domains[domain],$._pagination.default_settings,settings || {});
			}else{
				$.extend($._pagination.domains[domain],settings || {});
			}
			if($._pagination.domains[domain].is_auto_request){
				$._pagination.to_page(domain,settings);
			}
		},
		refresh_page : function(domain){
			$._pagination.to_page(domain,{});
		},
		to_page : function(domain,settings){
			var is_intercept = $._pagination.domains[domain].gather_request_params(domain);
			if(is_intercept){
				return;
			}
			$._sysbus.showMask(false);
			if($.type(settings['request_params'])!='undefined'){
				$.extend($._pagination.domains[domain].request_params,settings['request_params']);
				delete settings['request_params'];
			}
			$.extend($._pagination.domains[domain],settings || {});
			$._sysbus.request({
				url : $._pagination.domains[domain].request_url,
				type : 'get',
				ajaxData : $.param($._pagination.domains[domain].request_params,true),
                callback : function(data){
                    if($.isPlainObject(data)){
                        var pageInfo = data[$._pagination.keys.data_page_info] || {};
                        $.extend($._pagination.domains[domain],{
                        	total_page : parseInt(pageInfo[$._pagination.keys.page_info_total_page],10), 
                        	total_record : parseInt(pageInfo[$._pagination.keys.page_info_total_record],10),
                        	page_size : parseInt(pageInfo[$._pagination.keys.page_info_page_size],10)
                        });
                        var pageData = data[$._pagination.keys.data_page_data] || {};
                        $.extend($._pagination.domains[domain],{
                        	records : pageData
                        });
                        $._pagination.render_pagebar(domain);
                        $._sysbus.hideMask();
                        //if($.type(data[$._pagination.keys.data_page_info])=='undefined'||$.type(data[$._pagination.keys.data_page_data])=='undefined')
                        //	$._sysbus.showAlert('服务端返回数据结构不匹配！',null);
                    }else{
                    	$._sysbus.hideMask();
                    	$._sysbus.showAlert('服务端返回数据结构不匹配！',null);
                    }
                }
			});
		},
		render_pagebar : function(domain){
            var elms = [],
            previous 	= 		'<li><a href="javascript:void(0);">&lt;上一页</a></li>',
            skip 		= 		'<li><a href="javascript:void(0);">&hellip;</a></li>',
            next 		= 		'<li><a href="javascript:void(0);">下一页&gt;</a></li>',
            index = function(i){
                return ['<li><a href="javascript:void(0);">' , i , '</a></li>'].join('');
            };
            // 清空分页容器
            $._pagination.domains[domain].pagebar_container.empty();
            if($._pagination.domains[domain].total_record > 0){
            	elms.push(previous);
            	var current_page_no = parseInt($._pagination.domains[domain].request_params.page_no,10);
            	var active_all_count_max = $._pagination.domains[domain].active_left_count + 1 + $._pagination.domains[domain].active_right_count;
            	
            	var page_left_threshold = current_page_no - $._pagination.domains[domain].active_left_count;
            	var page_right_threshold = current_page_no + $._pagination.domains[domain].active_right_count;
            	var page_append = 0;
            	if(page_left_threshold < 1){
            		page_append = 1 - page_left_threshold;
            		page_left_threshold = 1;
            		page_right_threshold = page_right_threshold + page_append;
            	}
            	page_append = 0;
            	if(page_right_threshold > $._pagination.domains[domain].total_page){
            		page_append = $._pagination.domains[domain].total_page - page_right_threshold;
            		page_right_threshold = $._pagination.domains[domain].total_page;
            		page_left_threshold = page_left_threshold + page_append;
            	}
            	if(page_left_threshold < 1){
            		page_left_threshold = 1;
            	}
            	if(current_page_no >= $._pagination.domains[domain].total_page){
            		page_right_threshold = current_page_no;
            	}
            	var prev_no = 0;
            	if(page_left_threshold > $._pagination.domains[domain].start_count){
                    for (var i=1; i <= $._pagination.domains[domain].start_count; i++) {
                    	if(i-prev_no!=1)
                    		elms.push(skip);
                    	prev_no = i;
                        elms.push(index(i));
                    };
            	}else{
                    for (var i=1; i < page_left_threshold; i++) {
                    	if(i-prev_no!=1)
                    		elms.push(skip);
                    	prev_no = i;
                        elms.push(index(i));
                    };
            	}
            	for (var i = page_left_threshold; i <= page_right_threshold ; i++){
                	if(i-prev_no!=1)
                		elms.push(skip);
                	prev_no = i;
            		elms.push(index(i));
            	}
            	if(page_right_threshold < $._pagination.domains[domain].total_page - $._pagination.domains[domain].end_count + 1){
                    for (var i=$._pagination.domains[domain].total_page - $._pagination.domains[domain].end_count + 1; i <= $._pagination.domains[domain].total_page; i++) {
                    	if(i-prev_no!=1)
                    		elms.push(skip);
                    	prev_no = i;
                        elms.push(index(i));
                    };
            	}else{
                    for (var i=page_right_threshold + 1; i <= $._pagination.domains[domain].total_page; i++) {
                    	if(i-prev_no!=1)
                    		elms.push(skip);
                    	prev_no = i;
                        elms.push(index(i));
                    };
            	}
                elms.push(next);
                $._pagination.domains[domain].pagebar_container.append( elms.join('') );
            }
            $.each($._pagination.domains[domain].pagebar_container.find('li'), function(i, obj){
                var atext = $(obj).text();
                if(atext==$._pagination.domains[domain].request_params.page_no){
                    $(obj).addClass('active');
                }
            });
            $._pagination.domains[domain].pagebar_container.find('li').off('click').on('click',function(){
                var atext = $(this).text();
                if($._pagination.no_exp_reg.test(atext)){
                    if(atext==$._pagination.domains[domain].request_params.page_no){
                        return;
                    }else{
                        $._pagination.to_page(domain,{request_params: {page_no : parseInt(atext,10)}});
                    }

                }else{
                    if(atext=="<上一页"){
                        if($._pagination.domains[domain].request_params.page_no-1<=0) 
                        	return; 
                        else 
                        	$._pagination.to_page(domain,{request_params: {page_no : parseInt($._pagination.domains[domain].request_params.page_no-1,10)}});
                    }
                    if(atext=="下一页>"){
                        if($._pagination.domains[domain].request_params.page_no+1>$._pagination.domains[domain].total_page) 
                        	return; 
                        else 
                        	$._pagination.to_page(domain,{request_params: {page_no : parseInt($._pagination.domains[domain].request_params.page_no+1,10)}});
                    }
                }
            });
            $._pagination.domains[domain].render_data(domain);
		}
	};
});