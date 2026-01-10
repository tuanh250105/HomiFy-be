# 🔐 Hướng Dẫn Authentication - HomiFy Backend

## 📋 Mục Lục
1. [Tổng Quan Hệ Thống](#tổng-quan-hệ-thống)
2. [Hướng Dẫn Backend Developer](#hướng-dẫn-backend-developer)
3. [Hướng Dẫn Frontend Developer](#hướng-dẫn-frontend-developer)
4. [Security Best Practices](#security-best-practices)

---

## 🎯 Tổng Quan Hệ Thống

### Kiến Trúc Authentication
HomiFy sử dụng **Cookie-based JWT Authentication** với các đặc điểm:

- ✅ **HttpOnly Cookies**: JWT được lưu trong cookie, không thể access bởi JavaScript
- ✅ **userId trong JWT**: Token chứa user ID thay vì username/email
- ✅ **CustomUserDetails**: Lưu toàn bộ thông tin user trong SecurityContext
- ✅ **Automatic Cookie Handling**: Browser tự động gửi cookies, frontend không cần code thêm

### Authentication Flow
```
1. User Login
   ↓
2. Backend validate credentials
   ↓
3. Generate JWT (subject = userId)
   ↓
4. Set HttpOnly cookies:
   - accessToken (24h)
   - refreshToken (7 days)
   ↓
5. Return user info (NO tokens in body)
   ↓
6. Browser tự động gửi cookies trong mọi request
   ↓
7. Backend đọc cookie → Authenticate → SecurityContext
```

---

## 🔧 Hướng Dẫn Backend Developer

### ⚠️ VẤN ĐỀ BẢO MẬT NGHIÊM TRỌNG

#### ❌ KHÔNG BAO GIỜ LÀM NHƯ NÀY:
```java
// ❌ DANGER: User có thể fake userId!
@GetMapping("/profile")
public ResponseEntity<?> getProfile(@RequestParam Long userId) {
    // Hacker gọi: /profile?userId=999 để xem profile người khác!
    User user = userService.findById(userId);
    return ResponseEntity.ok(user);
}

@DeleteMapping("/saved-homes/{propertyId}")
public ResponseEntity<?> remove(
    @PathVariable Long propertyId,
    @RequestParam Long customerId  // ❌ Không an toàn!
) {
    service.remove(customerId, propertyId);
    return ResponseEntity.ok("OK");
}
```

**Vấn đề:** User có thể thay đổi `userId` trong URL để:
- Xem thông tin người khác
- Xóa/sửa dữ liệu người khác
- Bypass authorization

---

### ✅ CÁCH LÀM ĐÚNG

#### Option 1: Lấy User từ SecurityContext (Khuyến nghị)

```java
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.homifybackend.auth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping
    public ResponseEntity<?> getProfile() {
        // ✅ SECURE: Lấy userId từ JWT token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();
        
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }
}
```

#### Option 2: Helper Method (Gọn hơn)

```java
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }
    
    @GetMapping("/saved-homes")
    public ResponseEntity<?> getSavedHomes() {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        List<Home> homes = savedHomeService.getByUserId(userId);
        return ResponseEntity.ok(homes);
    }
    
    @DeleteMapping("/saved-homes/{propertyId}")
    public ResponseEntity<?> removeSavedHome(@PathVariable Long propertyId) {
        Long userId = getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // User chỉ có thể xóa saved home của chính mình
        savedHomeService.remove(userId, propertyId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
```

---

### 📚 CustomUserDetails API

Các thông tin có sẵn trong `CustomUserDetails`:

```java
CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

// Available properties:
Long userId = userDetails.getUserId();
String email = userDetails.getEmail();
String username = userDetails.getUsername();
String fullName = userDetails.getFullName();
String phoneNumber = userDetails.getPhoneNumber();
String role = userDetails.getRole().toString(); // "CUSTOMER" or "AGENT"
String avatarUrl = userDetails.getAvatarUrl();
LocalDate dateOfBirth = userDetails.getDateOfBirth();
Gender gender = userDetails.getGender();
LocalDateTime registrationDate = userDetails.getRegistrationDate();
```

---

### 🎯 Ví Dụ Thực Tế

#### Example 1: Customer Dashboard
```java
@RestController
@RequestMapping("/api/customer/dashboard")
public class CustomerDashboardController {

    private final DashboardService dashboardService;
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUserId();
        }
        return null;
    }
    
    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            return user.getRole().toString().equalsIgnoreCase(role);
        }
        return false;
    }

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview() {
        // Verify CUSTOMER role
        if (!hasRole("CUSTOMER")) {
            return ResponseEntity.status(403).body("Only customers can access");
        }
        
        Long customerId = getCurrentUserId();
        if (customerId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        DashboardData data = dashboardService.getCustomerOverview(customerId);
        return ResponseEntity.ok(data);
    }
}
```

#### Example 2: Agent Dashboard
```java
@RestController
@RequestMapping("/api/agent/dashboard")
public class AgentDashboardController {

    private final AgentService agentService;
    
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        CustomUserDetails user = getCurrentUser();
        
        if (user == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // Verify AGENT role
        if (!"AGENT".equals(user.getRole().toString())) {
            return ResponseEntity.status(403).body("Only agents can access");
        }
        
        AgentStats stats = agentService.getStats(user.getUserId());
        return ResponseEntity.ok(stats);
    }
}
```

#### Example 3: Update Profile
```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        Long userId = currentUser.getUserId();
        
        // Update profile
        User updated = userService.updateProfile(userId, dto);
        
        return ResponseEntity.ok(updated);
    }
}
```

#### Example 4: Validate Ownership
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        Long ownerId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
        
        // Verify ownership
        Property property = propertyService.findById(propertyId);
        if (!property.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(403)
                .body("You can only edit your own properties");
        }
        
        Property updated = propertyService.update(propertyId, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        Long ownerId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
        
        // Verify ownership before delete
        Property property = propertyService.findById(propertyId);
        if (!property.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        propertyService.delete(propertyId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
```

---

### 🛡️ Best Practices

#### 1. KHÔNG BAO GIỜ tin tưởng userId từ request
```java
// ❌ NEVER
@GetMapping("/data")
public Data getData(@RequestParam Long userId) { ... }

// ✅ ALWAYS
@GetMapping("/data")
public Data getData() {
    Long userId = getCurrentUserId();
    ...
}
```

#### 2. Validate ownership trước khi modify/delete
```java
// ✅ GOOD
Property property = propertyService.findById(propertyId);
if (!property.getOwnerId().equals(currentUserId)) {
    return ResponseEntity.status(403).body("Access denied");
}
```

#### 3. Sử dụng @PreAuthorize cho role-based access (Optional)
```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.findAll();
    }
}
```

#### 4. Return đúng HTTP status codes
```java
// 401: Not authenticated (chưa login)
if (userId == null) {
    return ResponseEntity.status(401).body("Please login");
}

// 403: Authenticated but no permission
if (!hasRole("AGENT")) {
    return ResponseEntity.status(403).body("Agents only");
}

// 404: Resource not found
if (resource == null) {
    return ResponseEntity.status(404).body("Not found");
}
```

---

## 🎨 Hướng Dẫn Frontend Developer

### Quick Setup

#### Step 1: Cấu hình Axios
```javascript
// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true  // ⭐ CRITICAL: Enable cookies
});

// Auto refresh token khi expired
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Refresh token
        await api.post('/auth/refresh');
        // Retry original request
        return api(originalRequest);
      } catch (refreshError) {
        // Redirect to login
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

#### Step 2: Sử dụng API

```javascript
import api from './api/axiosConfig';

// ✅ Login
async function login(email, password) {
  const response = await api.post('/auth/login', { 
    email, 
    password,
    rememberMe: true 
  });
  
  // Response: { email, username, fullName, phone, role }
  // Cookies tự động được set!
  return response.data;
}

// ✅ Get Profile (NO userId needed!)
async function getProfile() {
  const response = await api.get('/profile');
  return response.data;
}

// ✅ Update Profile (userId from cookie)
async function updateProfile(profileData) {
  const response = await api.put('/profile', profileData);
  return response.data;
}

// ✅ Get Saved Homes (customerId from cookie)
async function getSavedHomes() {
  const response = await api.get('/account/saved-homes');
  return response.data;
}

// ✅ Agent Dashboard (agentId from cookie)
async function getAgentDashboard() {
  const response = await api.get('/agent/dashboard/overview');
  return response.data;
}

// ✅ Logout
async function logout() {
  await api.post('/auth/logout');
  window.location.href = '/login';
}
```

---

### 🔄 Migration từ localStorage

#### ❌ BEFORE: Manual token handling
```javascript
// OLD CODE - XÓA ĐI!
const token = localStorage.getItem('accessToken');
const userId = localStorage.getItem('userId');

const response = await axios.get(`/api/profile?userId=${userId}`, {
  headers: { 
    Authorization: `Bearer ${token}` 
  }
});
```

#### ✅ AFTER: Cookie-based (tự động)
```javascript
// NEW CODE - Đơn giản hơn nhiều!
const response = await api.get('/profile', {
  withCredentials: true  // Cookies tự động gửi
});
```

---

### 📋 Migration Checklist

- [ ] **Xóa localStorage token management**
  ```javascript
  // ❌ XÓA các dòng này
  localStorage.setItem('accessToken', token);
  localStorage.getItem('accessToken');
  localStorage.removeItem('accessToken');
  localStorage.setItem('userId', userId);
  ```

- [ ] **Xóa manual Authorization headers**
  ```javascript
  // ❌ XÓA
  headers: { Authorization: `Bearer ${token}` }
  ```

- [ ] **Xóa userId/customerId/agentId khỏi API calls**
  ```javascript
  // ❌ BEFORE
  /api/profile?userId=123
  /api/saved-homes?customerId=123
  /api/dashboard?agentId=456
  
  // ✅ AFTER
  /api/profile
  /api/saved-homes
  /api/dashboard
  ```

- [ ] **Add withCredentials: true**
  ```javascript
  // ✅ ADD
  const api = axios.create({
    withCredentials: true
  });
  ```

---

### 🎭 React Example

```javascript
// src/hooks/useAuth.js
import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

export function useAuth() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const response = await api.get('/profile');
      setUser(response.data);
    } catch (error) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { 
      email, 
      password 
    });
    setUser(response.data);
    return response.data;
  };

  const logout = async () => {
    await api.post('/auth/logout');
    setUser(null);
    window.location.href = '/login';
  };

  return { user, loading, login, logout };
}

// Usage in component
function App() {
  const { user, loading, login, logout } = useAuth();

  if (loading) return <div>Loading...</div>;
  
  if (!user) {
    return <LoginPage onLogin={login} />;
  }

  return (
    <div>
      <Header user={user} onLogout={logout} />
      <Dashboard user={user} />
    </div>
  );
}
```

---

## 🚨 Troubleshooting

### Issue 1: CORS Error
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**Solution:** Backend đã config CORS trong `WebConfig.java`:
```java
.allowedOrigins("http://localhost:3000", "http://localhost:5173")
.allowCredentials(true)
```

Đảm bảo frontend URL match với config!

---

### Issue 2: Cookies không được gửi
**Solution:** 
```javascript
// Đảm bảo có withCredentials: true
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true  // REQUIRED!
});
```

---

### Issue 3: 401 sau khi login
**Solution:** 
1. Check cookies trong DevTools → Application → Cookies
2. Phải có `accessToken` và `refreshToken`
3. Verify `withCredentials: true` trong axios config

---

## 🍪 Cookie Details

### Access Token Cookie
```
Name: accessToken
HttpOnly: true          ← JavaScript không thể access
Secure: false           ← Set true trong production (HTTPS)
Path: /
Max-Age: 86400         ← 24 hours
```

### Refresh Token Cookie
```
Name: refreshToken
HttpOnly: true
Secure: false
Path: /
Max-Age: 604800        ← 7 days
```

---

## 📡 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Cookie Response |
|--------|----------|----------------|
| POST | `/api/auth/login` | ✅ Sets cookies |
| POST | `/api/auth/login/customer` | ✅ Sets cookies |
| POST | `/api/auth/login/agent` | ✅ Sets cookies |
| POST | `/api/auth/register` | ❌ No cookies (sends OTP) |
| POST | `/api/auth/verify-otp` | ✅ Sets cookies |
| POST | `/api/auth/google/customer` | ✅ Sets cookies |
| POST | `/api/auth/google/agent` | ✅ Sets cookies |
| POST | `/api/auth/choose-role` | ✅ Sets cookies |
| POST | `/api/auth/refresh` | ✅ Sets cookies |
| POST | `/api/auth/logout` | ❌ Clears cookies |

---

## 📊 Security Benefits

| Feature | Before (Header) | After (Cookie) |
|---------|----------------|----------------|
| Token Storage | localStorage | HttpOnly Cookie |
| XSS Vulnerable | ✅ Yes | ❌ No |
| Manual Header | ✅ Required | ❌ Auto |
| Auto-Send | ❌ No | ✅ Yes |
| Fake userId | ✅ Possible | ❌ Impossible |

---

## ✅ Checklist khi Tạo API Mới

### Backend:
- [ ] Không dùng `@RequestParam Long userId`
- [ ] Lấy userId từ `SecurityContextHolder`
- [ ] Check `userId != null` (handle unauthenticated)
- [ ] Validate ownership nếu edit/delete resource
- [ ] Check role nếu cần (CUSTOMER vs AGENT)
- [ ] Return 401 cho not authenticated
- [ ] Return 403 cho no permission

### Frontend:
- [ ] Không truyền userId trong URL
- [ ] Dùng `withCredentials: true`
- [ ] Không lưu token vào localStorage
- [ ] Handle 401 để refresh token
- [ ] Handle 403 để show error

---

## 🎓 Key Takeaways

1. **Backend:** Lấy userId từ `SecurityContextHolder`, KHÔNG tin tưởng request params
2. **Frontend:** Chỉ cần `withCredentials: true`, không cần quản lý token
3. **Security:** HttpOnly cookies + userId in JWT = An toàn tuyệt đối
4. **Migration:** Xóa userId khỏi tất cả API calls

---

## 💡 Quick Reference

### Backend - Get Current User
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
Long userId = user.getUserId();
String role = user.getRole().toString();
```

### Frontend - Call API
```javascript
// Setup once
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true
});

// Use everywhere
const data = await api.get('/endpoint');
```

---

**🎉 Ready to use! Hệ thống authentication đã sẵn sàng.**

**Câu hỏi?** Tham khảo source code trong:
- `JwtFilter.java` - Cookie reading logic
- `AuthController.java` - Cookie setting logic
- `CustomUserDetails.java` - User info structure
