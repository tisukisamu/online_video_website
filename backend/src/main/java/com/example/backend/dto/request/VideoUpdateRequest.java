package com.example.backend.dto.request;

import jakarta.validation.constraints.Size;

public class VideoUpdateRequest {

    @Size(max = 100, message = "视频标题不能超过100个字符")
    private String title;

    @Size(max = 500, message = "视频描述不能超过500个字符")
    private String description;

    private String coverUrl;

    private String visibility;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
