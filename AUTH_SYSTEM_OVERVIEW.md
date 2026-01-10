# 📖 Authentication System - Complete Documentation

## 🎯 Tổng Quan Hệ Thống

Hệ thống authentication HomiFy sử dụng **Cookie-based JWT** với các tính năng:
- ✅ HttpOnly Cookies (bảo mật cao, chống XSS)
- ✅ JWT chứa userId thay vì username/email
- ✅ CustomUserDetails lưu toàn bộ thông tin user
- ✅ SecurityUtils để controllers lấy user info
- ✅ Automatic token refresh
- ✅ Role-based access control (CUSTOMER, AGENT, ADMIN)

---

## 📁 File Structure

```
HomiFy-be/
├── src/main/java/com/homifybackend/
│   ├── auth/
│   │   ├── controller/
│   │   │   └── AuthController.java          ← Login, Register, Logout endpoints
│   │   ├── security/
│   │   │   ├── JwtFilter.java               ← Đọc JWT từ cookie, authenticate user
│   │   │   ├── JwtService.java              ← Generate/validate JWT (userId in subject)
│   │   │   ├── CustomUserDetails.java       ← Full user info trong SecurityContext
│   │   │   └── CustomUserDetailsService.java← Load user by userId
│   │   ├── service/
│   │   │   ├── AuthService.java             ← Business logic (login, register, etc)
│   │   │   └── GoogleOAuthService.java      ← Google authentication
│   │   └── config/
│   │       └── AuthConstants.java           ← All constants (errors, success messages)
│   │
│   ├── security/
│   │   └── SecurityUtils.java               ← ⭐ Utility để lấy user info
│   │
│   ├── controller/                          ← ⭐ Các module khác dùng SecurityUtils
│   │   ├── dashboard/
│   │   │   └── DashboardController.java     ← Example: Agent dashboard
│   │   ├── SavedHomeController/
│   │   │   └── SavedHomesController.java    ← Example: Customer saved homes
│   │   └── ManagePersonalinfoController/
│   │       └── ManagePersonalInfoController.java
│   │
│   └── WebConfig.java                       ← CORS config với allowCredentials
│
└── Documentation/
    ├── CONTROLLER_AUTH_GUIDE.md             ← ⭐ Hướng dẫn cho Backend Developers
    ├── FRONTEND_INTEGRATION.md              ← ⭐ Hướng dẫn cho Frontend Developers
    └── THIS_FILE.md                         ← Overview tổng quan
```

---

## 🔐 Authentication Flow

### 1. Login Flow
```
User → POST /api/auth/login { email, password }
  ↓
AuthController → AuthService.login()
  ↓
Validate credentials
  ↓
Load CustomUserDetails (with userId, email, name, role, etc)
  ↓
JwtService.generateToken(userDetails)
  - Subject: userId (not email!)
  - Claims: email, role, etc
  ↓
AuthController.setAuthCookies(response, accessToken, refreshToken)
  - Set HttpOnly cookie: accessToken (24h)
  - Set HttpOnly cookie: refreshToken (7d)
  ↓
Return UserResponse { email, username, fullName, phone, role }
  (NO userId, NO tokens in body!)
```

### 2. Request Authentication Flow
```
Browser → GET /api/protected-endpoint
  + Cookie: accessToken=eyJhbGc...
  ↓
JwtFilter.doFilterInternal()
  ↓
Extract JWT from cookie (or Authorization header fallback)
  ↓
JwtService.extractUserId(jwt) → Get userId from token
  ↓
CustomUserDetailsService.loadUserByUserId(userId)
  ↓
Load full user info from database
  ↓
JwtService.validateToken(jwt, userDetails)
  ↓
Create Authentication object
  ↓
SecurityContextHolder.setContext(authentication)
  ↓
Controller → SecurityUtils.getCurrentUserId()
  ↓
Get userId from SecurityContext
  ↓
Service layer processes request with userId
```

### 3. Token Refresh Flow
```
Frontend → POST /api/auth/refresh
  + Cookie: refreshToken=eyJhbGc...
  ↓
AuthController.refreshToken()
  ↓
Extract refreshToken from cookie
  ↓
JwtService.validateToken(refreshToken)
  ↓
Extract userId from refreshToken
  ↓
Load user by userId
  ↓
Generate NEW accessToken + refreshToken
  ↓
Set new cookies
  ↓
Return UserResponse
```

