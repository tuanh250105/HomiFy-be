# 🔐 Authentication Guide cho Controllers

## ⚠️ VẤN ĐỀ BẢO MẬT NGHIÊM TRỌNG

### ❌ **KHÔNG BAO GIỜ LÀM NHƯ NÀY:**
```java
// ❌ DANGER: User có thể fake userId để truy cập data của người khác!
@GetMapping("/profile")
public ResponseEntity<?> getProfile(@RequestParam Long userId) {
    // Hacker có thể gọi: /profile?userId=999 để xem profile người khác!
    User user = userService.findById(userId);
    return ResponseEntity.ok(user);
}

@DeleteMapping("/saved-homes/{propertyId}")
public ResponseEntity<?> removeSaved(
    @PathVariable Long propertyId,
    @RequestParam Long customerId  // ❌ Không an toàn!
) {
    service.removeSavedHome(customerId, propertyId);
    return ResponseEntity.ok("Deleted");
}
```

**Vấn đề:** User có thể thay đổi `userId` trong URL để:
- Xem thông tin của người khác
- Xóa/sửa dữ liệu của người khác
- Bypass authorization

---

## ✅ **ĐÚNG CÁCH - Dùng SecurityUtils**

### 1. Import SecurityUtils
```java
import com.homifybackend.security.SecurityUtils;
```

### 2. Lấy User ID từ JWT Token (SecurityContext)

```java
@RestController
@RequestMapping("/api/account")
public class SavedHomesController {

    private final SavedHomesService service;

    @GetMapping("/saved-homes")
    public ResponseEntity<?> getSavedHomes() {
        // ✅ CORRECT: Lấy userId từ JWT token
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        List<SavedHomeDto> homes = service.getSavedHomes(userId);
        return ResponseEntity.ok(homes);
    }

    @DeleteMapping("/saved-homes/{propertyId}")
    public ResponseEntity<?> removeSavedHome(@PathVariable Long propertyId) {
        // ✅ CORRECT: User chỉ có thể xóa saved home của chính mình
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        service.removeSavedHome(userId, propertyId);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
```

---

## 📚 SecurityUtils API Reference

### Lấy Thông Tin User

```java
// Lấy User ID (từ JWT token)
Long userId = SecurityUtils.getCurrentUserId();

// Lấy email
String email = SecurityUtils.getCurrentUserEmail();

// Lấy username
String username = SecurityUtils.getCurrentUsername();

// Lấy full name
String fullName = SecurityUtils.getCurrentUserFullName();

// Lấy role (CUSTOMER, AGENT, ADMIN)
String role = SecurityUtils.getCurrentUserRole();

// Lấy số điện thoại
String phone = SecurityUtils.getCurrentUserPhone();

// Lấy toàn bộ CustomUserDetails object
CustomUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
```

### Kiểm Tra Authentication & Authorization

```java
// Kiểm tra user đã đăng nhập chưa
if (!SecurityUtils.isAuthenticated()) {
    return ResponseEntity.status(401).body("Please login");
}

// Kiểm tra user có role cụ thể không
if (SecurityUtils.hasRole("AGENT")) {
    // Only agents can access this
}

// Yêu cầu role cụ thể (throw exception nếu không match)
SecurityUtils.requireRole("AGENT");

// Validate user chỉ access data của chính mình
SecurityUtils.validateUserAccess(requestedUserId);
```

---

## 🎯 Ví Dụ Thực Tế

### Example 1: Profile Management
```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    // ❌ WRONG: Vulnerable to unauthorized access
    @GetMapping("/wrong")
    public ResponseEntity<?> getProfileWrong(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    // ✅ CORRECT: Secure - uses JWT token
    @GetMapping
    public ResponseEntity<?> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        User updated = userService.updateProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }
}
```

