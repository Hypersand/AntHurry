package com.ant.hurry.base.region.service;

import com.ant.hurry.base.region.dto.RegCodeListDTO;
import com.ant.hurry.base.region.entity.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RegionSearchServiceTest {

    @Autowired
    private RegionSearchService regionSearchService;


    @Test
    @DisplayName("지역코드 검색을 통해 서울특별시가 나오는지 확인")
    public void t1() throws Exception {
        // 호출하고 null이 아닌지 확인
        String regCodePattern = "*00000000";
        Mono<RegCodeListDTO> result = regionSearchService.getRegCodeListDTO(regCodePattern);
        RegCodeListDTO regCodeListDTO = result.block();
        assertNotNull(regCodeListDTO);

        // regCodeListDTO.getRegCodeDTOList().get(0).getName() == "서울특별시" 확인
        assertThat(regCodeListDTO.getRegCodeDTOList().get(0).getName()).isEqualTo("서울특별시");
    }

    @Test
    @DisplayName("regCodePattern을 11*로 했을 때 saveRegionData메서드를 통해 code가 1111010200인 것의 one,two,three Depth 확인")
    public void t2() throws Exception {
        String regCodePattern = "11*";
        regionSearchService.saveRegionData(regCodePattern);

        Region region = regionSearchService.findByCode("1111010200").orElseThrow();

        assertThat(region.getOneDepth()).isEqualTo("서울특별시");
        assertThat(region.getTwoDepth()).isEqualTo("종로구");
        assertThat(region.getThreeDepth()).isEqualTo("신교동");

    }


}
