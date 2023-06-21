package com.ant.hurry.base.api.service;

import com.ant.hurry.base.api.dto.RegCodeListDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

class RegionSearchServiceTest {

    @Test
    public void testGetRegCodeListDTO() {
        WebClient.Builder webClientBuilder = WebClient.builder().baseUrl("https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app");
        RegionSearchService regionSearchService = new RegionSearchService(webClientBuilder);

        // 호출 및 결과 확인
        String regcodePattern = "*00000000";
        Mono<RegCodeListDTO> result = regionSearchService.getRegCodeListDTO(regcodePattern);
        RegCodeListDTO regCodeListDTO = result.block();
        assertNotNull(regCodeListDTO);

        // regCodeListDTO.getRegCodeDTOList().get(0).getName() == "서울특별시" 확인
        assertThat(regCodeListDTO.getRegCodeDTOList().get(0).getName()).isEqualTo("서울특별시");
    }
}
