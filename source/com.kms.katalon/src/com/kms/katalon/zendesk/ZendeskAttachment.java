package com.kms.katalon.zendesk;

public class ZendeskAttachment {
    private long id;

    private String file_name;

    private String content_url;

    private String content_type;

    private int size;

    private ZendeskAttachment[] thumbnails;

    private boolean inline;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ZendeskAttachment[] getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ZendeskAttachment[] thumbnails) {
        this.thumbnails = thumbnails;
    }

    public boolean isInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }
}
