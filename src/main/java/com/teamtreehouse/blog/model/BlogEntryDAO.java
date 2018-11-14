package com.teamtreehouse.blog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface BlogEntryDAO {

    BlogEntry getEntryBySlug(String slug);

    List<BlogEntry> getEntriesByTag(Tag tag);

    List<BlogEntry> findAll();

    boolean addEntry(BlogEntry entry);

    boolean removeEntry(BlogEntry entry);

    Tag getTagBySlug(String slug);

    Set<Tag> getTags();

    boolean addTag(Tag tag);

}
