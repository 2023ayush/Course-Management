package com.ocms.coursemgmt.dto;

public class EmailMessage {
    private String to;
    private String subject;
    private String body;
    private String name;



    public EmailMessage() {}

    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getName() {return name;}

    public void setName(String name) { this.name = name;}

    @Override
    public String toString() {
        return "EmailMessage{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}