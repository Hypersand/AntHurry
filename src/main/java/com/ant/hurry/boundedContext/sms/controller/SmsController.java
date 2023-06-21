package com.ant.hurry.boundedContext.sms.controller;

import com.ant.hurry.boundedContext.sms.service.SmsService;
import com.ant.hurry.boundedContext.sms.web.SendRequest;
import com.ant.hurry.boundedContext.sms.web.SmsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;



@RestController
@RequiredArgsConstructor
@Slf4j
public class SmsController {

    private final SmsService smsService;


    //SMS 확인 임시 메서드
    @PostMapping("/user/sms")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SmsResponse> test(@RequestBody SendRequest request) throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {
        log.info("request phone = {}", request.getRecipientPhoneNumber());
        log.info("request message = {}", request.getContent());
        SmsResponse data = smsService.sendSms(request.getRecipientPhoneNumber(), request.getContent());
        return ResponseEntity.ok().body(data);
    }
}