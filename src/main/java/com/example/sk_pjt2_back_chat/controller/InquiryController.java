package com.example.sk_pjt2_back_chat.controller;

import com.example.sk_pjt2_back_chat.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inquiry")
public class InquiryController {
    @Autowired
    private InquiryService inquiryService;

    @PostMapping("/upload")
    public String upload(@RequestBody String inquiryDto) {
        System.out.println("문의 전송중...");
        try{
            inquiryService.inquiry(inquiryDto);
            System.out.println("문의 전송 완료");
        }catch (Exception e){
            System.out.println("전송중 문제 발생");
        }


        return "success";
    }
}
