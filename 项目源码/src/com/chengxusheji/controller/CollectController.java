﻿package com.chengxusheji.controller;

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
import com.chengxusheji.po.Collect;
import com.chengxusheji.service.NoteService;
import com.chengxusheji.po.Note;
import com.chengxusheji.service.UserInfoService;
import com.chengxusheji.po.UserInfo;

//Collect管理控制层
@Controller
@RequestMapping("/Collect")
public class CollectController extends BaseController {

    /*业务层对象*/
    @Resource CollectService collectService;

    @Resource NoteService noteService;
    @Resource UserInfoService userInfoService;
	@InitBinder("noteObj")
	public void initBindernoteObj(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("noteObj.");
	}
	@InitBinder("userObj")
	public void initBinderuserObj(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("userObj.");
	}
	@InitBinder("collect")
	public void initBinderCollect(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("collect.");
	}
	/*跳转到添加Collect视图*/
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model,HttpServletRequest request) throws Exception {
		model.addAttribute(new Collect());
		/*查询所有的Note信息*/
		List<Note> noteList = noteService.queryAllNote();
		request.setAttribute("noteList", noteList);
		/*查询所有的UserInfo信息*/
		List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
		request.setAttribute("userInfoList", userInfoList);
		return "Collect_add";
	}

	/*客户端ajax方式提交添加笔记收藏信息*/
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@Validated Collect collect, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
		boolean success = false;
		if (br.hasErrors()) {
			message = "输入信息不符合要求！";
			writeJsonResponse(response, success, message);
			return ;
		}
        collectService.addCollect(collect);
        message = "笔记收藏添加成功!";
        success = true;
        writeJsonResponse(response, success, message);
	}
	
	
	/*客户端ajax方式提交添加笔记收藏信息*/
	@RequestMapping(value = "/userAdd", method = RequestMethod.POST)
	public void userAdd(Collect collect, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		String message = "";
		boolean success = false;
		if (br.hasErrors()) {
			message = "输入信息不符合要求！";
			writeJsonResponse(response, success, message);
			return ;
		}
		
		String user_name = (String)session.getAttribute("user_name");
		if(null == user_name) {
			message = "请先登录网站！";
			writeJsonResponse(response, success, message);
			return ;
		}
		
		
		int noteId = collect.getNoteObj().getNoteId();
		String noteUserName = noteService.getNote(noteId).getUserObj().getUser_name();
		if(noteUserName.equals(user_name)) {
			message = "你不能收藏自己的笔记！";
			writeJsonResponse(response, success, message);
			return ;
		}
		
		UserInfo userObj = new UserInfo();
		userObj.setUser_name(user_name);
		if(collectService.queryCollect(collect.getNoteObj(), userObj, "").size() > 0) {
			message = "你已经收藏过这个笔记！";
			writeJsonResponse(response, success, message);
			return ;
		}
		
		
		collect.setUserObj(userObj);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		collect.setCollectTime(sdf.format(new java.util.Date()));
		
        collectService.addCollect(collect);
        message = "笔记收藏添加成功!";
        success = true;
        writeJsonResponse(response, success, message);
	}
	
	
	
	/*ajax方式按照查询条件分页查询笔记收藏信息*/
	@RequestMapping(value = { "/list" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void list(@ModelAttribute("noteObj") Note noteObj,@ModelAttribute("userObj") UserInfo userObj,String collectTime,Integer page,Integer rows, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		if (page==null || page == 0) page = 1;
		if (collectTime == null) collectTime = "";
		if(rows != 0)collectService.setRows(rows);
		List<Collect> collectList = collectService.queryCollect(noteObj, userObj, collectTime, page);
	    /*计算总的页数和总的记录数*/
	    collectService.queryTotalPageAndRecordNumber(noteObj, userObj, collectTime);
	    /*获取到总的页码数目*/
	    int totalPage = collectService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = collectService.getRecordNumber();
        response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象
		JSONObject jsonObj=new JSONObject();
		jsonObj.accumulate("total", recordNumber);
		JSONArray jsonArray = new JSONArray();
		for(Collect collect:collectList) {
			JSONObject jsonCollect = collect.getJsonObject();
			jsonArray.put(jsonCollect);
		}
		jsonObj.accumulate("rows", jsonArray);
		out.println(jsonObj.toString());
		out.flush();
		out.close();
	}

	/*ajax方式按照查询条件分页查询笔记收藏信息*/
	@RequestMapping(value = { "/listAll" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void listAll(HttpServletResponse response) throws Exception {
		List<Collect> collectList = collectService.queryAllCollect();
        response.setContentType("text/json;charset=UTF-8"); 
		PrintWriter out = response.getWriter();
		JSONArray jsonArray = new JSONArray();
		for(Collect collect:collectList) {
			JSONObject jsonCollect = new JSONObject();
			jsonCollect.accumulate("collectId", collect.getCollectId());
			jsonArray.put(jsonCollect);
		}
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}

	/*前台按照查询条件分页查询笔记收藏信息*/
	@RequestMapping(value = { "/frontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String frontlist(@ModelAttribute("noteObj") Note noteObj,@ModelAttribute("userObj") UserInfo userObj,String collectTime,Integer currentPage, Model model, HttpServletRequest request) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (collectTime == null) collectTime = "";
		List<Collect> collectList = collectService.queryCollect(noteObj, userObj, collectTime, currentPage);
	    /*计算总的页数和总的记录数*/
	    collectService.queryTotalPageAndRecordNumber(noteObj, userObj, collectTime);
	    /*获取到总的页码数目*/
	    int totalPage = collectService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = collectService.getRecordNumber();
	    request.setAttribute("collectList",  collectList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("noteObj", noteObj);
	    request.setAttribute("userObj", userObj);
	    request.setAttribute("collectTime", collectTime);
	    List<Note> noteList = noteService.queryAllNote();
	    request.setAttribute("noteList", noteList);
	    List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
	    request.setAttribute("userInfoList", userInfoList);
		return "Collect/collect_frontquery_result"; 
	}

	
	/*前台按照查询条件分页查询笔记收藏信息*/
	@RequestMapping(value = { "/userFrontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String userFrontlist(@ModelAttribute("noteObj") Note noteObj,@ModelAttribute("userObj") UserInfo userObj,String collectTime,Integer currentPage, Model model, HttpServletRequest request,HttpSession session) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (collectTime == null) collectTime = "";
		userObj = new UserInfo();
		userObj.setUser_name(session.getAttribute("user_name").toString());
		
		List<Collect> collectList = collectService.queryCollect(noteObj, userObj, collectTime, currentPage);
	    /*计算总的页数和总的记录数*/
	    collectService.queryTotalPageAndRecordNumber(noteObj, userObj, collectTime);
	    /*获取到总的页码数目*/
	    int totalPage = collectService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = collectService.getRecordNumber();
	    request.setAttribute("collectList",  collectList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("noteObj", noteObj);
	    request.setAttribute("userObj", userObj);
	    request.setAttribute("collectTime", collectTime);
	    List<Note> noteList = noteService.queryAllNote();
	    request.setAttribute("noteList", noteList);
	    List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
	    request.setAttribute("userInfoList", userInfoList);
		return "Collect/collect_userFrontquery_result"; 
	}

	
     /*前台查询Collect信息*/
	@RequestMapping(value="/{collectId}/frontshow",method=RequestMethod.GET)
	public String frontshow(@PathVariable Integer collectId,Model model,HttpServletRequest request) throws Exception {
		/*根据主键collectId获取Collect对象*/
        Collect collect = collectService.getCollect(collectId);

        List<Note> noteList = noteService.queryAllNote();
        request.setAttribute("noteList", noteList);
        List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
        request.setAttribute("userInfoList", userInfoList);
        request.setAttribute("collect",  collect);
        return "Collect/collect_frontshow";
	}

	/*ajax方式显示笔记收藏修改jsp视图页*/
	@RequestMapping(value="/{collectId}/update",method=RequestMethod.GET)
	public void update(@PathVariable Integer collectId,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        /*根据主键collectId获取Collect对象*/
        Collect collect = collectService.getCollect(collectId);

        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象 
		JSONObject jsonCollect = collect.getJsonObject();
		out.println(jsonCollect.toString());
		out.flush();
		out.close();
	}

	/*ajax方式更新笔记收藏信息*/
	@RequestMapping(value = "/{collectId}/update", method = RequestMethod.POST)
	public void update(@Validated Collect collect, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
    	boolean success = false;
		if (br.hasErrors()) { 
			message = "输入的信息有错误！";
			writeJsonResponse(response, success, message);
			return;
		}
		try {
			collectService.updateCollect(collect);
			message = "笔记收藏更新成功!";
			success = true;
			writeJsonResponse(response, success, message);
		} catch (Exception e) {
			e.printStackTrace();
			message = "笔记收藏更新失败!";
			writeJsonResponse(response, success, message); 
		}
	}
    /*删除笔记收藏信息*/
	@RequestMapping(value="/{collectId}/delete",method=RequestMethod.GET)
	public String delete(@PathVariable Integer collectId,HttpServletRequest request) throws UnsupportedEncodingException {
		  try {
			  collectService.deleteCollect(collectId);
	            request.setAttribute("message", "笔记收藏删除成功!");
	            return "message";
	        } catch (Exception e) { 
	            e.printStackTrace();
	            request.setAttribute("error", "笔记收藏删除失败!");
				return "error";

	        }

	}

	/*ajax方式删除多条笔记收藏记录*/
	@RequestMapping(value="/deletes",method=RequestMethod.POST)
	public void delete(String collectIds,HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		String message = "";
    	boolean success = false;
        try { 
        	int count = collectService.deleteCollects(collectIds);
        	success = true;
        	message = count + "条记录删除成功";
        	writeJsonResponse(response, success, message);
        } catch (Exception e) { 
            //e.printStackTrace();
            message = "有记录存在外键约束,删除失败";
            writeJsonResponse(response, success, message);
        }
	}

	/*按照查询条件导出笔记收藏信息到Excel*/
	@RequestMapping(value = { "/OutToExcel" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void OutToExcel(@ModelAttribute("noteObj") Note noteObj,@ModelAttribute("userObj") UserInfo userObj,String collectTime, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
        if(collectTime == null) collectTime = "";
        List<Collect> collectList = collectService.queryCollect(noteObj,userObj,collectTime);
        ExportExcelUtil ex = new ExportExcelUtil();
        String _title = "Collect信息记录"; 
        String[] headers = { "收藏id","被收藏笔记","收藏用户","收藏时间"};
        List<String[]> dataset = new ArrayList<String[]>(); 
        for(int i=0;i<collectList.size();i++) {
        	Collect collect = collectList.get(i); 
        	dataset.add(new String[]{collect.getCollectId() + "",collect.getNoteObj().getTitle(),collect.getUserObj().getName(),collect.getCollectTime()});
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
			response.setHeader("Content-disposition","attachment; filename="+"Collect.xls");//filename是下载的xls的名，建议最好用英文 
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