---

## 🛠️ Backend Developer Guide

### Quick Start (Controllers)

```java
import com.homifybackend.security.SecurityUtils;

@RestController
@RequestMapping("/api/my-endpoint")
public class MyController {

    @GetMapping
    public ResponseEntity<?> getData() {
        // ✅ Get userId from JWT token
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // Process with userId
        return ResponseEntity.ok(service.getData(userId));
    }
    
    @PostMapping("/agent-only")
    public ResponseEntity<?> agentEndpoint() {
        // ✅ Require AGENT role
        SecurityUtils.requireRole("AGENT");
        
        Long agentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.agentData(agentId));
    }
}
```

### ⚠️ Security Rules

**NEVER:**
```java
// ❌ VULNERABLE: User can fake userId!
@GetMapping("/profile")
public User getProfile(@RequestParam Long userId) {
    return userService.findById(userId);
}
```

**ALWAYS:**
```java
// ✅ SECURE: Get userId from JWT
@GetMapping("/profile")
public User getProfile() {
    Long userId = SecurityUtils.getCurrentUserId();
    return userService.findById(userId);
}
```

**📖 Read More:** [CONTROLLER_AUTH_GUIDE.md](CONTROLLER_AUTH_GUIDE.md)

---

## 🎨 Frontend Developer Guide

### Quick Setup (Axios)

```javascript
// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true  // ⭐ REQUIRED for cookies
});

export default api;
```

### API Usage

```javascript
// Login
const user = await api.post('/auth/login', { email, password });

// Get profile (NO userId needed!)
const profile = await api.get('/profile');

// Update profile (userId from cookie)
await api.put('/manage-personalinfo', profileData);

// Logout
await api.post('/auth/logout');
```

### Migration Checklist

- [ ] Remove `localStorage.setItem('accessToken')`
- [ ] Remove manual `Authorization` headers
- [ ] Remove `userId` from API calls
- [ ] Add `withCredentials: true` to axios

**📖 Read More:** [FRONTEND_INTEGRATION.md](FRONTEND_INTEGRATION.md)

---

## 🔑 SecurityUtils API

| Method | Return | Description |
|--------|--------|-------------|
| `getCurrentUserId()` | `Long` | User ID từ JWT token |
| `getCurrentUserEmail()` | `String` | Email của user |
| `getCurrentUsername()` | `String` | Username |
| `getCurrentUserFullName()` | `String` | Full name |
| `getCurrentUserRole()` | `String` | Role (CUSTOMER/AGENT) |
| `getCurrentUserPhone()` | `String` | Phone number |
| `isAuthenticated()` | `boolean` | Đã login chưa? |
| `hasRole(String)` | `boolean` | Có role cụ thể không? |
| `requireRole(String)` | `void` | Throw exception nếu không có role |
| `validateUserAccess(Long)` | `void` | Verify user chỉ access data của mình |
| `getCurrentUserDetails()` | `CustomUserDetails` | Full user object |

---

## 🍪 Cookie Configuration

### Access Token Cookie
```
Name: accessToken
Value: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
HttpOnly: true          ← JavaScript cannot access
Secure: false           ← Set true in production (HTTPS)
Path: /
Max-Age: 86400         ← 24 hours
SameSite: Lax          ← CSRF protection
```

### Refresh Token Cookie
```
Name: refreshToken
Value: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
HttpOnly: true
Secure: false
Path: /
Max-Age: 604800        ← 7 days
SameSite: Lax
```

---

