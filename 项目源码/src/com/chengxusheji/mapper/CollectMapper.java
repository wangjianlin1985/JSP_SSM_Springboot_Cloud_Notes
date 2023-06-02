package com.chengxusheji.mapper;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Param;
import com.chengxusheji.po.Collect;

public interface CollectMapper {
	/*添加笔记收藏信息*/
	public void addCollect(Collect collect) throws Exception;

	/*按照查询条件分页查询笔记收藏记录*/
	public ArrayList<Collect> queryCollect(@Param("where") String where,@Param("startIndex") int startIndex,@Param("pageSize") int pageSize) throws Exception;

	/*按照查询条件查询所有笔记收藏记录*/
	public ArrayList<Collect> queryCollectList(@Param("where") String where) throws Exception;

	/*按照查询条件的笔记收藏记录数*/
	public int queryCollectCount(@Param("where") String where) throws Exception; 

	/*根据主键查询某条笔记收藏记录*/
	public Collect getCollect(int collectId) throws Exception;

	/*更新笔记收藏记录*/
	public void updateCollect(Collect collect) throws Exception;

	/*删除笔记收藏记录*/
	public void deleteCollect(int collectId) throws Exception;

}
