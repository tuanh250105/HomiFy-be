# 🔐 Hướng Dẫn Implementation Authentication - HomiFy Backend

## 📋 Mục Lục
1. [Hướng Dẫn Các Module Khác Sử Dụng Authentication](#hướng-dẫn-các-module-khác-sử-dụng-authentication)
2. [Lộ Trình Chi Tiết Chức Năng Authentication](#lộ-trình-chi-tiết-chức-năng-authentication)

---

## 🎯 PHẦN 1: Hướng Dẫn Các Module Khác Sử Dụng Authentication

### ⚠️ NGUYÊN TẮC BẢO MẬT QUAN TRỌNG

#### ❌ KHÔNG BAO GIỜ LÀM NHƯ NÀY:
```java
// ❌ NGUY HIỂM: User có thể fake userId!
@GetMapping("/profile")
public ResponseEntity<?> getProfile(@RequestParam Long userId) {
    User user = userService.findById(userId);
    return ResponseEntity.ok(user);
}
```

**Lý do:** User có thể thay đổi `userId` trong URL để xem/sửa/xóa dữ liệu của người khác!

---

### ✅ CÁCH LÀM ĐÚNG

#### Step 1: Tạo Helper Method lấy thông tin User hiện tại

```java
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.homifybackend.auth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/your-module")
public class YourController {

    // Helper method: Lấy userId hiện tại từ SecurityContext
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }
    
    // Helper method: Lấy toàn bộ thông tin User hiện tại
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }
    
    // Helper method: Check role
    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            return user.getRole().toString().equalsIgnoreCase(role);
        }
        return false;
    }
}
```

---

#### Step 2: Sử dụng trong Endpoint

```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }

    // ✅ SECURE: GET profile
    @GetMapping
    public ResponseEntity<?> getProfile() {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Not authenticated"));
        }
        
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }
    
    // ✅ SECURE: UPDATE profile
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto dto) {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Not authenticated"));
        }
        
        User updated = userService.updateProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }
}
```

---

### 📚 Thông Tin Có Sẵn trong CustomUserDetails

```java
CustomUserDetails userDetails = getCurrentUser();

// Có thể lấy các thông tin sau:
Long userId = userDetails.getUserId();                    // ID người dùng
String email = userDetails.getEmail();                    // Email
String username = userDetails.getUsername();              // Username
String fullName = userDetails.getFullName();              // Họ tên
String phoneNumber = userDetails.getPhoneNumber();        // Số điện thoại
String role = userDetails.getRole().toString();           // "CUSTOMER" hoặc "AGENT"
String avatarUrl = userDetails.getAvatarUrl();            // URL avatar
LocalDate dateOfBirth = userDetails.getDateOfBirth();     // Ngày sinh
Gender gender = userDetails.getGender();                  // Giới tính
LocalDateTime registrationDate = userDetails.getRegistrationDate(); // Ngày đăng ký
```

---

### 🎯 Ví Dụ Thực Tế

#### Ví Dụ 1: Saved Homes Controller
```java
@RestController
@RequestMapping("/api/saved-homes")
public class SavedHomesController {

    @Autowired
    private SavedHomeService savedHomeService;
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        return null;
    }

    // GET danh sách saved homes
    @GetMapping
    public ResponseEntity<?> getSavedHomes() {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        List<SavedHome> homes = savedHomeService.findByUserId(userId);
        return ResponseEntity.ok(homes);
    }
    
    // ADD saved home
    @PostMapping
    public ResponseEntity<?> addSavedHome(@RequestBody AddSavedHomeDto dto) {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        SavedHome saved = savedHomeService.save(userId, dto.getPropertyId());
        return ResponseEntity.ok(saved);
    }
    
    // DELETE saved home
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeSavedHome(@PathVariable Long propertyId) {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        savedHomeService.remove(userId, propertyId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
```

---

#### Ví Dụ 2: Agent Dashboard với Role Check
```java
@RestController
@RequestMapping("/api/agent/dashboard")
public class AgentDashboardController {

    @Autowired
    private AgentDashboardService dashboardService;
    
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getAgentStats() {
        CustomUserDetails user = getCurrentUser();
        
        if (user == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // Check role AGENT
        if (!"AGENT".equals(user.getRole().toString())) {
            return ResponseEntity.status(403).body("Only agents can access");
        }
        
        AgentStats stats = dashboardService.getStats(user.getUserId());
        return ResponseEntity.ok(stats);
    }
}
```

---

#### Ví Dụ 3: Validate Ownership (Quan Trọng!)
```java
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        return null;
    }
    
    // UPDATE property
    @PutMapping("/{propertyId}")
    public ResponseEntity<?> updateProperty(
        @PathVariable Long propertyId,
        @RequestBody PropertyDto dto
    ) {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // ⭐ QUAN TRỌNG: Validate ownership
        Property property = propertyService.findById(propertyId);
        if (!property.getOwnerId().equals(userId)) {
            return ResponseEntity.status(403)
                .body("You can only edit your own properties");
        }
        
        Property updated = propertyService.update(propertyId, dto);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE property
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId) {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // ⭐ QUAN TRỌNG: Validate ownership before delete
        Property property = propertyService.findById(propertyId);
        if (!property.getOwnerId().equals(userId)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        propertyService.delete(propertyId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
```

---

### ✅ Checklist Khi Tạo API Mới

- [ ] **KHÔNG** dùng `@RequestParam Long userId` hoặc `@RequestParam Long customerId`
- [ ] Lấy userId từ `SecurityContextHolder` thay vì từ request
- [ ] Check `userId != null` để handle unauthenticated user
- [ ] Validate ownership nếu edit/delete resource của người khác
- [ ] Check role (CUSTOMER vs AGENT) nếu cần
- [ ] Return status code phù hợp:
  - `401` cho not authenticated
  - `403` cho no permission
  - `404` cho resource not found

---

## 🔄 PHẦN 2: Lộ Trình Chi Tiết Chức Năng Authentication

### 📂 Các File Chính và Chức Năng

```
src/main/java/com/homifybackend/
├── auth/
│   ├── security/
│   │   ├── CustomUserDetails.java         → Lưu thông tin user đầy đủ
│   │   ├── CustomUserDetailsService.java  → Load user từ database
│   │   ├── JwtService.java                → Generate & validate JWT
│   │   ├── JwtFilter.java                 → Intercept request, authenticate
│   │   ├── SecurityConfigDev.java         → Config Spring Security (dev)
│   │   └── SecurityConfigProd.java        → Config Spring Security (prod)
│   ├── controller/
│   │   └── AuthController.java            → REST endpoints (login, register, etc.)
│   └── service/
│       └── AuthService.java               → Business logic authentication
└── WebConfig.java                         → CORS configuration
```

---

### 🔄 FLOW 1: User Login

#### **Request → Response Flow:**

```
[Client] POST /api/auth/login
   ↓
[AuthController.login()]
   ↓
[AuthService.login()]
   ↓
[CustomUserDetailsService.loadUserByUsername()]
   ↓
[AccountRepository.findByUsernameWithUser()]
   ↓
[CustomUserDetails được tạo với full user info]
   ↓
[AuthenticationManager.authenticate()]
   ↓
[JwtService.generateToken()] → JWT với subject = userId
   ↓
[AuthController.setAuthCookies()] → Set HttpOnly cookies
   ↓
[Client] Response: { email, username, fullName, role, ... }
         + Cookies: accessToken, refreshToken
```

---

#### **Chi Tiết Từng File:**

### 📄 1. AuthController.java

**Vị trí:** `src/main/java/com/homifybackend/auth/controller/AuthController.java`

#### Method: `login()`
**Dòng:** 55-59  
**Chức năng:**
- Nhận `LoginRequest` từ client (email, password, rememberMe)
- Gọi `handleLogin()` để xử lý

#### Method: `handleLogin()`
**Dòng:** 80-99  
**Chức năng:**
```java
1. Set expectedRole vào loginRequest (nếu có)
2. Gọi authService.login() → Trả về UserResponse
3. Gọi setAuthCookies() để set cookies với accessToken & refreshToken
4. Return ResponseEntity.ok(userResponse)
5. Catch BadCredentialsException → Return 401
6. Catch Exception → Return 500
```

#### Method: `setAuthCookies()`
**Dòng:** 383-399  
**Chức năng:**
```java
// Set Access Token Cookie
Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
accessTokenCookie.setHttpOnly(true);      // ⭐ JavaScript không thể truy cập
accessTokenCookie.setSecure(false);       // Set true trong production (HTTPS)
accessTokenCookie.setPath("/");
accessTokenCookie.setMaxAge(24 * 60 * 60); // 24 giờ
response.addCookie(accessTokenCookie);

// Set Refresh Token Cookie (tương tự)
Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
response.addCookie(refreshTokenCookie);
```

---

### 📄 2. AuthService.java

**Vị trí:** `src/main/java/com/homifybackend/auth/service/AuthService.java`

#### Method: `login()`
**Chức năng:**
```java
1. Lấy email từ loginRequest
2. Load UserDetails qua CustomUserDetailsService
3. Authenticate với AuthenticationManager
4. Validate role (nếu expectedRole được set)
5. Generate accessToken và refreshToken qua JwtService
6. Tạo và return UserResponse với:
   - User info (email, username, fullName, role, etc.)
   - accessToken
   - refreshToken
```

**Flow:**
```
loginRequest
   ↓
customUserDetailsService.loadUserByUsername(email)
   ↓
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
)
   ↓
Check role match (nếu có expectedRole)
   ↓
jwtService.generateToken(userDetails, rememberMe)
jwtService.generateRefreshToken(userDetails, rememberMe)
   ↓
UserResponse.builder()
    .email(...)
    .accessToken(...)
    .refreshToken(...)
    .build()
```

---

### 📄 3. CustomUserDetailsService.java

**Vị trí:** `src/main/java/com/homifybackend/auth/security/CustomUserDetailsService.java`

#### Method: `loadUserByUsername()`
**Dòng:** 21-65  
**Chức năng:**
```java
1. Tìm Account theo username
2. Nếu không thấy, thử tìm theo email
3. Throw UsernameNotFoundException nếu không tìm thấy
4. Lấy User từ Account
5. Handle OAuth users (không có password)
6. Build và return CustomUserDetails với:
   - userId
   - username
   - email
   - password
   - fullName
   - phoneNumber
   - registrationDate
   - dateOfBirth
   - gender
   - avatarUrl
   - role
   - account flags (enabled, non-expired, etc.)
```

**Code:**
```java
Account account = accountRepository.findByUsernameWithUser(username)
    .or(() -> accountRepository.findByEmailWithUser(username))
    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

User user = account.getUser();

return CustomUserDetails.builder()
    .userId(user.getUserId())            // ⭐ User ID
    .username(account.getUsername())
    .email(account.getEmail())
    .password(account.getPassword())
    .fullName(user.getFullName())
    .phoneNumber(user.getPhoneNumber())
    .role(user.getRole())                // ⭐ CUSTOMER hoặc AGENT
    // ... các fields khác
    .build();
```

#### Method: `loadUserByUserId()`
**Dòng:** 70-95  
**Chức năng:**
- Tương tự `loadUserByUsername()` nhưng tìm theo userId
- Được gọi từ JwtFilter khi authenticate bằng JWT token

---

### 📄 4. JwtService.java

**Vị trí:** `src/main/java/com/homifybackend/auth/security/JwtService.java`

#### Method: `generateToken()`
**Dòng:** 120-124  
**Chức năng:**
```java
1. Lấy userId từ CustomUserDetails
2. Create JWT với:
   - Subject: userId (String)
   - Claims: empty map (hoặc extraClaims)
   - Expiration: 24h (hoặc 30 ngày nếu rememberMe)
3. Sign với secret key (HS256)
4. Return JWT string
```

**Code:**
```java
public String generateToken(UserDetails userDetails, boolean rememberMe) {
    Map<String, Object> claims = new HashMap<>();
    String subject = getUserIdentifier(userDetails);  // userId
    Long tokenExpiration = rememberMe ? rememberMeExpiration : expiration;
    return createToken(claims, subject, tokenExpiration);
}

private String getUserIdentifier(UserDetails userDetails) {
    if (userDetails instanceof CustomUserDetails) {
        return ((CustomUserDetails) userDetails).getUserIdAsString();
    }
    return userDetails.getUsername();
}

private String createToken(Map<String, Object> claims, String subject, Long expiration) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)                    // ⭐ userId
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())           // HS256 với secret key
        .compact();
}
```

#### Method: `extractUserId()`
**Dòng:** 71-78  
**Chức năng:**
```java
// Extract userId từ JWT subject
public Long extractUserId(String token) {
    String userId = extractClaim(token, Claims::getSubject);
    return userId != null ? Long.parseLong(userId) : null;
}
```

#### Method: `validateToken()`
**Dòng:** 185-194  
**Chức năng:**
```java
1. Extract userId từ token
2. So sánh với userId trong CustomUserDetails
3. Check token chưa expired
4. Return true nếu valid, false nếu không
```

---

### 📄 5. CustomUserDetails.java

**Vị trí:** `src/main/java/com/homifybackend/auth/security/CustomUserDetails.java`

**Chức năng:** Class lưu trữ toàn bộ thông tin user

**Fields:**
```java
private Long userId;                    // ⭐ ID user
private String username;
private String email;
private String password;
private String fullName;
private String phoneNumber;
private LocalDate registrationDate;
private LocalDate dateOfBirth;
private Gender gender;
private String avatarUrl;
private Role role;                      // ⭐ CUSTOMER or AGENT

// Account status
private boolean accountNonExpired;
private boolean accountNonLocked;
private boolean credentialsNonExpired;
private boolean enabled;
```

**Method quan trọng:**
```java
// Dòng 84-86
public String getUserIdAsString() {
    return userId != null ? userId.toString() : null;
}

// Dòng 61-64
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    String roleName = "ROLE_" + role.name();  // "ROLE_CUSTOMER" or "ROLE_AGENT"
    return Collections.singletonList(new SimpleGrantedAuthority(roleName));
}
```

---

### 🔄 FLOW 2: Client Gọi API Được Bảo Vệ

```
[Client] GET /api/profile
         Cookie: accessToken=eyJhbGciOiJ...
   ↓
[JwtFilter.doFilterInternal()]
   ↓
[1] Extract JWT từ cookie "accessToken"
   ↓
[2] jwtService.extractUserId(jwt) → userId
   ↓
[3] customUserDetailsService.loadUserByUserId(userId)
   ↓
[4] jwtService.validateToken(jwt, userDetails)
   ↓
[5] Create UsernamePasswordAuthenticationToken
   ↓
[6] SecurityContextHolder.setAuthentication(authToken)
   ↓
[7] filterChain.doFilter() → Request tiếp tục
   ↓
[ProfileController.getProfile()]
   ↓
[8] SecurityContextHolder.getContext().getAuthentication()
   ↓
[9] CustomUserDetails userDetails = auth.getPrincipal()
   ↓
[10] Long userId = userDetails.getUserId()
   ↓
[11] userService.findById(userId)
   ↓
[12] Return user data
```

---

### 📄 6. JwtFilter.java

**Vị trí:** `src/main/java/com/homifybackend/auth/security/JwtFilter.java`

#### Method: `doFilterInternal()`
**Dòng:** 28-87  
**Chức năng:** Intercept mọi HTTP request để authenticate

**Chi tiết từng bước:**

```java
// STEP 1: Extract JWT từ cookie
Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie cookie : cookies) {
        if ("accessToken".equals(cookie.getName())) {
            jwt = cookie.getValue();
            break;
        }
    }
}

// STEP 2: Fallback to Authorization header (backward compatibility)
if (jwt == null) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7);
    }
}

// STEP 3: Nếu không có JWT, tiếp tục mà không authenticate
if (jwt == null) {
    filterChain.doFilter(request, response);
    return;
}

// STEP 4: Extract userId từ JWT
Long userId = jwtService.extractUserId(jwt);

// STEP 5: Load user từ database
if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    CustomUserDetailsService customUserDetailsService = 
        (CustomUserDetailsService) userDetailsService;
    UserDetails userDetails = customUserDetailsService.loadUserByUserId(userId);
    
    // STEP 6: Validate token
    if (jwtService.validateToken(jwt, userDetails)) {
        // STEP 7: Create authentication token
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
                userDetails,           // ⭐ Principal = CustomUserDetails
                null,                  // Credentials (không cần password)
                userDetails.getAuthorities()  // Authorities (roles)
            );
        
        // STEP 8: Set authentication details
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        
        // STEP 9: ⭐ Set vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}

// STEP 10: Continue filter chain
filterChain.doFilter(request, response);
```

**Kết quả:**
- Request đã được authenticate
- `SecurityContextHolder` chứa `Authentication` object
- `Authentication.getPrincipal()` trả về `CustomUserDetails` với full user info

---

### 📄 7. WebConfig.java

**Vị trí:** `src/main/java/com/homifybackend/WebConfig.java`

**Dòng:** 10-18  
**Chức năng:** Configure CORS để support cookies

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:3000", "http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)    // ⭐ CRITICAL: Enable cookies
        .maxAge(3600);
}
```

**Quan trọng:** `allowCredentials(true)` cho phép browser gửi cookies cross-origin.

---

### 📄 8. SecurityConfigDev.java / SecurityConfigProd.java

**Vị trí:** `src/main/java/com/homifybackend/auth/security/SecurityConfigDev.java`

**Chức năng:** Configure Spring Security

#### Dev Profile:
```java
@Configuration
@Profile({"dev", "test"})
public class SecurityConfigDev {
    
    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)    // Disable CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()            // ⭐ Dev: Tất cả đều allow
            );
        return http.build();
    }
}
```

#### Prod Profile:
```java
@Configuration
@Profile("prod")
public class SecurityConfigProd {
    
