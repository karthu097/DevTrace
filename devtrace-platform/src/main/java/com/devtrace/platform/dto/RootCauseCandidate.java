package com.devtrace.platform.dto;

public class RootCauseCandidate {
    private String service;
    private String dependency;
    private String type;
    private String description;
    private int score;

    public RootCauseCandidate(String service, String dependency, String type, String description, int score) {
        this.service = service;
        this.dependency = dependency;
        this.type = type;
        this.description = description;
        this.score = score;
    }

    public String getService() { return service; }
    public String getDependency() { return dependency; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
