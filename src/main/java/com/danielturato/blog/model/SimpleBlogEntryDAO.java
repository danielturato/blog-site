package com.danielturato.blog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SimpleBlogEntryDAO implements BlogEntryDAO {

    List<BlogEntry> entries;
    Set<Tag> tags;

    public SimpleBlogEntryDAO() {
        entries = new ArrayList<>();
        tags = new TreeSet<>();
    }

    @Override
    public BlogEntry getEntryBySlug(String slug) {
        return entries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<BlogEntry> getEntriesByTag(Tag tag) {
        List<BlogEntry> tagEntries = new ArrayList<>();
        entries.stream()
                .filter(entry -> entry.getTags().contains(tag))
                .forEach(tagEntries::add);
        return tagEntries;
    }

    @Override
    public List<BlogEntry> findAll() {
        return new ArrayList<>(entries);
    }

    @Override
    public boolean addEntry(BlogEntry entry) {
        return entries.add(entry);
    }

    @Override
    public boolean removeEntry(BlogEntry entry) {
        return entries.remove(entry);
    }

    @Override
    public Tag getTagBySlug(String slug) {
        return tags.stream()
                .filter(tag -> tag.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Set<Tag> getTags() {
        return new TreeSet<>(tags);
    }

    @Override
    public boolean addTag(Tag tag) {
        return tags.add(tag);
    }
}
