package com.chengxusheji.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.chengxusheji.utils.ExportExcelUtil;
import com.chengxusheji.utils.UserException;
import com.chengxusheji.service.CollectService;
import com.chengxusheji.service.NoteService;
import com.chengxusheji.po.Collect;
import com.chengxusheji.po.Note;
import com.chengxusheji.service.NoteTypeService;
import com.chengxusheji.po.NoteType;
import com.chengxusheji.service.UserInfoService;
import com.chengxusheji.po.UserInfo;

//Note管理控制层
@Controller
@RequestMapping("/Note")
public class NoteController extends BaseController {

    /*业务层对象*/
    @Resource NoteService noteService;
    @Resource CollectService collectService;

    @Resource NoteTypeService noteTypeService;
    @Resource UserInfoService userInfoService;
	@InitBinder("noteTypeObj")
	public void initBindernoteTypeObj(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("noteTypeObj.");
	}
	@InitBinder("userObj")
	public void initBinderuserObj(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("userObj.");
	}
	@InitBinder("note")
	public void initBinderNote(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("note.");
	}
	/*跳转到添加Note视图*/
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model,HttpServletRequest request) throws Exception {
		model.addAttribute(new Note());
		/*查询所有的NoteType信息*/
		List<NoteType> noteTypeList = noteTypeService.queryAllNoteType();
		request.setAttribute("noteTypeList", noteTypeList);
		/*查询所有的UserInfo信息*/
		List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
		request.setAttribute("userInfoList", userInfoList);
		return "Note_add";
	}

	/*客户端ajax方式提交添加笔记信息*/
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@Validated Note note, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
		boolean success = false;
		if (br.hasErrors()) {
			message = "输入信息不符合要求！";
			writeJsonResponse(response, success, message);
			return ;
		}
		try {
			note.setNotebookPhoto(this.handlePhotoUpload(request, "notebookPhotoFile"));
		} catch(UserException ex) {
			message = "图片格式不正确！";
			writeJsonResponse(response, success, message);
			return ;
		}
		note.setNoteFile(this.handleFileUpload(request, "noteFileFile"));
        noteService.addNote(note);
        message = "笔记添加成功!";
        success = true;
        writeJsonResponse(response, success, message);
	}
	
	
	/*客户端ajax方式提交添加笔记信息*/
	@RequestMapping(value = "/userAdd", method = RequestMethod.POST)
	public void userAdd(Note note, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		String message = "";
		boolean success = false;
		 
		try {
			note.setNotebookPhoto(this.handlePhotoUpload(request, "notebookPhotoFile"));
		} catch(UserException ex) {
			message = "图片格式不正确！";
			writeJsonResponse(response, success, message);
			return ;
		}
		note.setNoteFile(this.handleFileUpload(request, "noteFileFile"));
		
		UserInfo userObj = new UserInfo();
		userObj.setUser_name(session.getAttribute("user_name").toString());
		note.setUserObj(userObj);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		note.setAddTime(sdf.format(new java.util.Date()));
		
        noteService.addNote(note);
        message = "笔记添加成功!";
        success = true;
        writeJsonResponse(response, success, message);
	}
	
	
	/*ajax方式按照查询条件分页查询笔记信息*/
	@RequestMapping(value = { "/list" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void list(@ModelAttribute("noteTypeObj") NoteType noteTypeObj,String title,String deleteFlag,@ModelAttribute("userObj") UserInfo userObj,String addTime,Integer page,Integer rows, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		if (page==null || page == 0) page = 1;
		if (title == null) title = "";
		if (deleteFlag == null) deleteFlag = "";
		if (addTime == null) addTime = "";
		if(rows != 0)noteService.setRows(rows);
		List<Note> noteList = noteService.queryNote(noteTypeObj, title, deleteFlag, userObj, addTime, page);
	    /*计算总的页数和总的记录数*/
	    noteService.queryTotalPageAndRecordNumber(noteTypeObj, title, deleteFlag, userObj, addTime);
	    /*获取到总的页码数目*/
	    int totalPage = noteService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = noteService.getRecordNumber();
        response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象
		JSONObject jsonObj=new JSONObject();
		jsonObj.accumulate("total", recordNumber);
		JSONArray jsonArray = new JSONArray();
		for(Note note:noteList) {
			JSONObject jsonNote = note.getJsonObject();
			jsonArray.put(jsonNote);
		}
		jsonObj.accumulate("rows", jsonArray);
		out.println(jsonObj.toString());
		out.flush();
		out.close();
	}

	/*ajax方式按照查询条件分页查询笔记信息*/
	@RequestMapping(value = { "/listAll" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void listAll(HttpServletResponse response) throws Exception {
		List<Note> noteList = noteService.queryAllNote();
        response.setContentType("text/json;charset=UTF-8"); 
		PrintWriter out = response.getWriter();
		JSONArray jsonArray = new JSONArray();
		for(Note note:noteList) {
			JSONObject jsonNote = new JSONObject();
			jsonNote.accumulate("noteId", note.getNoteId());
			jsonNote.accumulate("title", note.getTitle());
			jsonArray.put(jsonNote);
		}
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}

	/*前台按照查询条件分页查询笔记信息*/
	@RequestMapping(value = { "/frontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String frontlist(@ModelAttribute("noteTypeObj") NoteType noteTypeObj,String title,String deleteFlag,@ModelAttribute("userObj") UserInfo userObj,String addTime,Integer currentPage, Model model, HttpServletRequest request) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (title == null) title = "";
		if (deleteFlag == null) deleteFlag = "";
		if (addTime == null) addTime = "";
		deleteFlag = "否";
		List<Note> noteList = noteService.queryNote(noteTypeObj, title, deleteFlag, userObj, addTime, currentPage);
	    /*计算总的页数和总的记录数*/
	    noteService.queryTotalPageAndRecordNumber(noteTypeObj, title, deleteFlag, userObj, addTime);
	    /*获取到总的页码数目*/
	    int totalPage = noteService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = noteService.getRecordNumber();
	    request.setAttribute("noteList",  noteList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("noteTypeObj", noteTypeObj);
	    request.setAttribute("title", title);
	    request.setAttribute("deleteFlag", deleteFlag);
	    request.setAttribute("userObj", userObj);
	    request.setAttribute("addTime", addTime);
	    List<NoteType> noteTypeList = noteTypeService.queryAllNoteType();
	    request.setAttribute("noteTypeList", noteTypeList);
	    List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
	    request.setAttribute("userInfoList", userInfoList);
		return "Note/note_frontquery_result"; 
	}

	
	
	/*前台按照查询条件分页查询笔记信息*/
	@RequestMapping(value = { "/userFrontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String userFrontlist(@ModelAttribute("noteTypeObj") NoteType noteTypeObj,String title,String deleteFlag,@ModelAttribute("userObj") UserInfo userObj,String addTime,Integer currentPage, Model model, HttpServletRequest request,HttpSession session) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (title == null) title = "";
		if (deleteFlag == null) deleteFlag = "";
		if (addTime == null) addTime = "";
		userObj = new UserInfo();
		userObj.setUser_name(session.getAttribute("user_name").toString());
		deleteFlag = "否";
		
		List<Note> noteList = noteService.queryNote(noteTypeObj, title, deleteFlag, userObj, addTime, currentPage);
	    /*计算总的页数和总的记录数*/
	    noteService.queryTotalPageAndRecordNumber(noteTypeObj, title, deleteFlag, userObj, addTime);
	    /*获取到总的页码数目*/
	    int totalPage = noteService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = noteService.getRecordNumber();
	    request.setAttribute("noteList",  noteList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("noteTypeObj", noteTypeObj);
	    request.setAttribute("title", title);
	    request.setAttribute("deleteFlag", deleteFlag);
	    request.setAttribute("userObj", userObj);
	    request.setAttribute("addTime", addTime);
	    List<NoteType> noteTypeList = noteTypeService.queryAllNoteType();
	    request.setAttribute("noteTypeList", noteTypeList);
	    List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
	    request.setAttribute("userInfoList", userInfoList);
		return "Note/note_userFrontquery_result"; 
	}
	
	
	/*前台按照查询条件分页查询用户回收站中的笔记信息*/
	@RequestMapping(value = { "/userFrontlist2" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String userFrontlist2(@ModelAttribute("noteTypeObj") NoteType noteTypeObj,String title,String deleteFlag,@ModelAttribute("userObj") UserInfo userObj,String addTime,Integer currentPage, Model model, HttpServletRequest request,HttpSession session) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (title == null) title = "";
		if (deleteFlag == null) deleteFlag = "";
		if (addTime == null) addTime = "";
		userObj = new UserInfo();
		userObj.setUser_name(session.getAttribute("user_name").toString());
		deleteFlag = "是";
		
		List<Note> noteList = noteService.queryNote(noteTypeObj, title, deleteFlag, userObj, addTime, currentPage);
	    /*计算总的页数和总的记录数*/
	    noteService.queryTotalPageAndRecordNumber(noteTypeObj, title, deleteFlag, userObj, addTime);
	    /*获取到总的页码数目*/
	    int totalPage = noteService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = noteService.getRecordNumber();
	    request.setAttribute("noteList",  noteList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("noteTypeObj", noteTypeObj);
	    request.setAttribute("title", title);
	    request.setAttribute("deleteFlag", deleteFlag);
	    request.setAttribute("userObj", userObj);
	    request.setAttribute("addTime", addTime);
	    List<NoteType> noteTypeList = noteTypeService.queryAllNoteType();
	    request.setAttribute("noteTypeList", noteTypeList);
	    List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
	    request.setAttribute("userInfoList", userInfoList);
		return "Note/note_userFrontquery_result2"; 
	}
	
	
	
	
     /*前台查询Note信息*/
	@RequestMapping(value="/{noteId}/frontshow",method=RequestMethod.GET)
	public String frontshow(@PathVariable Integer noteId,Model model,HttpServletRequest request) throws Exception {
		/*根据主键noteId获取Note对象*/
        Note note = noteService.getNote(noteId);

        List<NoteType> noteTypeList = noteTypeService.queryAllNoteType();
        request.setAttribute("noteTypeList", noteTypeList);
        List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
        request.setAttribute("userInfoList", userInfoList);
        request.setAttribute("note",  note);
        return "Note/note_frontshow";
	}

	/*ajax方式显示笔记修改jsp视图页*/
	@RequestMapping(value="/{noteId}/update",method=RequestMethod.GET)
	public void update(@PathVariable Integer noteId,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        /*根据主键noteId获取Note对象*/
        Note note = noteService.getNote(noteId);

        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象 
		JSONObject jsonNote = note.getJsonObject();
		out.println(jsonNote.toString());
		out.flush();
		out.close();
	}

	/*ajax方式更新笔记信息*/
	@RequestMapping(value = "/{noteId}/update", method = RequestMethod.POST)
	public void update(@Validated Note note, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
    	boolean success = false;
		if (br.hasErrors()) { 
			message = "输入的信息有错误！";
			writeJsonResponse(response, success, message);
			return;
		}
		String notebookPhotoFileName = this.handlePhotoUpload(request, "notebookPhotoFile");
		if(!notebookPhotoFileName.equals("upload/NoImage.jpg"))note.setNotebookPhoto(notebookPhotoFileName); 


		String noteFileFileName = this.handleFileUpload(request, "noteFileFile");
		if(!noteFileFileName.equals(""))note.setNoteFile(noteFileFileName);
		try {
			noteService.updateNote(note);
			message = "笔记更新成功!";
			success = true;
			writeJsonResponse(response, success, message);
		} catch (Exception e) {
			e.printStackTrace();
			message = "笔记更新失败!";
			writeJsonResponse(response, success, message); 
		}
	}
    /*删除笔记信息*/
	@RequestMapping(value="/{noteId}/delete",method=RequestMethod.GET)
	public String delete(@PathVariable Integer noteId,HttpServletRequest request) throws UnsupportedEncodingException {
		  try {
			  noteService.deleteNote(noteId);
	            request.setAttribute("message", "笔记删除成功!");
	            return "message";
	        } catch (Exception e) { 
	            e.printStackTrace();
	            request.setAttribute("error", "笔记删除失败!");
				return "error";

	        }

	}

	/*ajax方式删除多条笔记记录*/
	@RequestMapping(value="/deletes",method=RequestMethod.POST)
	public void delete(String noteIds,HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		String message = "";
    	boolean success = false;
        try { 
        	int count = noteService.deleteNotes(noteIds);
        	success = true;
        	message = count + "条记录删除成功";
        	writeJsonResponse(response, success, message);
        } catch (Exception e) { 
            //e.printStackTrace();
            message = "有记录存在外键约束,删除失败";
            writeJsonResponse(response, success, message);
        }
	}
	
	

	/*ajax方式清空回收站笔记记录*/
	@RequestMapping(value="/clearNote",method=RequestMethod.POST)
	public void clearNote(String noteIds,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		String message = "";
    	boolean success = false;
    	
    	UserInfo userObj = new UserInfo();
    	userObj.setUser_name(session.getAttribute("user_name").toString());
    	
    	//查询当前用户所有回收站笔记本信息然后遍历删除
    	ArrayList<Note> noteList = noteService.queryNote(null, "", "是", userObj, "");
    	for(Note note: noteList) {
    		ArrayList<Collect> collectList = collectService.queryCollect(note, null, "");
    		for(Collect collect: collectList) {
    			//先删除该笔记的收藏记录
    			collectService.deleteCollect(collect.getCollectId());
    		}
    		noteService.deleteNote(note.getNoteId());
    	}
    	
    	success = true;
    	message = "清空回收站成功";
    	writeJsonResponse(response, success, message);
    	
         
	}
	
	
	
	
	
	
	/*ajax方式还原多条笔记记录*/
	@RequestMapping(value="/restore",method=RequestMethod.POST)
	public void restore(String noteIds,HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		String message = "";
    	boolean success = false;
        try { 
        	int count = noteService.restoreNotes(noteIds);
        	success = true;
        	message = count + "条记录删除成功";
        	writeJsonResponse(response, success, message);
        } catch (Exception e) { 
            //e.printStackTrace();
            message = "有记录存在外键约束,删除失败";
            writeJsonResponse(response, success, message);
        }
	}
	
	
	/*ajax方式操作多条笔记记录进入回收站 */
	@RequestMapping(value="/logicDeletes",method=RequestMethod.POST)
	public void logicDelete(String noteIds,HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		String message = "";
    	boolean success = false;
        try { 
        	int count = noteService.logicDeleteNotes(noteIds);
        	success = true;
        	message = count + "条记录操作成功";
        	writeJsonResponse(response, success, message);
        } catch (Exception e) { 
            //e.printStackTrace();
            message = "有记录存在外键约束,删除失败";
            writeJsonResponse(response, success, message);
        }
	}
	

	/*按照查询条件导出笔记信息到Excel*/
	@RequestMapping(value = { "/OutToExcel" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void OutToExcel(@ModelAttribute("noteTypeObj") NoteType noteTypeObj,String title,String deleteFlag,@ModelAttribute("userObj") UserInfo userObj,String addTime, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
        if(title == null) title = "";
        if(deleteFlag == null) deleteFlag = "";
        if(addTime == null) addTime = "";
        List<Note> noteList = noteService.queryNote(noteTypeObj,title,deleteFlag,userObj,addTime);
        ExportExcelUtil ex = new ExportExcelUtil();
        String _title = "Note信息记录"; 
        String[] headers = { "笔记id","笔记类型","摘要标题","笔记图片","是否回收站","上传用户","上传时间"};
        List<String[]> dataset = new ArrayList<String[]>(); 
        for(int i=0;i<noteList.size();i++) {
        	Note note = noteList.get(i); 
        	dataset.add(new String[]{note.getNoteId() + "",note.getNoteTypeObj().getNoteTypeName(),note.getTitle(),note.getNotebookPhoto(),note.getDeleteFlag(),note.getUserObj().getName(),note.getAddTime()});
        }
        /*
        OutputStream out = null;
		try {
			out = new FileOutputStream("C://output.xls");
			ex.exportExcel(title,headers, dataset, out);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		OutputStream out = null;//创建一个输出流对象 
		try { 
			out = response.getOutputStream();//
			response.setHeader("Content-disposition","attachment; filename="+"Note.xls");//filename是下载的xls的名，建议最好用英文 
			response.setContentType("application/msexcel;charset=UTF-8");//设置类型 
			response.setHeader("Pragma","No-cache");//设置头 
			response.setHeader("Cache-Control","no-cache");//设置头 
			response.setDateHeader("Expires", 0);//设置日期头  
			String rootPath = request.getSession().getServletContext().getRealPath("/");
			ex.exportExcel(rootPath,_title,headers, dataset, out);
			out.flush();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}finally{
			try{
				if(out!=null){ 
					out.close(); 
				}
			}catch(IOException e){ 
				e.printStackTrace(); 
			} 
		}
    }
}
