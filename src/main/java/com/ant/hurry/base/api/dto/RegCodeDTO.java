package com.ant.hurry.base.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegCodeDTO {
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
}
