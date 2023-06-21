package com.ant.hurry.base.region.service;

import com.ant.hurry.base.region.dto.RegCodeDTO;
import com.ant.hurry.base.region.dto.RegCodeListDTO;
import com.ant.hurry.base.region.entity.Region;
import com.ant.hurry.base.region.repository.RegionRepository;
import com.ant.hurry.base.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RegionSearchService {

    private final WebClient webClient;
    private final RegionRepository regionRepository;
    private final Rq rq;


    /**
     * 외부 API를 호출하여 지역 코드 목록을 얻는 메서드
     * @param regCodePattern 검색시 사용할 코드 패턴
     * @return Mono<RegCodeListDTO> : 지역코드 목록을 반환
     */
    public Mono<RegCodeListDTO> getRegCodeListDTO(String regCodePattern) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/regcodes")
                        .queryParam("regcode_pattern", regCodePattern)
                        .build())
                .retrieve()
                .bodyToMono(RegCodeListDTO.class);
    }

    /**
     * 지역 코드 데이터를 저장하기 위한 메서드
     **/
    @Transactional
    public void saveRegionData(String regCodePattern) {

        Mono<RegCodeListDTO> result = getRegCodeListDTO(regCodePattern);
        RegCodeListDTO regCodeListDTO = result.block();

        if (regCodeListDTO.getRegCodeDTOList().isEmpty()) {
            rq.redirectWithMsg("/", "데이터 읽어오지 못함");
        }

        for (RegCodeDTO regCode : regCodeListDTO.getRegCodeDTOList()) {
            String[] depthTokens = regCode.getName().split(" ");

            // 3개의 토큰이 있는 경우만 저장
            if (depthTokens.length == 3) {
                Region region = new Region();
                region.setCode(regCode.getCode());
                region.setOneDepth(depthTokens[0]);
                region.setTwoDepth(depthTokens[1]);
                region.setThreeDepth(depthTokens[2]);

                regionRepository.save(region);
            }
        }
    }

    public Optional<Region> findByCode(String code) {
        return regionRepository.findByCode(code);
    }


}
