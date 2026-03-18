package com.ocms.coursemgmt.dto;

import jakarta.mail.Multipart;
import org.springframework.web.multipart.MultipartFile;

public class SubmissionRequest {

    private MultipartFile file;
    private Double grade;
    private String feedback;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
