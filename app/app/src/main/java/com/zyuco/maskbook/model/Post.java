package com.zyuco.maskbook.model;

import java.util.Date;

public class Post {
    private Integer id;
    private User author;
    private String content;
    private String image;
    private Double parameter;
    private Integer price;
    private Date date;
    private Boolean unlocked;
    private Boolean like;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getParameter() {
        return parameter;
    }

    public void setParameter(Double parameter) {
        this.parameter = parameter;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getUnlock() {
        return unlocked;
    }

    public void setUnlock(Boolean unlock) {
        this.unlocked = unlocked;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }
}
