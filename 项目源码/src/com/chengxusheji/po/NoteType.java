package com.chengxusheji.po;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;

public class NoteType {
    /*笔记类型id*/
    private Integer noteTypeId;
    public Integer getNoteTypeId(){
        return noteTypeId;
    }
    public void setNoteTypeId(Integer noteTypeId){
        this.noteTypeId = noteTypeId;
    }

    /*笔记类型名称*/
    @NotEmpty(message="笔记类型名称不能为空")
    private String noteTypeName;
    public String getNoteTypeName() {
        return noteTypeName;
    }
    public void setNoteTypeName(String noteTypeName) {
        this.noteTypeName = noteTypeName;
    }

    /*笔记类型描述*/
    @NotEmpty(message="笔记类型描述不能为空")
    private String noteTypeDesc;
    public String getNoteTypeDesc() {
        return noteTypeDesc;
    }
    public void setNoteTypeDesc(String noteTypeDesc) {
        this.noteTypeDesc = noteTypeDesc;
    }

    public JSONObject getJsonObject() throws JSONException {
    	JSONObject jsonNoteType=new JSONObject(); 
		jsonNoteType.accumulate("noteTypeId", this.getNoteTypeId());
		jsonNoteType.accumulate("noteTypeName", this.getNoteTypeName());
		jsonNoteType.accumulate("noteTypeDesc", this.getNoteTypeDesc());
		return jsonNoteType;
    }}