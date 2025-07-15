package com.tsj.project;

public class Textbook {
    
    private Integer id;
    private String title;
    private String edition;
    private String author;
    private String publisher;
    private String coverImagePath;
    private String downloadUrl;
    private String publishYear;
    private String description;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getEdition() {
        return edition;
    }
    
    public void setEdition(String edition) {
        this.edition = edition;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getCoverImagePath() {
        return coverImagePath;
    }
    
    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public String getPublishYear() {
        return publishYear;
    }
    
    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 