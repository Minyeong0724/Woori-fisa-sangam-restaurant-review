package com.example.demo.model;

import lombok.Getter;

@Getter
public enum Category {
    KOREAN("한식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    CHINESE("중식");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

}
