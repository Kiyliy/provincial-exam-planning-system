package com.tsj.project;

public class InternshipInfo {
    
    private Integer id;
    private String name;
    private String direction;
    private String location;
    private String start; // 新增，与考试计划一致
    private Integer duration;
    private String documentUrl;
    private String company;
    private String contactPerson;
    private String contactPhone;
    private String status = "招募中";
    private String state2;
    private String state3;
    private String state4;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }
    // 兼容旧的startDate字段getter/setter，内部操作start
    public String getStartDate() {
        return start;
    }
    public void setStartDate(String startDate) {
        this.start = startDate;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getDocumentUrl() {
        return documentUrl;
    }
    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getState2() {
        return state2;
    }
    
    public void setState2(String state2) {
        this.state2 = state2;
    }
    
    public String getState3() {
        return state3;
    }
    
    public void setState3(String state3) {
        this.state3 = state3;
    }
    
    public String getState4() {
        return state4;
    }
    
    public void setState4(String state4) {
        this.state4 = state4;
    }
} 