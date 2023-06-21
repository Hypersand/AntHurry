package com.ant.hurry.base.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegCodeListDTO {
    @JsonProperty("regcodes")
    private List<RegCodeDTO> regCodeDTOList;
}
