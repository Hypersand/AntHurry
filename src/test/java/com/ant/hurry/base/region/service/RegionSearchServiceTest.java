package com.ant.hurry.base.region.service;

import com.ant.hurry.base.region.dto.RegCodeListDTO;
import com.ant.hurry.base.region.entity.Region;
import com.ant.hurry.base.region.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RegionSearchServiceTest {

    @Autowired
    private RegionSearchService regionSearchService;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void resetRegionData() {
        regionRepository.deleteAll();
    }


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
    @DisplayName("saveRegionData 메서드 테스트")
    public void t2() throws Exception {
        //11*로 API 호출하여 데이터 가져왔을 때
        String regCodePattern = "11*";
        regionSearchService.saveRegionData(regCodePattern);

        // 서울특별시 종로구 신교동의 지역코드를 통해 데이터가 들어갔는지 확인
        Region region = regionSearchService.findByCode("1111010200").orElseThrow();

        assertThat(region.getDepth1()).isEqualTo("서울특별시");
        assertThat(region.getDepth2()).isEqualTo("종로구");
        assertThat(region.getDepth3()).isEqualTo("신교동");

    }

    @Test
    @DisplayName("selectPattern 메서드 테스트")
    public void t3() throws Exception {
        // selectPattern 메서드 호출하면 모든 정보가 저장된다
        regionSearchService.selectPattern();

        // 서울특별시 종로구 효자동의 지역코드를 통해 데이터가 들어갔는지 확인
        Region region = regionSearchService.findByCode("1111010400").orElseThrow();

        assertThat(region.getDepth1()).isEqualTo("서울특별시");
        assertThat(region.getDepth2()).isEqualTo("종로구");
        assertThat(region.getDepth3()).isEqualTo("효자동");

    }

    @Test
    @DisplayName("지역선택을 통해 해당 코드를 넘겨주는 링크를 클릭해 해당 지역의 게시판으로 이동")
    @WithMockUser("test")
    void enterRegionBoard() throws Exception {
        regionSearchService.selectPattern();
        String regionCode = "1168010100"; //서울특별시 강남구 역삼동

        mvc.perform(MockMvcRequestBuilders.get("/board/enterRegion")
                        .param("code", regionCode))
                .andExpect(status().isOk())
                .andExpect(view().name("board/enterRegion"))
                .andExpect(content().string(containsString("서울특별시 강남구 역삼동")));
    }


}
