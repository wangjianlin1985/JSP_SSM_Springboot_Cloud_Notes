package com.chengxusheji.mapper;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Param;
import com.chengxusheji.po.Note;

public interface NoteMapper {
	/*添加笔记信息*/
	public void addNote(Note note) throws Exception;

	/*按照查询条件分页查询笔记记录*/
	public ArrayList<Note> queryNote(@Param("where") String where,@Param("startIndex") int startIndex,@Param("pageSize") int pageSize) throws Exception;

	/*按照查询条件查询所有笔记记录*/
	public ArrayList<Note> queryNoteList(@Param("where") String where) throws Exception;

	/*按照查询条件的笔记记录数*/
	public int queryNoteCount(@Param("where") String where) throws Exception; 

	/*根据主键查询某条笔记记录*/
	public Note getNote(int noteId) throws Exception;

	/*更新笔记记录*/
	public void updateNote(Note note) throws Exception;

	/*删除笔记记录*/
	public void deleteNote(int noteId) throws Exception;

}