## 📡 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Cookie Response |
|--------|----------|-------------|-----------------|
| POST | `/api/auth/login` | Login | Sets cookies |
| POST | `/api/auth/login/customer` | Customer login | Sets cookies |
| POST | `/api/auth/login/agent` | Agent login | Sets cookies |
| POST | `/api/auth/register` | Register (sends OTP) | No cookies |
| POST | `/api/auth/verify-otp` | Verify OTP & activate | Sets cookies |
| POST | `/api/auth/google/customer` | Google OAuth customer | Sets cookies |
| POST | `/api/auth/google/agent` | Google OAuth agent | Sets cookies |
| POST | `/api/auth/choose-role` | Switch role | Sets cookies |
| POST | `/api/auth/refresh` | Refresh tokens | Sets cookies |
| POST | `/api/auth/logout` | Logout | Clears cookies |
| POST | `/api/auth/forgot-password` | Request password reset | Email OTP |
| POST | `/api/auth/reset-password` | Reset password | No cookies |

### Protected Endpoints (Examples)

| Endpoint | Required Role | UserId Source |
|----------|--------------|---------------|
| `/api/profile` | Any | From JWT |
| `/api/saved-homes` | CUSTOMER | From JWT |
| `/api/dashboard/overview` | AGENT | From JWT |
| `/api/manage-personalinfo` | Any | From JWT |

---

## 🔧 Configuration

### CORS (WebConfig.java)
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // ⭐ Required for cookies
                .maxAge(3600);
    }
}
```

### JWT Configuration (application.properties)
```properties
jwt.secret=your-secret-key-here
jwt.access-token-expiration=86400000    # 24 hours
jwt.refresh-token-expiration=604800000  # 7 days
```

---

## 🚨 Troubleshooting

### Issue: 401 Unauthorized
**Cause:** Cookie không được gửi hoặc token expired
**Fix:**
- Check `withCredentials: true` trong axios
- Check cookies trong DevTools
- Try refresh token endpoint

### Issue: CORS Error
**Cause:** Frontend URL không match backend CORS config
**Fix:** Add frontend URL vào `WebConfig.allowedOrigins()`

### Issue: User can access other users' data
**Cause:** Controller dùng `@RequestParam userId`
**Fix:** Dùng `SecurityUtils.getCurrentUserId()` thay vì request param

### Issue: Role check fails
**Cause:** Role format mismatch (ROLE_CUSTOMER vs CUSTOMER)
**Fix:** Dùng `SecurityUtils.hasRole("CUSTOMER")` - không cần prefix ROLE_

---

## 📊 Security Checklist

### Backend
- [x] JWT stored in HttpOnly cookies
- [x] userId in JWT subject (not username/email)
- [x] SecurityUtils for user info extraction
- [x] No userId in UserResponse body
- [x] Controllers use SecurityUtils.getCurrentUserId()
- [x] Role-based access control
- [x] Ownership validation for resources
- [x] CORS configured with credentials
- [x] Generic error messages (prevent user enumeration)

### Frontend
- [x] `withCredentials: true` in axios
- [x] No localStorage token storage
- [x] No manual Authorization headers
- [x] No userId in API calls
- [x] Automatic token refresh interceptor
- [x] Logout clears cookies

---

## 🎓 Learning Resources

1. **Backend Developers:**
   - Read: [CONTROLLER_AUTH_GUIDE.md](CONTROLLER_AUTH_GUIDE.md)
   - Study: `SecurityUtils.java` source code
   - Examples: `SavedHomesController.java`, `DashboardController.java`

2. **Frontend Developers:**
   - Read: [FRONTEND_INTEGRATION.md](FRONTEND_INTEGRATION.md)
   - Setup: Axios with `withCredentials: true`
   - Remove: All localStorage token handling

3. **Security Review:**
   - JWT best practices
   - HttpOnly cookie security
   - XSS prevention
   - CSRF protection with SameSite

---

## 📝 Version History

- **v2.0** - Cookie-based auth with SecurityUtils (Current)
- **v1.0** - Header-based auth with userId in response (Deprecated)

---

## 💡 Key Takeaways

1. **Frontend:** Chỉ cần `withCredentials: true`, không cần quản lý token
2. **Backend:** Dùng `SecurityUtils.getCurrentUserId()`, không tin tưởng request params
3. **Security:** HttpOnly cookies + userId in JWT = An toàn hơn localStorage
4. **Migration:** Xóa userId khỏi tất cả API calls

---

**🎉 Ready to use! Hệ thống authentication đã sẵn sàng cho production.**

**Need help?** Check the guide files hoặc search code trong các controller examples.
