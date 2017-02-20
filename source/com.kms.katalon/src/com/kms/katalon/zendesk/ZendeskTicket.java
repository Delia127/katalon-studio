package com.kms.katalon.zendesk;

public class ZendeskTicket {
    private long id;

    private String subject;

    private String description;

    private ZendeskTicketComment comment;

    private ZendeskCollaborator requester;
    
    public ZendeskTicket() {
    }
    
    public ZendeskTicket(String subject) {
        this.subject = subject;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZendeskTicketComment getComment() {
        return comment;
    }

    public void setComment(ZendeskTicketComment comment) {
        this.comment = comment;
    }

    public ZendeskCollaborator getRequester() {
        return requester;
    }

    public void setRequester(ZendeskCollaborator requester) {
        this.requester = requester;
    }

    public static class ZendeskTicketComment {
        private String body;

        private String[] uploads;

        public ZendeskTicketComment() {
        }

        public ZendeskTicketComment(String body) {
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String[] getUploads() {
            return uploads;
        }

        public void setUploads(String[] uploads) {
            this.uploads = uploads;
        }
    }

    public static class ZendeskCollaborator {

        private String name;

        private String email;

        public ZendeskCollaborator() {
        }

        public ZendeskCollaborator(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
