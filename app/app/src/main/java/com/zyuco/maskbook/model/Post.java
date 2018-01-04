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
    private Boolean unlock;
    private Boolean like;

    public Integer getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public Double getParameter() {
        return parameter;
    }

    public Integer getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    public Boolean isUnlock() {
        return unlock;
    }

    public Boolean isLike() {
        return like;
    }
}
