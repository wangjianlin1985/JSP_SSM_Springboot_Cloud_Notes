package com.chengxusheji.po;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;

public class Note {
    /*笔记id*/
    private Integer noteId;
    public Integer getNoteId(){
        return noteId;
    }
    public void setNoteId(Integer noteId){
        this.noteId = noteId;
    }

    /*笔记类型*/
    private NoteType noteTypeObj;
    public NoteType getNoteTypeObj() {
        return noteTypeObj;
    }
    public void setNoteTypeObj(NoteType noteTypeObj) {
        this.noteTypeObj = noteTypeObj;
    }

    /*摘要标题*/
    @NotEmpty(message="摘要标题不能为空")
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    /*笔记图片*/
    private String notebookPhoto;
    public String getNotebookPhoto() {
        return notebookPhoto;
    }
    public void setNotebookPhoto(String notebookPhoto) {
        this.notebookPhoto = notebookPhoto;
    }

    /*笔记内容描述*/
    @NotEmpty(message="笔记内容描述不能为空")
    private String content;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    /*笔记文件*/
    private String noteFile;
    public String getNoteFile() {
        return noteFile;
    }
    public void setNoteFile(String noteFile) {
        this.noteFile = noteFile;
    }

    /*是否回收站*/
    @NotEmpty(message="是否回收站不能为空")
    private String deleteFlag;
    public String getDeleteFlag() {
        return deleteFlag;
    }
    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /*上传用户*/
    private UserInfo userObj;
    public UserInfo getUserObj() {
        return userObj;
    }
    public void setUserObj(UserInfo userObj) {
        this.userObj = userObj;
    }

    /*上传时间*/
    @NotEmpty(message="上传时间不能为空")
    private String addTime;
    public String getAddTime() {
        return addTime;
    }
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public JSONObject getJsonObject() throws JSONException {
    	JSONObject jsonNote=new JSONObject(); 
		jsonNote.accumulate("noteId", this.getNoteId());
		jsonNote.accumulate("noteTypeObj", this.getNoteTypeObj().getNoteTypeName());
		jsonNote.accumulate("noteTypeObjPri", this.getNoteTypeObj().getNoteTypeId());
		jsonNote.accumulate("title", this.getTitle());
		jsonNote.accumulate("notebookPhoto", this.getNotebookPhoto());
		jsonNote.accumulate("content", this.getContent());
		jsonNote.accumulate("noteFile", this.getNoteFile());
		jsonNote.accumulate("deleteFlag", this.getDeleteFlag());
		jsonNote.accumulate("userObj", this.getUserObj().getName());
		jsonNote.accumulate("userObjPri", this.getUserObj().getUser_name());
		jsonNote.accumulate("addTime", this.getAddTime().length()>19?this.getAddTime().substring(0,19):this.getAddTime());
		return jsonNote;
    }}