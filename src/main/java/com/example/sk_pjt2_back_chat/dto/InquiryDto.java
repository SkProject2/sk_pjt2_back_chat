package com.example.sk_pjt2_back_chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class InquiryDto {
    private String name;
    private String email;
    private String phone;
    private String message;

    @Builder
    public InquiryDto(String name, String email, String phone, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
    }
}
