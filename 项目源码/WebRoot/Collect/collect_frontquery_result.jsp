<%@ page language="java" import="java.util.*"  contentType="text/html;charset=UTF-8"%> 
<%@ page import="com.chengxusheji.po.Collect" %>
<%@ page import="com.chengxusheji.po.Note" %>
<%@ page import="com.chengxusheji.po.UserInfo" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    List<Collect> collectList = (List<Collect>)request.getAttribute("collectList");
    //获取所有的noteObj信息
    List<Note> noteList = (List<Note>)request.getAttribute("noteList");
    //获取所有的userObj信息
    List<UserInfo> userInfoList = (List<UserInfo>)request.getAttribute("userInfoList");
    int currentPage =  (Integer)request.getAttribute("currentPage"); //当前页
    int totalPage =   (Integer)request.getAttribute("totalPage");  //一共多少页
    int recordNumber =   (Integer)request.getAttribute("recordNumber");  //一共多少记录
    Note noteObj = (Note)request.getAttribute("noteObj");
    UserInfo userObj = (UserInfo)request.getAttribute("userObj");
    String collectTime = (String)request.getAttribute("collectTime"); //收藏时间查询关键字
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1 , user-scalable=no">
<title>笔记收藏查询</title>
<link href="<%=basePath %>plugins/bootstrap.css" rel="stylesheet">
<link href="<%=basePath %>plugins/bootstrap-dashen.css" rel="stylesheet">
<link href="<%=basePath %>plugins/font-awesome.css" rel="stylesheet">
<link href="<%=basePath %>plugins/animate.css" rel="stylesheet">
<link href="<%=basePath %>plugins/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
</head>
<body style="margin-top:70px;">
<div class="container">
<jsp:include page="../header.jsp"></jsp:include>
	<div class="row"> 
		<div class="col-md-9 wow fadeInDown" data-wow-duration="0.5s">
			<div>
				<!-- Nav tabs -->
				<ul class="nav nav-tabs" role="tablist">
			    	<li><a href="<%=basePath %>index.jsp">首页</a></li>
			    	<li role="presentation" class="active"><a href="#collectListPanel" aria-controls="collectListPanel" role="tab" data-toggle="tab">笔记收藏列表</a></li>
			    	<li role="presentation" ><a href="<%=basePath %>Collect/collect_frontAdd.jsp" style="display:none;">添加笔记收藏</a></li>
				</ul>
			  	<!-- Tab panes -->
			  	<div class="tab-content">
				    <div role="tabpanel" class="tab-pane active" id="collectListPanel">
				    		<div class="row">
				    			<div class="col-md-12 top5">
				    				<div class="table-responsive">
				    				<table class="table table-condensed table-hover">
				    					<tr class="success bold"><td>序号</td><td>收藏id</td><td>被收藏笔记</td><td>收藏用户</td><td>收藏时间</td><td>操作</td></tr>
				    					<% 
				    						/*计算起始序号*/
				    	            		int startIndex = (currentPage -1) * 5;
				    	            		/*遍历记录*/
				    	            		for(int i=0;i<collectList.size();i++) {
					    	            		int currentIndex = startIndex + i + 1; //当前记录的序号
					    	            		Collect collect = collectList.get(i); //获取到笔记收藏对象
 										%>
 										<tr>
 											<td><%=currentIndex %></td>
 											<td><%=collect.getCollectId() %></td>
 											<td><%=collect.getNoteObj().getTitle() %></td>
 											<td><%=collect.getUserObj().getName() %></td>
 											<td><%=collect.getCollectTime() %></td>
 											<td>
 												<a href="<%=basePath  %>Collect/<%=collect.getCollectId() %>/frontshow"><i class="fa fa-info"></i>&nbsp;查看</a>&nbsp;
 												<a href="#" onclick="collectEdit('<%=collect.getCollectId() %>');" style="display:none;"><i class="fa fa-pencil fa-fw"></i>编辑</a>&nbsp;
 												<a href="#" onclick="collectDelete('<%=collect.getCollectId() %>');" style="display:none;"><i class="fa fa-trash-o fa-fw"></i>删除</a>
 											</td> 
 										</tr>
 										<%}%>
				    				</table>
				    				</div>
				    			</div>
				    		</div>

				    		<div class="row">
					            <div class="col-md-12">
						            <nav class="pull-left">
						                <ul class="pagination">
						                    <li><a href="#" onclick="GoToPage(<%=currentPage-1 %>,<%=totalPage %>);" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
						                     <%
						                    	int startPage = currentPage - 5;
						                    	int endPage = currentPage + 5;
						                    	if(startPage < 1) startPage=1;
						                    	if(endPage > totalPage) endPage = totalPage;
						                    	for(int i=startPage;i<=endPage;i++) {
						                    %>
						                    <li class="<%= currentPage==i?"active":"" %>"><a href="#"  onclick="GoToPage(<%=i %>,<%=totalPage %>);"><%=i %></a></li>
						                    <%  } %> 
						                    <li><a href="#" onclick="GoToPage(<%=currentPage+1 %>,<%=totalPage %>);"><span aria-hidden="true">&raquo;</span></a></li>
						                </ul>
						            </nav>
						            <div class="pull-right" style="line-height:75px;" >共有<%=recordNumber %>条记录，当前第 <%=currentPage %>/<%=totalPage %> 页</div>
					            </div>
				            </div> 
				    </div>
				</div>
			</div>
		</div>
	<div class="col-md-3 wow fadeInRight">
		<div class="page-header">
    		<h1>笔记收藏查询</h1>
		</div>
		<form name="collectQueryForm" id="collectQueryForm" action="<%=basePath %>Collect/frontlist" class="mar_t15" method="post">
            <div class="form-group">
            	<label for="noteObj_noteId">被收藏笔记：</label>
                <select id="noteObj_noteId" name="noteObj.noteId" class="form-control">
                	<option value="0">不限制</option>
	 				<%
	 				for(Note noteTemp:noteList) {
	 					String selected = "";
 					if(noteObj!=null && noteObj.getNoteId()!=null && noteObj.getNoteId().intValue()==noteTemp.getNoteId().intValue())
 						selected = "selected";
	 				%>
 				 <option value="<%=noteTemp.getNoteId() %>" <%=selected %>><%=noteTemp.getTitle() %></option>
	 				<%
	 				}
	 				%>
 			</select>
            </div>
            <div class="form-group">
            	<label for="userObj_user_name">收藏用户：</label>
                <select id="userObj_user_name" name="userObj.user_name" class="form-control">
                	<option value="">不限制</option>
	 				<%
	 				for(UserInfo userInfoTemp:userInfoList) {
	 					String selected = "";
 					if(userObj!=null && userObj.getUser_name()!=null && userObj.getUser_name().equals(userInfoTemp.getUser_name()))
 						selected = "selected";
	 				%>
 				 <option value="<%=userInfoTemp.getUser_name() %>" <%=selected %>><%=userInfoTemp.getName() %></option>
	 				<%
	 				}
	 				%>
 			</select>
            </div>
			<div class="form-group">
				<label for="collectTime">收藏时间:</label>
				<input type="text" id="collectTime" name="collectTime" class="form-control"  placeholder="请选择收藏时间" value="<%=collectTime %>" onclick="SelectDate(this,'yyyy-MM-dd')" />
			</div>
            <input type=hidden name=currentPage value="<%=currentPage %>" />
            <button type="submit" class="btn btn-primary">查询</button>
        </form>
	</div>

		</div>
	</div> 
