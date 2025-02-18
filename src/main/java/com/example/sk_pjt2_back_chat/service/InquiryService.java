package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.dto.InquiryDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class InquiryService {

    private final JavaMailSenderImpl mailSender;
    // 서머 메일
    @Value("${spring.mail.username}")
    private String from;

    // 관리자메일
    private String to = "lightning0145@naver.com";

    public InquiryService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void inquiry(String ip) throws JsonProcessingException {
        // 이메일 전송하기
        ObjectMapper objectMapper = new ObjectMapper();
        InquiryDto inquiryDto = objectMapper.readValue(ip, InquiryDto.class);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[문의]: "+inquiryDto.getName());
        message.setText(inquiryDto.getMessage() + "\n 문의자: " + inquiryDto.getEmail() + "/" + inquiryDto.getPhone());
        mailSender.send(message);
    }
}
