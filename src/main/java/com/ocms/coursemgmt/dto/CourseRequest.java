package com.ocms.coursemgmt.dto;

import java.util.Set;

public class CourseRequest {
    private String title;
    private String description;
    private String schedule;
    private Set<Long> instructorIds;


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public Set<Long> getInstructorIds() { return instructorIds; }
    public void setInstructorIds(Set<Long> instructorIds) { this.instructorIds = instructorIds; }
}