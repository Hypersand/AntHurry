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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/coin")
@Tag(name = "CoinController", description = "코인에 대한 컨트롤러")
public class CoinController {
    private final MemberService memberService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final CoinService coinService;

    @Value("${custom.toss-payments.secretKey}")
    private String SECRET_KEY;

    @Operation(summary = "코인 충전", description = "코인 충전 페이지 입니다.")
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

    @Operation(summary = "코인 충전 성공", description = "코인 충전 성공시 코인을 받습니다.")
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

    @Operation(summary = "코인 충전 실패", description = "코인 충전 실패시 실패 페이지로 이동합니다.")
    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "coin/fail";
    }

    @Operation(summary = "환전 신청 조회", description = "보유한 코인을 환전할 수 있는 페이지입니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/exchange")
    public String exchangePoint(Model model){
        Member member = memberService.getMember();
        List<Exchange> exchangeList = coinService.getExchangeList(member);
        model.addAttribute("member", member);
        model.addAttribute("exchangeList", exchangeList);
        model.addAttribute("bankTypes", BankType.values());
        return "coin/exchange";
    }

    @Operation(summary = "환전 신청", description = "코인 환전을 신청합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/exchange")
    public String progressExchange(Model model, ExchangeRequest exchangeRequest) {
        RsData canExchange = memberService.canExchange(exchangeRequest.getMoney());
        model.addAttribute("bankTypes", BankType.values());
        if(canExchange.isFail()){
            return rq.historyBack(canExchange);
        }
        RsData rsData = coinService.applyExchange(exchangeRequest);
        return rq.redirectWithMsg("/coin/exchange", rsData.getMsg());
    }

    @Operation(summary = "환전 신청 수정", description = "신청한 환전신청을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/exchange/{exchangeId}")
    public String editApplyExchange(Model model, ExchangeRequest exchangeRequest, @PathVariable Long exchangeId) {
        RsData canExchange = memberService.canExchange(exchangeRequest.getMoney());
        model.addAttribute("bankTypes", BankType.values());
        if(canExchange.isFail()){
            return rq.historyBack(canExchange);
        }
        RsData rsData = coinService.modifyApplyExchange(exchangeRequest, exchangeId);
        if(rsData.isFail()){
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/coin/exchange", rsData.getMsg());
    }

    @Operation(summary = "환전 신청 취소", description = "환전신청을 취소합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/exchange/{exchangeId}")
    public String cancelApplyExchange(Model model, @PathVariable Long exchangeId) {
        RsData canCancelExchange = coinService.canCancelExchange(exchangeId);
        model.addAttribute("bankTypes", BankType.values());
        if(canCancelExchange.isFail()){
            return rq.historyBack(canCancelExchange);
        }
        RsData rsData = coinService.cancelExchange((Exchange) canCancelExchange.getData());
        return rq.redirectWithMsg("/coin/exchange", rsData.getMsg());
    }

    @Operation(summary = "환전 내역 삭제", description = "신청했던 환전신청들을 삭제할 수 있습니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/exchange/info/{exchangeId}")
    public String deleteApplyExchange(Model model, @PathVariable Long exchangeId) {
        RsData canCancelExchange = coinService.canCancelExchange(exchangeId);
        model.addAttribute("bankTypes", BankType.values());
        if(canCancelExchange.isFail()){
            return rq.historyBack(canCancelExchange);
        }
        RsData rsData = coinService.deleteExchangeInfo((Exchange) canCancelExchange.getData());
        return rq.redirectWithMsg("/coin/exchange", rsData.getMsg());
    }
}
