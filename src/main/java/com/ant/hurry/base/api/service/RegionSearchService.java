package com.ant.hurry.base.api.service;

import com.ant.hurry.base.api.dto.RegCodeDTO;
import com.ant.hurry.base.api.dto.RegCodeListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class RegionSearchService {

    private final WebClient webClient;

    @Autowired
    public RegionSearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app").build();
    }

    public Mono<RegCodeListDTO> getRegCodeListDTO(String regcodePattern) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/regcodes")
                        .queryParam("regcode_pattern", regcodePattern)
                        .build())
                .retrieve()
                .bodyToMono(RegCodeListDTO.class);
    }
}
