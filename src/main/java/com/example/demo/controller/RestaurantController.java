package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Restaurant;
import com.example.demo.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("restaurant", new Restaurant()); // 빈 객체를 넘겨서 폼 세팅
        return "addForm";
    }

    // 2. 맛집 등록 데이터 처리하기 (POST)
    @PostMapping
    public String addRestaurant(@ModelAttribute Restaurant restaurant) {
        restaurantService.save(restaurant);
        // 처리 완료 후, 뷰(html)를 렌더링하지 않고 목록 URL로 리다이렉트 (PRG 패턴)
        return "redirect:/restaurant/list";
    }
}
