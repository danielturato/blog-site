package com.danielturato.blog.model;

public class BlogComment {

    String name;
    String text;

    public BlogComment(String name, String text) {
        if (name.trim().equals("")) {
            this.name = "Anonymous";
        } else {
            this.name = name;
        }
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