    @Bean
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // Public endpoints
                .anyRequest().authenticated()                // ⭐ Prod: Cần authentication
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

---

### 🔄 FLOW 3: Refresh Token

```
[Client] POST /api/auth/refresh
         Cookie: refreshToken=eyJhbGciOiJ...
   ↓
[AuthController.refreshToken()]
   ↓
[1] extractRefreshTokenFromRequest() → Lấy từ cookie
   ↓
[2] authService.refreshToken(refreshToken)
   ↓
[3] jwtService.validateToken(refreshToken)
   ↓
[4] jwtService.extractUserId(refreshToken) → userId
   ↓
[5] customUserDetailsService.loadUserByUserId(userId)
   ↓
[6] jwtService.generateToken(userDetails)           → New access token
[7] jwtService.generateRefreshToken(userDetails)    → New refresh token
   ↓
[8] setAuthCookies() → Set new cookies
   ↓
[9] Return UserResponse
```

#### Method: `refreshToken()` trong AuthController
**Dòng:** 309-336  
```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    // Extract refresh token từ cookie
    String refreshToken = extractRefreshTokenFromRequest(request);
    
    if (refreshToken == null) {
        return ResponseEntity.status(401)
            .body(new ErrorResponse("Invalid refresh token"));
    }
    
    // Refresh và generate tokens mới
    UserResponse userResponse = authService.refreshToken(refreshToken);
    
    // Set cookies mới
    setAuthCookies(response, userResponse.getAccessToken(), userResponse.getRefreshToken());
    
    return ResponseEntity.ok(userResponse);
}
```

---

### 🔄 FLOW 4: Logout

```
[Client] POST /api/auth/logout
   ↓
[AuthController.logout()]
   ↓
[1] Create cookies với maxAge = 0
[2] Cookie("accessToken", null).setMaxAge(0)
[3] Cookie("refreshToken", null).setMaxAge(0)
   ↓
[4] response.addCookie() → Xóa cookies
   ↓
[5] Return success message
```

#### Method: `logout()` trong AuthController
**Dòng:** 338-361  
```java
@PostMapping("/logout")
public ResponseEntity<?> logout(HttpServletResponse response) {
    // Clear access token cookie
    Cookie accessTokenCookie = new Cookie("accessToken", null);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(0);        // ⭐ Xóa cookie
    response.addCookie(accessTokenCookie);

    // Clear refresh token cookie
    Cookie refreshTokenCookie = new Cookie("refreshToken", null);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(0);       // ⭐ Xóa cookie
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.ok(new MessageResponse("Logout successful"));
}
```

---

## 📊 Tóm Tắt Các File và Chức Năng Chính

| File | Chức Năng Chính | Methods Quan Trọng |
|------|-----------------|-------------------|
| **CustomUserDetails.java** | Lưu trữ thông tin user đầy đủ | `getUserId()`, `getRole()`, `getAuthorities()` |
| **CustomUserDetailsService.java** | Load user từ database | `loadUserByUsername()`, `loadUserByUserId()` |
| **JwtService.java** | Generate & validate JWT | `generateToken()`, `extractUserId()`, `validateToken()` |
| **JwtFilter.java** | Intercept request, authenticate | `doFilterInternal()` |
| **AuthController.java** | REST endpoints authentication | `login()`, `refreshToken()`, `logout()`, `setAuthCookies()` |
| **AuthService.java** | Business logic authentication | `login()`, `register()`, `refreshToken()` |
| **WebConfig.java** | CORS configuration | `addCorsMappings()` với `allowCredentials(true)` |
| **SecurityConfigDev.java** | Spring Security config (dev) | `devSecurityFilterChain()` - permitAll() |
| **SecurityConfigProd.java** | Spring Security config (prod) | `prodSecurityFilterChain()` - authenticated() |

---

## 🎯 Key Takeaways

### Cho Developer:
1. **KHÔNG BAO GIỜ** tin tưởng userId từ request parameters
2. **LUÔN LUÔN** lấy userId từ `SecurityContextHolder`
3. **BẮT BUỘC** validate ownership trước khi edit/delete
4. Check `userId != null` để handle unauthenticated user
5. Return đúng HTTP status codes (401, 403, 404)

### Về Kiến Trúc:
1. JWT subject chứa **userId** (không phải username/email)
2. Token được lưu trong **HttpOnly cookies** (không thể access bởi JS)
3. Browser tự động gửi cookies → Frontend không cần code gì thêm
4. `SecurityContext` chứa `CustomUserDetails` với full user info
5. `JwtFilter` tự động authenticate mọi request

---

## 📚 Quick Reference

### Backend - Get Current User
```java
// Option 1: Chỉ lấy userId
private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }
    return null;
}

// Option 2: Lấy full user info
private CustomUserDetails getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
        return (CustomUserDetails) auth.getPrincipal();
    }
    return null;
}
```

### Sử Dụng trong Endpoint
```java
@GetMapping("/your-endpoint")
public ResponseEntity<?> yourEndpoint() {
    Long userId = getCurrentUserId();
    
    if (userId == null) {
        return ResponseEntity.status(401).body("Not authenticated");
    }
    
    // Your logic here với userId an toàn
    YourData data = yourService.getData(userId);
    return ResponseEntity.ok(data);
}
```

---

**🎉 Hoàn tất! Bạn đã có đầy đủ thông tin để implement authentication trong module của mình.**