<div id="collectEditDialog" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><i class="fa fa-edit"></i>&nbsp;笔记收藏信息编辑</h4>
      </div>
      <div class="modal-body" style="height:450px; overflow: scroll;">
      	<form class="form-horizontal" name="collectEditForm" id="collectEditForm" enctype="multipart/form-data" method="post"  class="mar_t15">
		  <div class="form-group">
			 <label for="collect_collectId_edit" class="col-md-3 text-right">收藏id:</label>
			 <div class="col-md-9"> 
			 	<input type="text" id="collect_collectId_edit" name="collect.collectId" class="form-control" placeholder="请输入收藏id" readOnly>
			 </div>
		  </div> 
		  <div class="form-group">
		  	 <label for="collect_noteObj_noteId_edit" class="col-md-3 text-right">被收藏笔记:</label>
		  	 <div class="col-md-9">
			    <select id="collect_noteObj_noteId_edit" name="collect.noteObj.noteId" class="form-control">
			    </select>
		  	 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="collect_userObj_user_name_edit" class="col-md-3 text-right">收藏用户:</label>
		  	 <div class="col-md-9">
			    <select id="collect_userObj_user_name_edit" name="collect.userObj.user_name" class="form-control">
			    </select>
		  	 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="collect_collectTime_edit" class="col-md-3 text-right">收藏时间:</label>
		  	 <div class="col-md-9">
                <div class="input-group date collect_collectTime_edit col-md-12" data-link-field="collect_collectTime_edit">
                    <input class="form-control" id="collect_collectTime_edit" name="collect.collectTime" size="16" type="text" value="" placeholder="请选择收藏时间" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
		  	 </div>
		  </div>
		</form> 
	    <style>#collectEditForm .form-group {margin-bottom:5px;}  </style>
      </div>
      <div class="modal-footer"> 
      	<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      	<button type="button" class="btn btn-primary" onclick="ajaxCollectModify();">提交</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<jsp:include page="../footer.jsp"></jsp:include> 
