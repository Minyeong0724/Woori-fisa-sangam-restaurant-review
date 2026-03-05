# Woori-fisa-sangam-restaurant-review
Spring MVC Flow Review (Sangam Food)

---

## 기술 스택

- Java 17
- Spring Boot 4.0.3
- Spring Web MVC
- Thymeleaf (SSR)
- Lombok
- Gradle

---

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── model/
│   ├── Category.java              # enum - 음식 카테고리 (KOREAN / JAPANESE / WESTERN / CHINESE)
│   └── Restaurant.java            # DTO - 맛집 정보
├── repository/
│   └── RestaurantRepository.java  # DAO - 더미 데이터 6개 보관 및 조회
├── service/
│   └── RestaurantService.java     # 비즈니스 로직 (랜덤 추출, 카테고리 필터)
└── controller/
    └── RestaurantController.java  # URL 매핑 및 뷰 반환

src/main/resources/
├── templates/
│   ├── index.html    # 메인 페이지
│   ├── list.html     # 목록 페이지
│   └── detail.html   # 상세 / 랜덤 결과 페이지
└── application.properties
```

---

## 3-Tier 계층 구성

```
Controller  →  요청 수신, Model에 데이터 적재, 뷰 이름 반환
Service     →  랜덤 추출 / 카테고리 필터 등 비즈니스 로직 처리
Repository  →  DB 대신 List<Restaurant>으로 데이터 보관 및 조회
```

---

## DispatcherServlet 흐름

`GET /restaurant/random` 호출 기준

```
1. DispatcherServlet → 요청 수신
2. HandlerMapping    → @GetMapping("/random") 탐색, RestaurantController.random() 선택
3. HandlerAdapter    → 컨트롤러 메서드 실행
4. Service → Repository → Random 추출
5. Controller        → model.addAttribute("restaurant", ...) 로 Model에 데이터 적재
6. DispatcherServlet → 반환값 "detail" 수신
7. ViewResolver      → "detail" → /templates/detail.html 매핑
8. Thymeleaf         → th:text="${restaurant.name}" 등 HTML 렌더링
9. Response          → 완성된 HTML → 브라우저
```

---

## 기능 목록

### 1. 메인 페이지 (`GET /restaurant`)
- 카테고리 선택 버튼(한식 / 일식 / 양식 / 중식) 노출
- "오늘 뭐 먹지?" 랜덤 추천 버튼 제공
```java
@GetMapping
public String index(Model model) {
    model.addAttribute("categories", Category.values());
    return "index";
}
```

### 2. 전체 목록 조회 (`GET /restaurant/list`)
- 등록된 맛집 전체를 카드 형태로 출력
- 이름, 카테고리, 혼밥 여부, 한줄평, 평균 가격 표시
```java
@GetMapping("/list")
public String list(@RequestParam(required = false) Category category, Model model) {
    List<Restaurant> restaurants = (category != null)
            ? restaurantService.getByCategory(category)
            : restaurantService.getAllRestaurants();
    model.addAttribute("restaurants", restaurants);
    return "list";
}
```

### 3. 카테고리 필터 (`GET /restaurant/list?category={CATEGORY}`)
- 선택한 카테고리에 해당하는 맛집만 필터링하여 출력
- 필터 탭 UI 활성화 상태 연동
```java
// RestaurantRepository.java
public List<Restaurant> findByCategory(Category category) {
    return restaurants.stream()
            .filter(r -> r.getCategory() == category)
            .collect(Collectors.toList());
}
```

### 4. 상세 페이지 (`GET /restaurant/{id}`)
- 특정 맛집의 상세 정보 출력
- 메뉴, 주소, 한줄평, 평균 가격, 혼밥 가능 여부 표시
```java
@GetMapping("/{id}")
public String detail(@PathVariable Long id, Model model) {
    model.addAttribute("restaurant", restaurantService.getById(id));
    return "detail";
}
```

### 5. 랜덤 추천 (`GET /restaurant/random`)
- 전체 맛집 중 1개를 랜덤으로 선택하여 상세 페이지로 이동
- "다시 뽑기" 버튼으로 재추천 가능
```java
@GetMapping("/random")
public String random(Model model) {
    model.addAttribute("restaurant", restaurantService.getRandomRestaurant());
    return "detail";
}
```

### 6. 맛집 등록 (추후 추가)

---

## 실행 방법

```bash
./gradlew bootRun
```

브라우저에서 `http://localhost:8080/restaurant` 접속

> MySQL 드라이버가 classpath에 포함되어 있으나 DB 연결 없이 실행합니다.
> `application.properties`에서 DataSource 자동설정을 제외 처리했습니다.
