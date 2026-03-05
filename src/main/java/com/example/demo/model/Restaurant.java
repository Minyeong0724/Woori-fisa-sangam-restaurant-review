package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
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
