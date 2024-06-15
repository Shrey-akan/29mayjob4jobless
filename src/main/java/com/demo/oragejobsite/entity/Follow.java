package com.demo.oragejobsite.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Document(collection = "follow")
public class Follow {
    @Id
    private String id;
    private String uid;
    private String empid; // Updated to use empid

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date sendTime;
    private boolean isFollowing;

    public Follow() {
        super();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        this.sendTime = calendar.getTime();
    }

    public Follow(String id, String uid, String empid, Date sendTime, boolean isFollowing) {
        super();
        this.id = id;
        this.uid = uid;
        this.empid = empid;
        setSendTime(sendTime);
        this.isFollowing = isFollowing;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        if (sendTime != null) {
            calendar.setTime(sendTime);
        }
        this.sendTime = calendar.getTime();
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}
