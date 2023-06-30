package com.ant.hurry.boundedContext.coin.controller;

import com.ant.hurry.base.rq.Rq;
import com.ant.hurry.base.rsData.RsData;
import com.ant.hurry.boundedContext.coin.dto.ExchangeRequest;
import com.ant.hurry.boundedContext.coin.entity.BankType;
import com.ant.hurry.boundedContext.coin.entity.CoinChargeLog;
import com.ant.hurry.boundedContext.coin.entity.Exchange;
import com.ant.hurry.boundedContext.coin.service.CoinService;
import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.member.service.MemberService;
import com.ant.hurry.standard.util.Ut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coin")
public class CoinController {
    private final MemberService memberService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final CoinService coinService;

    @Value("${custom.toss-payments.secretKey}")
    private String SECRET_KEY;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/charge")
    public String chargePoint(Model model){
        Member member = memberService.getMember();
        model.addAttribute("member", member);
        return "coin/charge";
    }

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }


    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable Long id,
            @RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Long amount,
            Model model) throws Exception {

        RsData rsData = memberService.checkCanCharge(id, orderId);

        if(rsData.isFail()){
            return rq.redirectWithMsg("/coin/charge", rsData);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode successNode = responseEntity.getBody();
            model.addAttribute("orderId", successNode.get("orderId").asText());
            memberService.addCoin(memberService.getMember(), amount, "코인충전");
            return "redirect:/usr/member/profile?msg=%s".formatted(Ut.url.encode(rsData.getMsg()));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "coin/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "coin/fail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/exchange")
    public String exchangePoint(Model model){
        Member member = memberService.getMember();
        List<Exchange> exchangeLists = coinService.getExchangeList(member);
        model.addAttribute("member", member);
        model.addAttribute("exchangeLists", exchangeLists);
        model.addAttribute("bankTypes", BankType.values());
        return "coin/exchange";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/exchange")
    public String progressExchange(Model model, ExchangeRequest exchangeRequest) {
        RsData canExchange = memberService.canExchange(exchangeRequest.getMoney());
        model.addAttribute("bankTypes", BankType.values());
        if(canExchange.isFail()){
            return rq.historyBack(canExchange);
        }
        coinService.applyExchange(exchangeRequest);
        return rq.redirectWithMsg("/usr/member/profile", "성공");
    }
}
