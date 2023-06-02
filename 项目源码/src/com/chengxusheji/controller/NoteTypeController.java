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
import com.chengxusheji.service.NoteTypeService;
import com.chengxusheji.po.NoteType;

//NoteType管理控制层
@Controller
@RequestMapping("/NoteType")
public class NoteTypeController extends BaseController {

    /*业务层对象*/
    @Resource NoteTypeService noteTypeService;

	@InitBinder("noteType")
	public void initBinderNoteType(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("noteType.");
	}
	/*跳转到添加NoteType视图*/
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model,HttpServletRequest request) throws Exception {
		model.addAttribute(new NoteType());
		return "NoteType_add";
	}

	/*客户端ajax方式提交添加笔记类型信息*/
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@Validated NoteType noteType, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
		boolean success = false;
		if (br.hasErrors()) {
			message = "输入信息不符合要求！";
			writeJsonResponse(response, success, message);
			return ;
		}
        noteTypeService.addNoteType(noteType);
        message = "笔记类型添加成功!";
        success = true;
        writeJsonResponse(response, success, message);
	}
	/*ajax方式按照查询条件分页查询笔记类型信息*/
	@RequestMapping(value = { "/list" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void list(Integer page,Integer rows, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		if (page==null || page == 0) page = 1;
		if(rows != 0)noteTypeService.setRows(rows);
		List<NoteType> noteTypeList = noteTypeService.queryNoteType(page);
	    /*计算总的页数和总的记录数*/
	    noteTypeService.queryTotalPageAndRecordNumber();
	    /*获取到总的页码数目*/
	    int totalPage = noteTypeService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = noteTypeService.getRecordNumber();
        response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象
		JSONObject jsonObj=new JSONObject();
		jsonObj.accumulate("total", recordNumber);
		JSONArray jsonArray = new JSONArray();
		for(NoteType noteType:noteTypeList) {
			JSONObject jsonNoteType = noteType.getJsonObject();
			jsonArray.put(jsonNoteType);
		}
		jsonObj.accumulate("rows", jsonArray);
		out.println(jsonObj.toString());
		out.flush();
		out.close();
	}

	/*ajax方式按照查询条件分页查询笔记类型信息*/
	@RequestMapping(value = { "/listAll" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void listAll(HttpServletResponse response) throws Exception {
		List<NoteType> noteTypeList = noteTypeService.queryAllNoteType();
        response.setContentType("text/json;charset=UTF-8"); 
		PrintWriter out = response.getWriter();
		JSONArray jsonArray = new JSONArray();
		for(NoteType noteType:noteTypeList) {
			JSONObject jsonNoteType = new JSONObject();
			jsonNoteType.accumulate("noteTypeId", noteType.getNoteTypeId());
			jsonNoteType.accumulate("noteTypeName", noteType.getNoteTypeName());
			jsonArray.put(jsonNoteType);
		}
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}

	/*前台按照查询条件分页查询笔记类型信息*/
	@RequestMapping(value = { "/frontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String frontlist(Integer currentPage, Model model, HttpServletRequest request) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		List<NoteType> noteTypeList = noteTypeService.queryNoteType(currentPage);
	    /*计算总的页数和总的记录数*/
	    noteTypeService.queryTotalPageAndRecordNumber();
	    /*获取到总的页码数目*/
	    int totalPage = noteTypeService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = noteTypeService.getRecordNumber();
	    request.setAttribute("noteTypeList",  noteTypeList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
		return "NoteType/noteType_frontquery_result"; 
	}

     /*前台查询NoteType信息*/
	@RequestMapping(value="/{noteTypeId}/frontshow",method=RequestMethod.GET)
	public String frontshow(@PathVariable Integer noteTypeId,Model model,HttpServletRequest request) throws Exception {
		/*根据主键noteTypeId获取NoteType对象*/
        NoteType noteType = noteTypeService.getNoteType(noteTypeId);

        request.setAttribute("noteType",  noteType);
        return "NoteType/noteType_frontshow";
	}

	/*ajax方式显示笔记类型修改jsp视图页*/
	@RequestMapping(value="/{noteTypeId}/update",method=RequestMethod.GET)
	public void update(@PathVariable Integer noteTypeId,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        /*根据主键noteTypeId获取NoteType对象*/
        NoteType noteType = noteTypeService.getNoteType(noteTypeId);

        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象 
		JSONObject jsonNoteType = noteType.getJsonObject();
		out.println(jsonNoteType.toString());
		out.flush();
		out.close();
	}

	/*ajax方式更新笔记类型信息*/
	@RequestMapping(value = "/{noteTypeId}/update", method = RequestMethod.POST)
	public void update(@Validated NoteType noteType, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
    	boolean success = false;
		if (br.hasErrors()) { 
			message = "输入的信息有错误！";
			writeJsonResponse(response, success, message);
			return;
		}
		try {
			noteTypeService.updateNoteType(noteType);
			message = "笔记类型更新成功!";
			success = true;
			writeJsonResponse(response, success, message);
		} catch (Exception e) {
			e.printStackTrace();
			message = "笔记类型更新失败!";
			writeJsonResponse(response, success, message); 
		}
	}
    /*删除笔记类型信息*/
	@RequestMapping(value="/{noteTypeId}/delete",method=RequestMethod.GET)
	public String delete(@PathVariable Integer noteTypeId,HttpServletRequest request) throws UnsupportedEncodingException {
		  try {
			  noteTypeService.deleteNoteType(noteTypeId);
	            request.setAttribute("message", "笔记类型删除成功!");
	            return "message";
	        } catch (Exception e) { 
	            e.printStackTrace();
	            request.setAttribute("error", "笔记类型删除失败!");
				return "error";

	        }

	}

	/*ajax方式删除多条笔记类型记录*/
	@RequestMapping(value="/deletes",method=RequestMethod.POST)
	public void delete(String noteTypeIds,HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		String message = "";
    	boolean success = false;
        try { 
        	int count = noteTypeService.deleteNoteTypes(noteTypeIds);
        	success = true;
        	message = count + "条记录删除成功";
        	writeJsonResponse(response, success, message);
        } catch (Exception e) { 
            //e.printStackTrace();
            message = "有记录存在外键约束,删除失败";
            writeJsonResponse(response, success, message);
        }
	}

	/*按照查询条件导出笔记类型信息到Excel*/
	@RequestMapping(value = { "/OutToExcel" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void OutToExcel( Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
        List<NoteType> noteTypeList = noteTypeService.queryNoteType();
        ExportExcelUtil ex = new ExportExcelUtil();
        String _title = "NoteType信息记录"; 
        String[] headers = { "笔记类型id","笔记类型名称","笔记类型描述"};
        List<String[]> dataset = new ArrayList<String[]>(); 
        for(int i=0;i<noteTypeList.size();i++) {
        	NoteType noteType = noteTypeList.get(i); 
        	dataset.add(new String[]{noteType.getNoteTypeId() + "",noteType.getNoteTypeName(),noteType.getNoteTypeDesc()});
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
			response.setHeader("Content-disposition","attachment; filename="+"NoteType.xls");//filename是下载的xls的名，建议最好用英文 
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
