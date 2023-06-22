package com.ant.hurry.base.region.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String depth1; //ex 서울특별시
    private String depth2; //ex 강남구
    private String depth3; //ex 역삼동
}
