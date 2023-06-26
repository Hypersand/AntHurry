package com.ant.hurry.base.api.service;

import com.ant.hurry.base.api.dto.KakaoApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    private final KakaoUriBuilderService kakaoUriBuilderService;

    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String kakaoRestApiKey;

    private final WebClient webClient;

    public Mono<KakaoApiResponseDTO> requestAddressSearch(String address){
        if(ObjectUtils.isEmpty(address)) return null;

        URI uri = kakaoUriBuilderService.buildUriAddressSearch(address);

        // kakao api 호출
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey)
                .retrieve()
                .bodyToMono(KakaoApiResponseDTO.class);
    }

}
