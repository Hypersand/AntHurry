package com.ant.hurry.boundedContext.member.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class PhoneAuthService {


    @Value("${naver-cloud.serviceId}")
    private String SERVICE_ID;

    @Value("${naver-cloud.accessKey}")
    private String ACCESS_KEY;

    @Value("${naver-cloud.secretKey}")
    private String SECRET_KEY;

    @Value("${naver-cloud.senderPhone}")
    private String SENDER_NUMBER;

    private final StringRedisTemplate redisTemplate;


    public String sendSms(String toNumber) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        RestTemplate restTemplate = new RestTemplate();

        //헤더 정보 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", String.valueOf(System.currentTimeMillis()));
        headers.set("x-ncp-iam-access-key", ACCESS_KEY);
        headers.set("x-ncp-apigw-signature-v2", makeSignature());

        //내용 세팅
        Map<String, Object> body = new HashMap<>();
        body.put("type", "SMS");
        body.put("contentType", "COMM");
        body.put("countryCode", "82");
        body.put("from", SENDER_NUMBER);

        //인증번호 생성
        String certificationNumber = createSendMessage();
        body.put("content", certificationNumber);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> msg = new HashMap<>();
        msg.put("to", toNumber);
        messages.add(msg);

        body.put("messages", messages);

        //SMS 문자 요청 보내기
        String API_URL = "https://sens.apigw.ntruss.com/sms/v2/services/" + SERVICE_ID + "/messages";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(API_URL, request, String.class);

        //redis 에 인증번호 저장
        setAuthCode(toNumber, certificationNumber);

        return certificationNumber;
    }

    public String makeSignature() throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String space = " "; // one space
        String newLine = "\n"; // new line

        String method = "POST"; // method
        String url = "/sms/v2/services/"+SERVICE_ID+"/messages"; // url (include query string)
        String timestamp = String.valueOf(System.currentTimeMillis()); // current timestamp (epoch)
        String accessKey = ACCESS_KEY; // access key id (from portal or Sub Account)
        String secretKey = SECRET_KEY;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public String createSendMessage() {
        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        return numStr;
    }

    public void setAuthCode(String phoneNumber, String authCode) {
        redisTemplate.opsForValue().set(phoneNumber, authCode, 3, TimeUnit.MINUTES);
    }

    public String getAuthCode(String phoneNumber) {
        return redisTemplate.opsForValue().get(phoneNumber);
    }
}
