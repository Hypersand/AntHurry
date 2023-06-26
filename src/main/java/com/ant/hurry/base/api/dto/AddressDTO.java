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
public class AddressDTO {

    private double x;
    private double y;
    @JsonProperty("region_2depth_name")
    private String depth2;
    @JsonProperty("region_3depth_name")
    private String depth3;
}
