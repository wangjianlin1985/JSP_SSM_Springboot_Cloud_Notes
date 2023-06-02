<%@ page language="java" import="java.util.*"  contentType="text/html;charset=UTF-8"%> 
<%@ page import="com.chengxusheji.po.Note" %>
<%@ page import="com.chengxusheji.po.NoteType" %>
<%@ page import="com.chengxusheji.po.UserInfo" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    List<Note> noteList = (List<Note>)request.getAttribute("noteList");
    //获取所有的noteTypeObj信息
    List<NoteType> noteTypeList = (List<NoteType>)request.getAttribute("noteTypeList");
    //获取所有的userObj信息
    List<UserInfo> userInfoList = (List<UserInfo>)request.getAttribute("userInfoList");
    int currentPage =  (Integer)request.getAttribute("currentPage"); //当前页
    int totalPage =   (Integer)request.getAttribute("totalPage");  //一共多少页
    int recordNumber =   (Integer)request.getAttribute("recordNumber");  //一共多少记录
    NoteType noteTypeObj = (NoteType)request.getAttribute("noteTypeObj");
    String title = (String)request.getAttribute("title"); //摘要标题查询关键字
    String deleteFlag = (String)request.getAttribute("deleteFlag"); //是否回收站查询关键字
    UserInfo userObj = (UserInfo)request.getAttribute("userObj");
    String addTime = (String)request.getAttribute("addTime"); //上传时间查询关键字
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1 , user-scalable=no">
<title>笔记查询</title>
<link href="<%=basePath %>plugins/bootstrap.css" rel="stylesheet">
<link href="<%=basePath %>plugins/bootstrap-dashen.css" rel="stylesheet">
<link href="<%=basePath %>plugins/font-awesome.css" rel="stylesheet">
<link href="<%=basePath %>plugins/animate.css" rel="stylesheet">
<link href="<%=basePath %>plugins/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
</head>
<body style="margin-top:70px;">
<div class="container">
<jsp:include page="../header.jsp"></jsp:include>
	<div class="col-md-9 wow fadeInLeft">
		<ul class="breadcrumb">
  			<li><a href="<%=basePath %>index.jsp">首页</a></li>
  			<li><a href="<%=basePath %>Note/frontlist">笔记信息列表</a></li>
  			<li class="active">查询结果显示</li>
  			<a class="pull-right" href="<%=basePath %>Note/note_frontAdd.jsp" style="display:none;">添加笔记</a>
		</ul>
		<div class="row">
			<%
				/*计算起始序号*/
				int startIndex = (currentPage -1) * 5;
				/*遍历记录*/
				for(int i=0;i<noteList.size();i++) {
            		int currentIndex = startIndex + i + 1; //当前记录的序号
            		Note note = noteList.get(i); //获取到笔记对象
            		String clearLeft = "";
            		if(i%4 == 0) clearLeft = "style=\"clear:left;\"";
			%>
			<div class="col-md-3 bottom15" <%=clearLeft %>>
			  <a  href="<%=basePath  %>Note/<%=note.getNoteId() %>/frontshow"><img class="img-responsive" src="<%=basePath%><%=note.getNotebookPhoto()%>" /></a>
			     <div class="showFields">
			     	 
			     	<div class="field">
	            		笔记类型:<%=note.getNoteTypeObj().getNoteTypeName() %>
			     	</div>
			     	<div class="field">
	            		摘要标题:<%=note.getTitle() %>
			     	</div>
			     	<div class="field">
	            		笔记文件:<%=note.getNoteFile().equals("")?"暂无文件":"<a href='" + basePath + note.getNoteFile() + "' target='_blank'>" + note.getNoteFile() + "</a>"%>
			     	</div>
			     	 
			     	<div class="field">
	            		上传用户:<%=note.getUserObj().getName() %>
			     	</div>
			     	<div class="field">
	            		上传时间:<%=note.getAddTime() %>
			     	</div>
			        <a class="btn btn-primary top5" href="<%=basePath %>Note/<%=note.getNoteId() %>/frontshow">详情</a>
			        <a class="btn btn-primary top5" onclick="noteEdit('<%=note.getNoteId() %>');" style="display:none;">修改</a>
			        <a class="btn btn-primary top5" onclick="noteDelete('<%=note.getNoteId() %>');" style="display:none;">删除</a>
			     </div>
			</div>
			<%  } %>

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

	<div class="col-md-3 wow fadeInRight">
		<div class="page-header">
    		<h1>笔记查询</h1>
		</div>
		<form name="noteQueryForm" id="noteQueryForm" action="<%=basePath %>Note/frontlist" class="mar_t15" method="post">
            <div class="form-group">
            	<label for="noteTypeObj_noteTypeId">笔记类型：</label>
                <select id="noteTypeObj_noteTypeId" name="noteTypeObj.noteTypeId" class="form-control">
                	<option value="0">不限制</option>
	 				<%
	 				for(NoteType noteTypeTemp:noteTypeList) {
	 					String selected = "";
 					if(noteTypeObj!=null && noteTypeObj.getNoteTypeId()!=null && noteTypeObj.getNoteTypeId().intValue()==noteTypeTemp.getNoteTypeId().intValue())
 						selected = "selected";
	 				%>
 				 <option value="<%=noteTypeTemp.getNoteTypeId() %>" <%=selected %>><%=noteTypeTemp.getNoteTypeName() %></option>
	 				<%
	 				}
	 				%>
 			</select>
            </div>
			<div class="form-group">
				<label for="title">摘要标题:</label>
				<input type="text" id="title" name="title" value="<%=title %>" class="form-control" placeholder="请输入摘要标题">
			</div>
			<div class="form-group" style="display:none;">
				<label for="deleteFlag">是否回收站:</label>
				<input type="text" id="deleteFlag" name="deleteFlag" value="<%=deleteFlag %>" class="form-control" placeholder="请输入是否回收站">
			</div>
            <div class="form-group">
            	<label for="userObj_user_name">上传用户：</label>
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
				<label for="addTime">上传时间:</label>
				<input type="text" id="addTime" name="addTime" class="form-control"  placeholder="请选择上传时间" value="<%=addTime %>" onclick="SelectDate(this,'yyyy-MM-dd')" />
			</div>
            <input type=hidden name=currentPage value="<%=currentPage %>" />
            <button type="submit" class="btn btn-primary">查询</button>
        </form>
	</div>

		</div>
