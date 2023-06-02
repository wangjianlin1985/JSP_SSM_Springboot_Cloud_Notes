package com.chengxusheji.mapper;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Param;
import com.chengxusheji.po.NoteType;

public interface NoteTypeMapper {
	/*添加笔记类型信息*/
	public void addNoteType(NoteType noteType) throws Exception;

	/*按照查询条件分页查询笔记类型记录*/
	public ArrayList<NoteType> queryNoteType(@Param("where") String where,@Param("startIndex") int startIndex,@Param("pageSize") int pageSize) throws Exception;

	/*按照查询条件查询所有笔记类型记录*/
	public ArrayList<NoteType> queryNoteTypeList(@Param("where") String where) throws Exception;

	/*按照查询条件的笔记类型记录数*/
	public int queryNoteTypeCount(@Param("where") String where) throws Exception; 

	/*根据主键查询某条笔记类型记录*/
	public NoteType getNoteType(int noteTypeId) throws Exception;

	/*更新笔记类型记录*/
	public void updateNoteType(NoteType noteType) throws Exception;

	/*删除笔记类型记录*/
	public void deleteNoteType(int noteTypeId) throws Exception;

}
