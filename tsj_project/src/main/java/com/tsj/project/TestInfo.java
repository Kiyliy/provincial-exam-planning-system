package com.tsj.project;
import java.time.LocalDateTime;

public class TestInfo {
    private Integer id;
    private Integer majorId;
    private String name;
    private String state;
    private String plan;
    private String place;
    private String start;
    private String when;
    
    // 添加关联字段
    private MajorInfo majorInfo;
    
    public MajorInfo getMajorInfo() {
        return majorInfo;
    }
    
    public void setMajorInfo(MajorInfo majorInfo) {
        this.majorInfo = majorInfo;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getMajorId() {
        return majorId;
    }
    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPlan() {
        return plan;
    }
    public void setPlan(String plan) {
        this.plan = plan;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public String getWhen() {
        return when;
    }
    public void setWhen(String when) {
        this.when = when;
    }
} 