</div>
<div id="noteEditDialog" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog" style="width:900px;" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><i class="fa fa-edit"></i>&nbsp;笔记信息编辑</h4>
      </div>
      <div class="modal-body" style="height:450px; overflow: scroll;">
      	<form class="form-horizontal" name="noteEditForm" id="noteEditForm" enctype="multipart/form-data" method="post"  class="mar_t15">
		  <div class="form-group">
			 <label for="note_noteId_edit" class="col-md-3 text-right">笔记id:</label>
			 <div class="col-md-9"> 
			 	<input type="text" id="note_noteId_edit" name="note.noteId" class="form-control" placeholder="请输入笔记id" readOnly>
			 </div>
		  </div> 
		  <div class="form-group">
		  	 <label for="note_noteTypeObj_noteTypeId_edit" class="col-md-3 text-right">笔记类型:</label>
		  	 <div class="col-md-9">
			    <select id="note_noteTypeObj_noteTypeId_edit" name="note.noteTypeObj.noteTypeId" class="form-control">
			    </select>
		  	 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_title_edit" class="col-md-3 text-right">摘要标题:</label>
		  	 <div class="col-md-9">
			    <input type="text" id="note_title_edit" name="note.title" class="form-control" placeholder="请输入摘要标题">
			 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_notebookPhoto_edit" class="col-md-3 text-right">笔记图片:</label>
		  	 <div class="col-md-9">
			    <img  class="img-responsive" id="note_notebookPhotoImg" border="0px"/><br/>
			    <input type="hidden" id="note_notebookPhoto" name="note.notebookPhoto"/>
			    <input id="notebookPhotoFile" name="notebookPhotoFile" type="file" size="50" />
		  	 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_content_edit" class="col-md-3 text-right">笔记内容描述:</label>
		  	 <div class="col-md-9">
			 	<textarea name="note.content" id="note_content_edit" style="width:100%;height:500px;"></textarea>
			 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_noteFile_edit" class="col-md-3 text-right">笔记文件:</label>
		  	 <div class="col-md-9">
			    <a id="note_noteFileA" target="_blank"></a><br/>
			    <input type="hidden" id="note_noteFile" name="note.noteFile"/>
			    <input id="noteFileFile" name="noteFileFile" type="file" size="50" />
		  	 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_deleteFlag_edit" class="col-md-3 text-right">是否回收站:</label>
		  	 <div class="col-md-9">
			    <input type="text" id="note_deleteFlag_edit" name="note.deleteFlag" class="form-control" placeholder="请输入是否回收站">
			 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_userObj_user_name_edit" class="col-md-3 text-right">上传用户:</label>
		  	 <div class="col-md-9">
			    <select id="note_userObj_user_name_edit" name="note.userObj.user_name" class="form-control">
			    </select>
		  	 </div>
		  </div>
		  <div class="form-group">
		  	 <label for="note_addTime_edit" class="col-md-3 text-right">上传时间:</label>
		  	 <div class="col-md-9">
                <div class="input-group date note_addTime_edit col-md-12" data-link-field="note_addTime_edit">
                    <input class="form-control" id="note_addTime_edit" name="note.addTime" size="16" type="text" value="" placeholder="请选择上传时间" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
		  	 </div>
		  </div>
		</form> 
	    <style>#noteEditForm .form-group {margin-bottom:5px;}  </style>
      </div>
      <div class="modal-footer"> 
      	<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      	<button type="button" class="btn btn-primary" onclick="ajaxNoteModify();">提交</button>
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
<script type="text/javascript" charset="utf-8" src="${pageContext.request.contextPath}/ueditor1_4_3/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="${pageContext.request.contextPath}/ueditor1_4_3/ueditor.all.min.js"> </script>
<script type="text/javascript" charset="utf-8" src="${pageContext.request.contextPath}/ueditor1_4_3/lang/zh-cn/zh-cn.js"></script>
<script>
//实例化编辑器
var note_content_edit = UE.getEditor('note_content_edit'); //笔记内容描述编辑器
var basePath = "<%=basePath%>";
/*跳转到查询结果的某页*/
function GoToPage(currentPage,totalPage) {
    if(currentPage==0) return;
    if(currentPage>totalPage) return;
    document.noteQueryForm.currentPage.value = currentPage;
    document.noteQueryForm.submit();
}

