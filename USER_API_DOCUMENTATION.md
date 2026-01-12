# User API Documentation

## Overview
Các API để trả về thông tin CustomUserDetails (user hiện tại đang đăng nhập) dựa trên JWT authentication token.

## Controller
**UserController** - `/api/user`

## Endpoints

### 1. Lấy thông tin user hiện tại
```
GET /api/user/current
```

**Authentication**: Required (JWT Token)

**Response Success:**
```json
{
  "success": true,
  "message": "Current user retrieved successfully",
  "data": {
    "userId": 1,
    "fullName": "John Doe",
    "phoneNumber": "0123456789",
    "email": "john@example.com",
    "username": "johndoe",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "avatarUrl": "https://example.com/avatar.jpg",
    "role": "customer",
    "licenseId": null,
    "rate": null,
    "bio": null,
    "address": {
      "addressId": 1,
      "zipCode": "10000",
      "city": "Hanoi",
      "province": "Hanoi",
      "street": "123 Main St",
      "nation": "Vietnam",
      "latitude": 21.0285,
      "longitude": 105.8542
    }
  }
}
```

---

### 2. Lấy username của user hiện tại
```
GET /api/user/current/username
```

**Authentication**: Required (JWT Token)

**Response Success:**
```json
{
  "success": true,
  "message": "Username retrieved successfully",
  "username": "johndoe"
}
```

---

### 3. Kiểm tra trạng thái authentication
```
GET /api/user/authenticated
```

**Authentication**: Not Required

**Response:**
```json
{
  "success": true,
  "authenticated": true,
  "message": "User is authenticated"
}
```

---

### 4. Lấy thông tin user profile (alias cho /current)
```
GET /api/user/profile
```

**Authentication**: Required (JWT Token)

**Response**: Giống với `/api/user/current`

---

### 5. Lấy thông tin user theo ID (Admin only)
```
GET /api/user/{userId}
```

**Authentication**: Required (JWT Token + ROLE_ADMIN)

**Path Parameters:**
- `userId`: ID của user cần lấy thông tin

**Response Success:**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "userId": 1,
    "fullName": "John Doe",
    "phoneNumber": "0123456789",
    ...
  }
}
```

---

## Error Responses

### 401 Unauthorized
```json
{
  "success": false,
  "message": "User is not authenticated"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "User not found: username"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Error fetching current user: [error details]"
}
```

---

## Implementation Details

### Service Layer
**UserService** - `com.homifybackend.service.UserService`

**Methods:**
- `getCurrentUser()`: Lấy thông tin user từ SecurityContext (JWT token)
- `getUserById(Long userId)`: Lấy thông tin user theo ID
- `getCurrentUsername()`: Lấy username từ SecurityContext
- `isAuthenticated()`: Kiểm tra authentication status

### Security Context
API sử dụng `SecurityContextHolder` để lấy thông tin user từ JWT token:
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
UserDetails userDetails = (UserDetails) authentication.getPrincipal();
```

---

## Usage Example

### Frontend (React/Axios)
```javascript
// Lấy thông tin user hiện tại
const getCurrentUser = async () => {
  try {
    const response = await axios.get('/api/user/current', {
      headers: {
        'Authorization': `Bearer ${jwtToken}`
      }
    });
    
    if (response.data.success) {
      console.log('User:', response.data.data);
    }
  } catch (error) {
    console.error('Error:', error);
  }
};

// Kiểm tra authentication
const checkAuth = async () => {
  try {
    const response = await axios.get('/api/user/authenticated');
    console.log('Is Authenticated:', response.data.authenticated);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

### Postman
1. Lấy JWT token từ endpoint login
2. Thêm token vào Headers:
   - Key: `Authorization`
   - Value: `Bearer YOUR_JWT_TOKEN`
3. Gọi endpoint `/api/user/current`

---

## Notes

- API tự động lấy user từ JWT token, không cần truyền userId
- Không cần header `X-User-Id` như API cũ
- API được bảo vệ bởi Spring Security
- Chỉ admin mới có thể lấy thông tin user khác (endpoint `/{userId}`)
- Tất cả endpoints trả về format JSON thống nhất với `success`, `message`, `data`

---

## Related APIs

### Existing MeController
Nếu muốn sử dụng API cũ với header `X-User-Id`:
```
GET /api/me - Lấy thông tin user (với X-User-Id header)
PUT /api/me - Cập nhật thông tin user
PUT /api/me/password - Đổi password
```

**Note**: MeController sử dụng header `X-User-Id` thay vì JWT token
