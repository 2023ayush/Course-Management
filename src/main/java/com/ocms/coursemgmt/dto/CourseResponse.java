package com.ocms.coursemgmt.dto;
import java.util.Set;

public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String schedule;
    private Set<InstructorDto> instructors;

    public CourseResponse() {}

    public CourseResponse(Long id, String title, String description, String schedule, Set<InstructorDto> instructors) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.schedule = schedule;
        this.instructors = instructors;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public Set<InstructorDto> getInstructors() { return instructors; }
    public void setInstructors(Set<InstructorDto> instructors) { this.instructors = instructors; }
}
