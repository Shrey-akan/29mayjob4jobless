package com.demo.oragejobsite.entity;

import java.util.Date;

import java.util.Calendar;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
	

@Document(collection = "postjob")
public class PostJob {
@Id
private String jobid; 
private String empName;
private String empEmail;
private String jobtitle;
private String companyforthisjob;
private Long numberofopening;
private String locationjob;
private String jobtype;
private String schedulejob;
private String payjob;
//private Double payjobsup;
private String descriptiondata;
private String empid;
private int applicants;
private boolean archive;
private boolean approvejob;
private String experience;
//@Field("sendTime")
//@CreatedDate
//private Date sendTime;
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
private Date sendTime;

public PostJob() {
	super();
	 Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
     this.sendTime = calendar.getTime();
	// TODO Auto-generated constructor stub
}
public PostJob(String jobid, String empName, String empEmail, String jobtitle, String companyforthisjob,
		Long numberofopening, String locationjob, String jobtype, String schedulejob, String payjob,
		String descriptiondata, String empid, int applicants, boolean archive, boolean approvejob, String experience,
		Date sendTime) {
	super();
	this.jobid = jobid;
	this.empName = empName;
	this.empEmail = empEmail;
	this.jobtitle = jobtitle;
	this.companyforthisjob = companyforthisjob;
	this.numberofopening = numberofopening;
	this.locationjob = locationjob;
	this.jobtype = jobtype;
	this.schedulejob = schedulejob;
	this.payjob = payjob;
	this.descriptiondata = descriptiondata;
	this.empid = empid;
	this.applicants = applicants;
	this.archive = archive;
	this.approvejob = approvejob;
	this.experience = experience;
	
	 this.sendTime = sendTime != null ? sendTime : Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).getTime();

}
public String getJobid() {
	return jobid;
}
public void setJobid(String jobid) {
	this.jobid = jobid;
}
public String getEmpName() {
	return empName;
}
public void setEmpName(String empName) {
	this.empName = empName;
}
public String getEmpEmail() {
	return empEmail;
}
public void setEmpEmail(String empEmail) {
	this.empEmail = empEmail;
}
public String getJobtitle() {
	return jobtitle;
}
public void setJobtitle(String jobtitle) {
	this.jobtitle = jobtitle;
}
public String getCompanyforthisjob() {
	return companyforthisjob;
}
public void setCompanyforthisjob(String companyforthisjob) {
	this.companyforthisjob = companyforthisjob;
}
public Long getNumberofopening() {
	return numberofopening;
}
public void setNumberofopening(Long numberofopening) {
	this.numberofopening = numberofopening;
}
public String getLocationjob() {
	return locationjob;
}
public void setLocationjob(String locationjob) {
	this.locationjob = locationjob;
}
public String getJobtype() {
	return jobtype;
}
public void setJobtype(String jobtype) {
	this.jobtype = jobtype;
}
public String getSchedulejob() {
	return schedulejob;
}
public void setSchedulejob(String schedulejob) {
	this.schedulejob = schedulejob;
}
public String getPayjob() {
	return payjob;
}
public void setPayjob(String payjob) {
	this.payjob = payjob;
}
public String getDescriptiondata() {
	return descriptiondata;
}
public void setDescriptiondata(String descriptiondata) {
	this.descriptiondata = descriptiondata;
}
public String getEmpid() {
	return empid;
}
public void setEmpid(String empid) {
	this.empid = empid;
}
public int getApplicants() {
	return applicants;
}
public void setApplicants(int applicants) {
	this.applicants = applicants;
}
public boolean isArchive() {
	return archive;
}
public void setArchive(boolean archive) {
	this.archive = archive;
}
public boolean isApprovejob() {
	return approvejob;
}
public void setApprovejob(boolean approvejob) {
	this.approvejob = approvejob;
}
public String getExperience() {
	return experience;
}
public void setExperience(String experience) {
	this.experience = experience;
}
public Date getSendTime() {
	return sendTime;
}
public void setSendTime(Date sendTime) {
	this.sendTime = sendTime;
}



}

