package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Restaurant;
import com.example.demo.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 메인 페이지
    @GetMapping
    public String index(Model model) {
        model.addAttribute("categories", Category.values());
        return "index";
    }

    // 전체 목록 / 카테고리 필터
    @GetMapping("/list")
    public String list(@RequestParam(required = false) Category category, Model model) {
        List<Restaurant> restaurants = (category != null)
                ? restaurantService.getByCategory(category)
                : restaurantService.getAllRestaurants();

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("categories", Category.values());
        model.addAttribute("selectedCategory", category);
        return "list";
    }

    // 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("restaurant", restaurantService.getById(id));
        model.addAttribute("isRandom", false);
        return "detail";
    }

    // 랜덤 추천
    @GetMapping("/random")
    public String random(Model model) {
        model.addAttribute("restaurant", restaurantService.getRandomRestaurant());
        model.addAttribute("isRandom", true);
        return "detail";
    }
}
