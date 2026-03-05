package com.example.demo.repository;

import com.example.demo.model.Category;
import com.example.demo.model.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RestaurantRepository {

    private final List<Restaurant> restaurants;

    public RestaurantRepository() {
        restaurants = new ArrayList<>(List.of(
            new Restaurant(1L, "무라", Category.JAPANESE, "서울 마포구 상암동 481", "돈가스, 라멘, 카레", 12000, "상암동 대표 일식 맛집. 점심 시간에 줄 서는 집", true),
            new Restaurant(2L, "저스트텐동", Category.JAPANESE, "서울 마포구 상암동 482", "새우텐동, 야채텐동", 13000, "바삭한 튀김이 올라간 인기 텐동 전문점", true),
            new Restaurant(3L, "상암회관", Category.KOREAN, "서울 마포구 상암동 241", "갈비탕, 설렁탕, 수육", 10000, "담백하고 깔끔한 국물 한식. 단체 회식도 OK", false),
            new Restaurant(4L, "진진", Category.CHINESE, "서울 마포구 상암동 332", "짜장면, 짬뽕, 탕수육", 9000, "정통 중화요리 전문점. 빠른 배달도 가능", true),
            new Restaurant(5L, "맥도날드 상암DMC점", Category.WESTERN, "서울 마포구 상암동 515", "빅맥, 맥스파이시, 맥너겟", 8000, "빠른 점심이 필요할 때. 혼밥 OK", true),
            new Restaurant(6L, "명동칼국수 상암점", Category.KOREAN, "서울 마포구 상암동 127", "칼국수, 비빔밥, 만두", 9000, "든든한 한 끼. 팀 점심으로 인기", false)
        ));
    }

    public List<Restaurant> findAll() {
        return restaurants;
    }

    public Optional<Restaurant> findById(Long id) {
        return restaurants.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Restaurant> findByCategory(Category category) {
        return restaurants.stream()
                .filter(r -> r.getCategory() == category)
                .collect(Collectors.toList());
    }
}
