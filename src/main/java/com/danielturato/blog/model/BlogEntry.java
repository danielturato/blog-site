package com.danielturato.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogEntry {

    List<BlogComment> comments = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    String slug;
    String title;
    LocalDateTime dateTime = LocalDateTime.now();
    String text;

    public BlogEntry(String title, String text, String tags) {
        this.title = title;
        this.text = text;
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (tags != null) {
            tags = tags.trim().replaceAll("\\s+", "");
            String tag = "";
            for (int i = 0; i < tags.length(); i++) {
                if (tags.charAt(i) == ',') {
                    addTag(tag);
                    tag = "";
                    continue;
                }
                tag += tags.charAt(i);
            }
        }
    }

    public List<BlogComment> getComments() {
        return new ArrayList<>(comments);
    }

    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return String.format("%s %d, %d at %d:%d", dateTime.getMonth().toString(), dateTime.getDayOfMonth(),
                             dateTime.getYear(), dateTime.getHour(), dateTime.getMinute());
    }

    public String getText() {
        return text;
    }


    public String getSlug() {
        return slug;
    }

    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addTag(String tag) {
        this.tags.add(new Tag(tag));
    }

    public boolean addComment(BlogComment comment) {
        return comments.add(comment);
    }

}
