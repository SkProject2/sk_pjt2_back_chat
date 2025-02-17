package com.example.sk_pjt2_back_chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class InquiryDto {
    private String subject;
    private String content;

    @Builder
    public InquiryDto(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
