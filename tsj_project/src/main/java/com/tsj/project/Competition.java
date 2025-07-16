package com.tsj.project;

public class Competition {
    private Integer id;
    private String name;
    private String organizer;
    private String deadline;
    private String level;
    private String description;
    private String category;
    private String signupLink;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSignupLink() { return signupLink; }
    public void setSignupLink(String signupLink) { this.signupLink = signupLink; }
} 