/*可以直接跳转到某页*/
function changepage(totalPage)
{
    var pageValue=document.noteQueryForm.pageValue.value;
    if(pageValue>totalPage) {
        alert('你输入的页码超出了总页数!');
        return ;
    }
    document.noteQueryForm.currentPage.value = pageValue;
    documentnoteQueryForm.submit();
}

/*弹出修改笔记界面并初始化数据*/
function noteEdit(noteId) {
	$.ajax({
		url :  basePath + "Note/" + noteId + "/update",
		type : "get",
		dataType: "json",
		success : function (note, response, status) {
			if (note) {
				$("#note_noteId_edit").val(note.noteId);
				$.ajax({
					url: basePath + "NoteType/listAll",
					type: "get",
					success: function(noteTypes,response,status) { 
						$("#note_noteTypeObj_noteTypeId_edit").empty();
						var html="";
		        		$(noteTypes).each(function(i,noteType){
		        			html += "<option value='" + noteType.noteTypeId + "'>" + noteType.noteTypeName + "</option>";
		        		});
		        		$("#note_noteTypeObj_noteTypeId_edit").html(html);
		        		$("#note_noteTypeObj_noteTypeId_edit").val(note.noteTypeObjPri);
					}
				});
				$("#note_title_edit").val(note.title);
				$("#note_notebookPhoto").val(note.notebookPhoto);
				$("#note_notebookPhotoImg").attr("src", basePath +　note.notebookPhoto);
				note_content_edit.setContent(note.content, false);
				$("#note_noteFile").val(note.noteFile);
				$("#note_noteFileA").text(note.noteFile);
				$("#note_noteFileA").attr("href", basePath +　note.noteFile);
				$("#note_deleteFlag_edit").val(note.deleteFlag);
				$.ajax({
					url: basePath + "UserInfo/listAll",
					type: "get",
					success: function(userInfos,response,status) { 
						$("#note_userObj_user_name_edit").empty();
						var html="";
		        		$(userInfos).each(function(i,userInfo){
		        			html += "<option value='" + userInfo.user_name + "'>" + userInfo.name + "</option>";
		        		});
		        		$("#note_userObj_user_name_edit").html(html);
		        		$("#note_userObj_user_name_edit").val(note.userObjPri);
					}
				});
				$("#note_addTime_edit").val(note.addTime);
				$('#noteEditDialog').modal('show');
			} else {
				alert("获取信息失败！");
			}
		}
	});
}

/*删除笔记信息*/
function noteDelete(noteId) {
	if(confirm("确认删除这个记录")) {
		$.ajax({
			type : "POST",
			url : basePath + "Note/deletes",
			data : {
				noteIds : noteId,
			},
			success : function (obj) {
				if (obj.success) {
					alert("删除成功");
					$("#noteQueryForm").submit();
					//location.href= basePath + "Note/frontlist";
				}
				else 
					alert(obj.message);
			},
		});
	}
}

/*ajax方式提交笔记信息表单给服务器端修改*/
function ajaxNoteModify() {
	$.ajax({
		url :  basePath + "Note/" + $("#note_noteId_edit").val() + "/update",
		type : "post",
		dataType: "json",
		data: new FormData($("#noteEditForm")[0]),
		success : function (obj, response, status) {
            if(obj.success){
                alert("信息修改成功！");
                $("#noteQueryForm").submit();
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

    /*上传时间组件*/
    $('.note_addTime_edit').datetimepicker({
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