### Example 2: Agent Dashboard
```java
@RestController
@RequestMapping("/api/agent/dashboard")
public class AgentDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview() {
        // Yêu cầu role AGENT
        SecurityUtils.requireRole("AGENT");
        
        Long agentId = SecurityUtils.getCurrentUserId();
        OverviewDto overview = dashboardService.getOverview(agentId);
        
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/performance")
    public ResponseEntity<?> getPerformance() {
        // Kiểm tra role manually
        if (!SecurityUtils.hasRole("AGENT")) {
            return ResponseEntity.status(403).body("Only agents can access");
        }
        
        Long agentId = SecurityUtils.getCurrentUserId();
        Performance7dDto perf = dashboardService.getPerformance7d(agentId);
        
        return ResponseEntity.ok(perf);
    }
}
```

### Example 3: Customer Tours
```java
@RestController
@RequestMapping("/api/tours")
public class CustomerToursController {

    private final TourService tourService;

    @GetMapping
    public ResponseEntity<?> getMyTours() {
        SecurityUtils.requireRole("CUSTOMER");
        
        Long customerId = SecurityUtils.getCurrentUserId();
        List<TourDto> tours = tourService.getCustomerTours(customerId);
        
        return ResponseEntity.ok(tours);
    }

    @PostMapping("/{tourId}/cancel")
    public ResponseEntity<?> cancelTour(@PathVariable Long tourId) {
        Long customerId = SecurityUtils.getCurrentUserId();
        
        if (customerId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // Verify tour belongs to current customer
        tourService.cancelTour(tourId, customerId);
        
        return ResponseEntity.ok("Tour cancelled");
    }
}
```

### Example 4: Admin Access (nếu có path param userId)
```java
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    // Admin có thể xem profile bất kỳ user nào
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        // Chỉ admin mới access được
        SecurityUtils.requireRole("ADMIN");
        
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }
}

// Nhưng nếu user thường xem profile của người khác:
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> viewUserProfile(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // User chỉ có thể xem profile của chính mình
        // HOẶC có thể cho phép xem public profile
        if (!currentUserId.equals(userId)) {
            // Return public profile only
            return ResponseEntity.ok(userService.getPublicProfile(userId));
        }
        
        // Return full profile for own account
        return ResponseEntity.ok(userService.getFullProfile(userId));
    }
}
```

### Example 5: Validation Helper
```java
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    @PutMapping("/{propertyId}")
    public ResponseEntity<?> updateProperty(
        @PathVariable Long propertyId,
        @RequestBody PropertyDto dto
    ) {
        Long ownerId = SecurityUtils.getCurrentUserId();
        
        // Verify property belongs to current user
        Property property = propertyService.findById(propertyId);
        
        if (!property.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(403)
                .body("You can only edit your own properties");
        }
        
        Property updated = propertyService.update(propertyId, dto);
        return ResponseEntity.ok(updated);
    }

    // Hoặc dùng validateUserAccess
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId) {
        Property property = propertyService.findById(propertyId);
        
        // Throw exception if not owner
        SecurityUtils.validateUserAccess(property.getOwnerId());
        
        propertyService.delete(propertyId);
        return ResponseEntity.ok("Deleted");
    }
}
```

---

## 🛡️ Best Practices

### 1. **KHÔNG BAO GIỜ tin tưởng userId từ client**
```java
// ❌ NEVER
@GetMapping("/data")
public Data getData(@RequestParam Long userId) { ... }

// ✅ ALWAYS
@GetMapping("/data")
public Data getData() {
    Long userId = SecurityUtils.getCurrentUserId();
    ...
}
```

### 2. **Validate ownership trước khi modify/delete**
```java
@DeleteMapping("/items/{itemId}")
public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
    Long userId = SecurityUtils.getCurrentUserId();
    
    Item item = itemService.findById(itemId);
    
    // Verify ownership
    if (!item.getOwnerId().equals(userId)) {
        return ResponseEntity.status(403).body("Access denied");
    }
    
    itemService.delete(itemId);
    return ResponseEntity.ok("Deleted");
}
```

