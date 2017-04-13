package com.vozer.model;

/**
 * Created by vqnguyen on 4/10/2017.
 */
public class Post {
    private String story;
    private String tags;
    private String pic;
    private String mediafile;
    private String url;

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getMediafile() {
        return mediafile;
    }

    public void setMediafile(String mediafile) {
        this.mediafile = mediafile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
