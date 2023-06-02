package com.chengxusheji.po;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;

public class Collect {
    /*收藏id*/
    private Integer collectId;
    public Integer getCollectId(){
        return collectId;
    }
    public void setCollectId(Integer collectId){
        this.collectId = collectId;
    }

    /*被收藏笔记*/
    private Note noteObj;
    public Note getNoteObj() {
        return noteObj;
    }
    public void setNoteObj(Note noteObj) {
        this.noteObj = noteObj;
    }

    /*收藏用户*/
    private UserInfo userObj;
    public UserInfo getUserObj() {
        return userObj;
    }
    public void setUserObj(UserInfo userObj) {
        this.userObj = userObj;
    }

    /*收藏时间*/
    @NotEmpty(message="收藏时间不能为空")
    private String collectTime;
    public String getCollectTime() {
        return collectTime;
    }
    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public JSONObject getJsonObject() throws JSONException {
    	JSONObject jsonCollect=new JSONObject(); 
		jsonCollect.accumulate("collectId", this.getCollectId());
		jsonCollect.accumulate("noteObj", this.getNoteObj().getTitle());
		jsonCollect.accumulate("noteObjPri", this.getNoteObj().getNoteId());
		jsonCollect.accumulate("userObj", this.getUserObj().getName());
		jsonCollect.accumulate("userObjPri", this.getUserObj().getUser_name());
		jsonCollect.accumulate("collectTime", this.getCollectTime().length()>19?this.getCollectTime().substring(0,19):this.getCollectTime());
		return jsonCollect;
    }}