### 3. **Sử dụng @PreAuthorize nếu cần (Spring Security)**
```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        // Only ADMIN can access
        return userService.findAll();
    }
}
```

### 4. **Return 401 cho unauthenticated, 403 cho unauthorized**
```java
// 401: Not authenticated
if (SecurityUtils.getCurrentUserId() == null) {
    return ResponseEntity.status(401).body("Please login");
}

// 403: Authenticated but no permission
if (!SecurityUtils.hasRole("AGENT")) {
    return ResponseEntity.status(403).body("Agents only");
}
```

### 5. **Handle exceptions globally**
```java
@ControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }
}
```

---

## 🔄 Migration Guide - Sửa Code Cũ

### Step 1: Tìm tất cả @RequestParam userId/customerId/agentId
```bash
# Search trong IDE
@RequestParam.*userId
@RequestParam.*customerId
@RequestParam.*agentId
```

### Step 2: Replace với SecurityUtils
```java
// BEFORE
@GetMapping("/profile")
public UserDto getProfile(@RequestParam Long userId) {
    return service.getProfile(userId);
}

// AFTER
@GetMapping("/profile")
public UserDto getProfile() {
    Long userId = SecurityUtils.getCurrentUserId();
    if (userId == null) {
        throw new SecurityException("Not authenticated");
    }
    return service.getProfile(userId);
}
```

### Step 3: Update Frontend calls
```javascript
// BEFORE
const response = await api.get(`/profile?userId=${userId}`);

// AFTER - Không cần truyền userId!
const response = await api.get('/profile', { 
    withCredentials: true  // Cookie tự động gửi
});
```

---

## 📦 Package Structure
```
com.homifybackend/
├── security/
│   ├── SecurityUtils.java         ← Utility class này
│   └── ...
├── auth/
│   └── security/
│       ├── CustomUserDetails.java  ← User info trong JWT
│       ├── JwtFilter.java          ← Đọc cookie và authenticate
│       └── ...
└── controller/
    ├── ProfileController.java      ← Dùng SecurityUtils
    ├── DashboardController.java    ← Dùng SecurityUtils
    └── ...
```

---

## ❓ FAQ

**Q: Tại sao không dùng @RequestParam userId?**
A: Vì user có thể thay đổi query param để access data của người khác. JWT token không thể fake!

**Q: Làm sao frontend biết userId để gọi API?**
A: Frontend KHÔNG CẦN biết userId! Backend tự lấy từ JWT cookie.

**Q: Nếu Admin muốn xem profile của user khác thì sao?**
A: Admin endpoint khác, check role ADMIN trước, sau đó mới accept userId từ path param.

**Q: SecurityUtils.getCurrentUserId() trả về null khi nào?**
A: Khi user chưa login hoặc JWT token invalid/expired.

**Q: Có cần check role trong mọi endpoint không?**
A: Tùy business logic. Nếu endpoint chỉ cho AGENT thì dùng `SecurityUtils.requireRole("AGENT")`.

---

## 🚀 Checklist khi tạo API mới

- [ ] Không dùng `@RequestParam Long userId`
- [ ] Dùng `SecurityUtils.getCurrentUserId()` để lấy user ID
- [ ] Check `userId != null` (handle unauthenticated)
- [ ] Validate ownership nếu cần (edit/delete resource)
- [ ] Check role nếu cần (CUSTOMER vs AGENT vs ADMIN)
- [ ] Return 401 cho not authenticated
- [ ] Return 403 cho no permission
- [ ] Test với user khác để verify không thể access cross-user data

---

**LƯU Ý QUAN TRỌNG:** Mọi endpoint yêu cầu authentication PHẢI dùng `SecurityUtils` để lấy user info từ JWT token. KHÔNG BAO GIỜ tin tưởng userId từ request parameters!
