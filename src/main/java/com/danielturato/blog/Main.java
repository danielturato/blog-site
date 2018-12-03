package com.danielturato.blog;

import com.danielturato.blog.model.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;

import static spark.Spark.*;

public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        staticFileLocation("/public");

        BlogEntryDAO dao = new SimpleBlogEntryDAO();

        /*
            All before requests
         */

        before((req, res) -> {
           if (req.cookie("username") != null) {
               req.attribute("username", req.cookie("username"));
           }
        });

        before("/add", (req, res) -> {
            notAdmin(req, res);
        });

        before("/:slug/edit", (req, res) -> {
            notAdmin(req, res);
        });

        /*
            All tag requests
         */

        get("/tags", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Set<Tag> tags = dao.getTags();
            model.put("tags", tags);
            return new ModelAndView(model, "tags.hbs");
        }, new HandlebarsTemplateEngine());

        get("/tags/:slug", (req, res) -> {
           Map<String, Object> model = new HashMap<>();
           Tag tag = dao.getTagBySlug(req.params("slug"));
           List<BlogEntry> allEntries = dao.getEntriesByTag(tag);
           model.put("entries", allEntries);
           return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        /*
            All sign-in requests
         */

        get("/sign-in", (req, res) -> {
           Map<String, Object> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "sign-in.hbs");
        }, new HandlebarsTemplateEngine());

        post("/sign-in", (req, res) -> {
            res.cookie("username", req.queryParams("username"));
            res.redirect("/");
            return null;
        });

        /*
            All add requests
         */

        get("/add", (req, res) -> {
            return new ModelAndView(null, "add.hbs");
        }, new HandlebarsTemplateEngine());

        post("/add", (req, res) -> {
            String tags = (String) req.queryParams("tags");
            if (tags.isEmpty()) {
                tags = null;
            }
            BlogEntry entry = new BlogEntry(req.queryParams("title"), req.queryParams("entry"), tags);
            dao.addEntry(entry);
            entry.getTags().stream()
                    .forEach(dao::addTag);
            setFlashMessage(req, "The blog post has been posted!");
            res.redirect("/");
            return null;
        });

        /*
            Main index page request
         */

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAll());
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        /*
            Requests on the individual blog posts
         */

        get("/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.getEntryBySlug(req.params("slug")));
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug", (req, res) -> {
            BlogEntry entry = dao.getEntryBySlug(req.params("slug"));
            entry.addComment(new BlogComment(req.queryParams("name"), req.queryParams("comment")));
            res.redirect(String.format("/%s", entry.getSlug()));
            return null;
        });

        /*
            Requests on editing a blog post
         */

        get("/:slug/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.getEntryBySlug(req.params("slug")));
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/edit", (req, res) -> {
            BlogEntry entry = dao.getEntryBySlug(req.params("slug"));
            dao.getEntryBySlug(req.params("slug")).setText(req.queryParams("entry"));
            dao.getEntryBySlug(req.params("slug")).setTitle(req.queryParams("title"));
            setFlashMessage(req, "Your changes have been implemented");
            res.redirect(String.format("/%s", entry.getSlug()));
            return null;
        });

        /*
            Requests on deleting a blog post
         */

        get("/:slug/delete", (req, res) -> {
           Map<String, Object> model = new HashMap<>();
           model.put("entry", dao.getEntryBySlug(req.params("slug")));
           return new ModelAndView(model, "delete.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/delete", (req, res) -> {
            BlogEntry entry = dao.getEntryBySlug(req.params("slug"));
            dao.removeEntry(entry);
            res.redirect("/");
            setFlashMessage(req, "The blog post has been deleted");
            return null;
        });

    }

    private static void notAdmin(Request req, Response res) {
        if(req.attribute("username") == null) {
            setFlashMessage(req, "You need to sign in as an admin to do this.");
            res.redirect("/sign-in");
            halt();
        } else if (!((String) (req.attribute("username"))).equalsIgnoreCase("admin")) {
            setFlashMessage(req, "You need to be an admin to access this.");
            res.redirect("/");
            halt();
        }
    }

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }



}
