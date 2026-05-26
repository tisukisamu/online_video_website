package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MessageCreateRequest {

    @NotNull(message = "接收用户不能为空")
    private Long receiverId;

    @NotBlank(message = "私聊内容不能为空")
    @Size(max = 2000, message = "私聊内容不能超过2000个字符")
    private String content;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
