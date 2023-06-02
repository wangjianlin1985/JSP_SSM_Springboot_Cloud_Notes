package com.chengxusheji.service;

import java.util.ArrayList;
import javax.annotation.Resource; 
import org.springframework.stereotype.Service;

import com.chengxusheji.po.Collect;
import com.chengxusheji.po.NoteType;
import com.chengxusheji.po.UserInfo;
import com.chengxusheji.po.Note;

import com.chengxusheji.mapper.CollectMapper;
import com.chengxusheji.mapper.NoteMapper;
@Service
public class NoteService {

	@Resource NoteMapper noteMapper;
	@Resource CollectMapper collectMapper;
	
    /*每页显示记录数目*/
    private int rows = 10;;
    public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}

    /*保存查询后总的页数*/
    private int totalPage;
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
    public int getTotalPage() {
        return totalPage;
    }

    /*保存查询到的总记录数*/
    private int recordNumber;
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }
    public int getRecordNumber() {
        return recordNumber;
    }

    /*添加笔记记录*/
    public void addNote(Note note) throws Exception {
    	noteMapper.addNote(note);
    }

    /*按照查询条件分页查询笔记记录*/
    public ArrayList<Note> queryNote(NoteType noteTypeObj,String title,String deleteFlag,UserInfo userObj,String addTime,int currentPage) throws Exception { 
     	String where = "where 1=1";
    	if(null != noteTypeObj && noteTypeObj.getNoteTypeId()!= null && noteTypeObj.getNoteTypeId()!= 0)  where += " and t_note.noteTypeObj=" + noteTypeObj.getNoteTypeId();
    	if(!title.equals("")) where = where + " and t_note.title like '%" + title + "%'";
    	if(!deleteFlag.equals("")) where = where + " and t_note.deleteFlag like '%" + deleteFlag + "%'";
    	if(null != userObj &&  userObj.getUser_name() != null  && !userObj.getUser_name().equals(""))  where += " and t_note.userObj='" + userObj.getUser_name() + "'";
    	if(!addTime.equals("")) where = where + " and t_note.addTime like '%" + addTime + "%'";
    	int startIndex = (currentPage-1) * this.rows;
    	return noteMapper.queryNote(where, startIndex, this.rows);
    }

    /*按照查询条件查询所有记录*/
    public ArrayList<Note> queryNote(NoteType noteTypeObj,String title,String deleteFlag,UserInfo userObj,String addTime) throws Exception  { 
     	String where = "where 1=1";
    	if(null != noteTypeObj && noteTypeObj.getNoteTypeId()!= null && noteTypeObj.getNoteTypeId()!= 0)  where += " and t_note.noteTypeObj=" + noteTypeObj.getNoteTypeId();
    	if(!title.equals("")) where = where + " and t_note.title like '%" + title + "%'";
    	if(!deleteFlag.equals("")) where = where + " and t_note.deleteFlag like '%" + deleteFlag + "%'";
    	if(null != userObj &&  userObj.getUser_name() != null && !userObj.getUser_name().equals(""))  where += " and t_note.userObj='" + userObj.getUser_name() + "'";
    	if(!addTime.equals("")) where = where + " and t_note.addTime like '%" + addTime + "%'";
    	return noteMapper.queryNoteList(where);
    }

    /*查询所有笔记记录*/
    public ArrayList<Note> queryAllNote()  throws Exception {
        return noteMapper.queryNoteList("where 1=1");
    }

    /*当前查询条件下计算总的页数和记录数*/
    public void queryTotalPageAndRecordNumber(NoteType noteTypeObj,String title,String deleteFlag,UserInfo userObj,String addTime) throws Exception {
     	String where = "where 1=1";
    	if(null != noteTypeObj && noteTypeObj.getNoteTypeId()!= null && noteTypeObj.getNoteTypeId()!= 0)  where += " and t_note.noteTypeObj=" + noteTypeObj.getNoteTypeId();
    	if(!title.equals("")) where = where + " and t_note.title like '%" + title + "%'";
    	if(!deleteFlag.equals("")) where = where + " and t_note.deleteFlag like '%" + deleteFlag + "%'";
    	if(null != userObj &&  userObj.getUser_name() != null && !userObj.getUser_name().equals(""))  where += " and t_note.userObj='" + userObj.getUser_name() + "'";
    	if(!addTime.equals("")) where = where + " and t_note.addTime like '%" + addTime + "%'";
        recordNumber = noteMapper.queryNoteCount(where);
        int mod = recordNumber % this.rows;
        totalPage = recordNumber / this.rows;
        if(mod != 0) totalPage++;
    }

    /*根据主键获取笔记记录*/
    public Note getNote(int noteId) throws Exception  {
        Note note = noteMapper.getNote(noteId);
        return note;
    }

    /*更新笔记记录*/
    public void updateNote(Note note) throws Exception {
        noteMapper.updateNote(note);
    }

    /*删除一条笔记记录*/
    public void deleteNote (int noteId) throws Exception {
        noteMapper.deleteNote(noteId);
    }

    /*删除多条笔记信息*/
    public int deleteNotes (String noteIds) throws Exception {
    	String _noteIds[] = noteIds.split(",");
    	for(String _noteId: _noteIds) {
    		ArrayList<Collect> collectList = collectMapper.queryCollectList(" where t_collect.noteObj=" + _noteId);
    		for(Collect collect: collectList) {
    			collectMapper.deleteCollect(collect.getCollectId());
    		}
    		noteMapper.deleteNote(Integer.parseInt(_noteId));
    	}
    	return _noteIds.length;
    }
    
    /*删除多条笔记信息*/
    public int restoreNotes (String noteIds) throws Exception {
    	String _noteIds[] = noteIds.split(",");
    	for(String _noteId: _noteIds) {
    		Note note = noteMapper.getNote(Integer.parseInt(_noteId));
    		note.setDeleteFlag("否");
    		noteMapper.updateNote(note);
    	}
    	return _noteIds.length;
    }
    
    
    /*操作多条笔记信息进入回收站*/
    public int logicDeleteNotes (String noteIds) throws Exception {
    	String _noteIds[] = noteIds.split(",");
    	for(String _noteId: _noteIds) {
    		Note note = noteMapper.getNote(Integer.parseInt(_noteId));
    		note.setDeleteFlag("是");
    		noteMapper.updateNote(note);
    	}
    	return _noteIds.length;
    }
    
    
}
