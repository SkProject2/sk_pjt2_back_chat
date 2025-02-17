package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.dto.InquiryDto;
import com.example.sk_pjt2_back_chat.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inquiry")
public class InquiryController {
    @Autowired
    private InquiryService inquiryService;

    @PostMapping("/upload")
    public String upload(@RequestBody InquiryDto inquiryDto,
                         @RequestHeader("X-Auth-User") String user) {
        inquiryService.inquiry(inquiryDto, user);
        return "success";
    }
}
