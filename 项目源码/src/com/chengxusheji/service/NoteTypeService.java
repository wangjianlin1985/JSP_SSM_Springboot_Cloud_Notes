package com.chengxusheji.service;

import java.util.ArrayList;
import javax.annotation.Resource; 
import org.springframework.stereotype.Service;
import com.chengxusheji.po.NoteType;

import com.chengxusheji.mapper.NoteTypeMapper;
@Service
public class NoteTypeService {

	@Resource NoteTypeMapper noteTypeMapper;
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

    /*添加笔记类型记录*/
    public void addNoteType(NoteType noteType) throws Exception {
    	noteTypeMapper.addNoteType(noteType);
    }

    /*按照查询条件分页查询笔记类型记录*/
    public ArrayList<NoteType> queryNoteType(int currentPage) throws Exception { 
     	String where = "where 1=1";
    	int startIndex = (currentPage-1) * this.rows;
    	return noteTypeMapper.queryNoteType(where, startIndex, this.rows);
    }

    /*按照查询条件查询所有记录*/
    public ArrayList<NoteType> queryNoteType() throws Exception  { 
     	String where = "where 1=1";
    	return noteTypeMapper.queryNoteTypeList(where);
    }

    /*查询所有笔记类型记录*/
    public ArrayList<NoteType> queryAllNoteType()  throws Exception {
        return noteTypeMapper.queryNoteTypeList("where 1=1");
    }

    /*当前查询条件下计算总的页数和记录数*/
    public void queryTotalPageAndRecordNumber() throws Exception {
     	String where = "where 1=1";
        recordNumber = noteTypeMapper.queryNoteTypeCount(where);
        int mod = recordNumber % this.rows;
        totalPage = recordNumber / this.rows;
        if(mod != 0) totalPage++;
    }

    /*根据主键获取笔记类型记录*/
    public NoteType getNoteType(int noteTypeId) throws Exception  {
        NoteType noteType = noteTypeMapper.getNoteType(noteTypeId);
        return noteType;
    }

    /*更新笔记类型记录*/
    public void updateNoteType(NoteType noteType) throws Exception {
        noteTypeMapper.updateNoteType(noteType);
    }

    /*删除一条笔记类型记录*/
    public void deleteNoteType (int noteTypeId) throws Exception {
        noteTypeMapper.deleteNoteType(noteTypeId);
    }

    /*删除多条笔记类型信息*/
    public int deleteNoteTypes (String noteTypeIds) throws Exception {
    	String _noteTypeIds[] = noteTypeIds.split(",");
    	for(String _noteTypeId: _noteTypeIds) {
    		noteTypeMapper.deleteNoteType(Integer.parseInt(_noteTypeId));
    	}
    	return _noteTypeIds.length;
    }
}
