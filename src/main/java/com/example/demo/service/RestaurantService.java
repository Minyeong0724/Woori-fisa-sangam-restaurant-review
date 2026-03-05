package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.Restaurant;
import com.example.demo.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("맛집을 찾을 수 없습니다. id=" + id));
    }

    public Restaurant getRandomRestaurant() {
        List<Restaurant> all = restaurantRepository.findAll();
        return all.get(new Random().nextInt(all.size()));
    }

    public List<Restaurant> getByCategory(Category category) {
        return restaurantRepository.findByCategory(category);
    }

    public void save(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }
}
