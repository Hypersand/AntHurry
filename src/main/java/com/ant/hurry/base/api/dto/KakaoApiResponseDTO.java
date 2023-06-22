package com.ant.hurry.base.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoApiResponseDTO {

    @JsonProperty("documents")
    private List<DocumentDTO> documentDTOList;

}
