package com.danielturato.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.util.Objects;

public class Tag implements Comparable {

    String name;
    String slug;

    public Tag(String name) {
        this.name = name;
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public String toString() {
        return String.format("<%s>", this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) &&
                Objects.equals(slug, tag.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, slug);
    }


    @Override
    public int compareTo(Object o) {
        if (this.name.equals(((Tag)o).name)) {
            return 0;
        } else {
            return 1;
        }

    }
}
