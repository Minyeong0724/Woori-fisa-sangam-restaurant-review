# Woori-fisa-sangam-restaurant-review

상암동의 맛집 리스트를 볼 수 있는 Spring MVC 실습 프로젝트입니다. 

---

## 기술 스택

- Java 17
- Spring Boot 4.0.3
- Spring Web MVC
- Thymeleaf (SSR)
- Lombok
- Gradle

---

## 실행 방법

```bash
./gradlew bootRun
```

브라우저에서 `http://localhost:8080/restaurant` 접속

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
│   ├── addForm.html     # 등록 페이지
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

`GET /restaurant/new` 등록 폼 호출 기준
```
1. DispatcherServlet → 요청 수신
2. HandlerMapping    → @GetMapping("/new") 탐색, RestaurantController.addForm() 선택
3. HandlerAdapter    → 컨트롤러 메서드 실행
4. Controller        → model.addAttribute로 카테고리 목록과 빈 Restaurant 객체 적재
5. DispatcherServlet → 반환값 "addForm" 수신
6. ViewResolver      → "addForm" → /templates/addForm.html 매핑
7. Thymeleaf         → th:object, th:field 등을 활용해 입력 폼 HTML 렌더링
8. Response          → 완성된 등록 폼 HTML → 브라우저
```

`POST /restaurant` 맛집 등록 및 리다이렉트 기준
```
1. DispatcherServlet → POST 요청 및 폼 데이터 수신
2. HandlerMapping    → @PostMapping 탐색, RestaurantController.addRestaurant() 선택
3. HandlerAdapter    → Data Binding 수행 (폼 데이터를 Restaurant 객체로 변환) 후 메서드 실행
4. Service → Repository → ArrayList에 새로운 맛집 객체 저장 (In-memory)
5. Controller        → 반환값 "redirect:/restaurant/list" 수신
6. DispatcherServlet → "redirect:" 접두어를 확인하고 리다이렉트 응답 결정
7. Response          → 브라우저에 HTTP 302 (Found) 상태 코드와 이동 경로 전달
8. Browser           → 전달받은 경로(/restaurant/list)로 새로운 GET 요청 발송
9. DispatcherServlet → 새로운 GET 요청을 수신하여 목록 페이지 흐름 시작 (PRG 패턴 완성)
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

### 6. 맛집 등록 (`GET /restaurant/new`, `POST /restaurant`)
- 새로운 맛집 정보를 입력받아 인메모리 리스트에 저장합니다.
- 상호명, 카테고리, 주소, 메뉴, 가격, 한줄평, 혼밥 여부를 등록할 수 있습니다.
- PRG(Post-Redirect-Get) 패턴을 적용하여 새로고침 시 데이터 중복 등록을 방지합니다.
```java
    // 1. 맛집 등록 폼 페이지 (GET)
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
```
---



