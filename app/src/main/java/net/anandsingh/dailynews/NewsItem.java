package net.anandsingh.dailynews;

/**
 * Created by anand on 29-12-2015.
 */
public class NewsItem {
    public String content;

    public String getContent() {
        return content;
    }

    public String details;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public String description;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String date;

    public NewsItem() {
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}