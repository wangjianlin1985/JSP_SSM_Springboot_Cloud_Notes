package com.chengxusheji.service;

import java.util.ArrayList;
import javax.annotation.Resource; 
import org.springframework.stereotype.Service;
import com.chengxusheji.po.Note;
import com.chengxusheji.po.UserInfo;
import com.chengxusheji.po.Collect;

import com.chengxusheji.mapper.CollectMapper;
@Service
public class CollectService {

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

    /*添加笔记收藏记录*/
    public void addCollect(Collect collect) throws Exception {
    	collectMapper.addCollect(collect);
    }

    /*按照查询条件分页查询笔记收藏记录*/
    public ArrayList<Collect> queryCollect(Note noteObj,UserInfo userObj,String collectTime,int currentPage) throws Exception { 
     	String where = "where 1=1";
    	if(null != noteObj && noteObj.getNoteId()!= null && noteObj.getNoteId()!= 0)  where += " and t_collect.noteObj=" + noteObj.getNoteId();
    	if(null != userObj &&  userObj.getUser_name() != null  && !userObj.getUser_name().equals(""))  where += " and t_collect.userObj='" + userObj.getUser_name() + "'";
    	if(!collectTime.equals("")) where = where + " and t_collect.collectTime like '%" + collectTime + "%'";
    	int startIndex = (currentPage-1) * this.rows;
    	return collectMapper.queryCollect(where, startIndex, this.rows);
    }

    /*按照查询条件查询所有记录*/
    public ArrayList<Collect> queryCollect(Note noteObj,UserInfo userObj,String collectTime) throws Exception  { 
     	String where = "where 1=1";
    	if(null != noteObj && noteObj.getNoteId()!= null && noteObj.getNoteId()!= 0)  where += " and t_collect.noteObj=" + noteObj.getNoteId();
    	if(null != userObj &&  userObj.getUser_name() != null && !userObj.getUser_name().equals(""))  where += " and t_collect.userObj='" + userObj.getUser_name() + "'";
    	if(!collectTime.equals("")) where = where + " and t_collect.collectTime like '%" + collectTime + "%'";
    	return collectMapper.queryCollectList(where);
    }

    /*查询所有笔记收藏记录*/
    public ArrayList<Collect> queryAllCollect()  throws Exception {
        return collectMapper.queryCollectList("where 1=1");
    }

    /*当前查询条件下计算总的页数和记录数*/
    public void queryTotalPageAndRecordNumber(Note noteObj,UserInfo userObj,String collectTime) throws Exception {
     	String where = "where 1=1";
    	if(null != noteObj && noteObj.getNoteId()!= null && noteObj.getNoteId()!= 0)  where += " and t_collect.noteObj=" + noteObj.getNoteId();
    	if(null != userObj &&  userObj.getUser_name() != null && !userObj.getUser_name().equals(""))  where += " and t_collect.userObj='" + userObj.getUser_name() + "'";
    	if(!collectTime.equals("")) where = where + " and t_collect.collectTime like '%" + collectTime + "%'";
        recordNumber = collectMapper.queryCollectCount(where);
        int mod = recordNumber % this.rows;
        totalPage = recordNumber / this.rows;
        if(mod != 0) totalPage++;
    }

    /*根据主键获取笔记收藏记录*/
    public Collect getCollect(int collectId) throws Exception  {
        Collect collect = collectMapper.getCollect(collectId);
        return collect;
    }

    /*更新笔记收藏记录*/
    public void updateCollect(Collect collect) throws Exception {
        collectMapper.updateCollect(collect);
    }

    /*删除一条笔记收藏记录*/
    public void deleteCollect (int collectId) throws Exception {
        collectMapper.deleteCollect(collectId);
    }

    /*删除多条笔记收藏信息*/
    public int deleteCollects (String collectIds) throws Exception {
    	String _collectIds[] = collectIds.split(",");
    	for(String _collectId: _collectIds) {
    		collectMapper.deleteCollect(Integer.parseInt(_collectId));
    	}
    	return _collectIds.length;
    }
}