<script src="<%=basePath %>plugins/jquery.min.js"></script>
<script src="<%=basePath %>plugins/bootstrap.js"></script>
<script src="<%=basePath %>plugins/wow.min.js"></script>
<script src="<%=basePath %>plugins/bootstrap-datetimepicker.min.js"></script>
<script src="<%=basePath %>plugins/locales/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="<%=basePath %>js/jsdate.js"></script>
<script>
var basePath = "<%=basePath%>";
/*跳转到查询结果的某页*/
function GoToPage(currentPage,totalPage) {
    if(currentPage==0) return;
    if(currentPage>totalPage) return;
    document.collectQueryForm.currentPage.value = currentPage;
    document.collectQueryForm.submit();
}

/*可以直接跳转到某页*/
function changepage(totalPage)
{
    var pageValue=document.collectQueryForm.pageValue.value;
    if(pageValue>totalPage) {
        alert('你输入的页码超出了总页数!');
        return ;
    }
    document.collectQueryForm.currentPage.value = pageValue;
    documentcollectQueryForm.submit();
}

/*弹出修改笔记收藏界面并初始化数据*/
function collectEdit(collectId) {
	$.ajax({
		url :  basePath + "Collect/" + collectId + "/update",
		type : "get",
		dataType: "json",
		success : function (collect, response, status) {
			if (collect) {
				$("#collect_collectId_edit").val(collect.collectId);
				$.ajax({
					url: basePath + "Note/listAll",
					type: "get",
					success: function(notes,response,status) { 
						$("#collect_noteObj_noteId_edit").empty();
						var html="";
		        		$(notes).each(function(i,note){
		        			html += "<option value='" + note.noteId + "'>" + note.title + "</option>";
		        		});
		        		$("#collect_noteObj_noteId_edit").html(html);
		        		$("#collect_noteObj_noteId_edit").val(collect.noteObjPri);
					}
				});
				$.ajax({
					url: basePath + "UserInfo/listAll",
					type: "get",
					success: function(userInfos,response,status) { 
						$("#collect_userObj_user_name_edit").empty();
						var html="";
		        		$(userInfos).each(function(i,userInfo){
		        			html += "<option value='" + userInfo.user_name + "'>" + userInfo.name + "</option>";
		        		});
		        		$("#collect_userObj_user_name_edit").html(html);
		        		$("#collect_userObj_user_name_edit").val(collect.userObjPri);
					}
				});
				$("#collect_collectTime_edit").val(collect.collectTime);
				$('#collectEditDialog').modal('show');
			} else {
				alert("获取信息失败！");
			}
		}
	});
}

/*删除笔记收藏信息*/
function collectDelete(collectId) {
	if(confirm("确认删除这个记录")) {
		$.ajax({
			type : "POST",
			url : basePath + "Collect/deletes",
			data : {
				collectIds : collectId,
			},
			success : function (obj) {
				if (obj.success) {
					alert("删除成功");
					$("#collectQueryForm").submit();
					//location.href= basePath + "Collect/frontlist";
				}
				else 
					alert(obj.message);
			},
		});
	}
}

/*ajax方式提交笔记收藏信息表单给服务器端修改*/
function ajaxCollectModify() {
	$.ajax({
		url :  basePath + "Collect/" + $("#collect_collectId_edit").val() + "/update",
		type : "post",
		dataType: "json",
		data: new FormData($("#collectEditForm")[0]),
		success : function (obj, response, status) {
            if(obj.success){
                alert("信息修改成功！");
                $("#collectQueryForm").submit();
            }else{
                alert(obj.message);
            } 
		},
		processData: false,
		contentType: false,
	});
}

$(function(){
	/*小屏幕导航点击关闭菜单*/
    $('.navbar-collapse a').click(function(){
        $('.navbar-collapse').collapse('hide');
    });
    new WOW().init();

    /*收藏时间组件*/
    $('.collect_collectTime_edit').datetimepicker({
    	language:  'zh-CN',  //语言
    	format: 'yyyy-mm-dd hh:ii:ss',
    	weekStart: 1,
    	todayBtn:  1,
    	autoclose: 1,
    	minuteStep: 1,
    	todayHighlight: 1,
    	startView: 2,
    	forceParse: 0
    });
})
</script>
</body>
</html>

