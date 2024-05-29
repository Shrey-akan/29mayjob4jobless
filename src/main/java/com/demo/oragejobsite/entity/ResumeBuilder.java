package com.demo.oragejobsite.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "resumebuilder")
public class ResumeBuilder {
	@Id
	private String rid;
	private String heading;
	private String skills;
	private String experience;
	private String education;
	private String description;
	

	public ResumeBuilder(String rid, String heading, String skills, String experience, String education,
			String description) {
		super();
		this.rid = rid;
		this.heading = heading;
		this.skills = skills;
		this.experience = experience;
		this.education = education;
		this.description = description;
	}
	public ResumeBuilder() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getSkills() {
		return skills;
	}
	public void setSkills(String skills) {
		this.skills = skills;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

	
}
