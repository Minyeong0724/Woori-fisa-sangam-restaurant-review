package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // 폼 데이터를 객체에 바인딩하고, ID를 자동 할당하기 위해 필요
@NoArgsConstructor // 스프링이 빈 객체를 먼저 생성할 때 필요
@AllArgsConstructor
public class Restaurant {
    private Long id;
    private String name;
    private Category category;
    private String address;
    private String menu;
    private int avgPrice;
    private String description;
    private boolean soloFriendly;
}