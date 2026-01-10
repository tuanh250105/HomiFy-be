# 🎯 Frontend Integration Guide - Cookie-Based Auth

## Quick Setup (1 phút)

### Step 1: Cấu hình Axios
```javascript
// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true  // ⭐ QUAN TRỌNG: Bật cookies
});

// Tự động refresh token khi hết hạn
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        await api.post('/auth/refresh');
        return api(originalRequest);
      } catch {
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

### Step 2: Sử dụng API

#### ✅ Login
```javascript
import api from './api/axiosConfig';

async function login(email, password) {
  const response = await api.post('/auth/login', { 
    email, 
    password,
    rememberMe: true 
  });
  
  // Response: { email, username, fullName, phone, role }
  // Cookies tự động được lưu bởi browser!
  return response.data;
}
```

#### ✅ Gọi API Protected (KHÔNG CẦN TOKEN!)
```javascript
// ❌ BEFORE: Phải manually attach token
const token = localStorage.getItem('accessToken');
const response = await axios.get('/api/profile', {
  headers: { Authorization: `Bearer ${token}` }
});

// ✅ AFTER: Cookies tự động gửi!
const response = await api.get('/profile');
```

#### ✅ Update Profile (userId tự động từ cookie)
```javascript
// ❌ BEFORE: Phải truyền userId
await api.put(`/manage-personalinfo?userId=${userId}`, profileData);

// ✅ AFTER: Backend tự lấy userId từ JWT
await api.put('/manage-personalinfo', profileData);
```

#### ✅ Get Dashboard (agentId tự động)
```javascript
// ❌ BEFORE
const response = await api.get(`/dashboard/overview?agentId=${agentId}`);

// ✅ AFTER
const response = await api.get('/dashboard/overview');
```

#### ✅ Logout
```javascript
async function logout() {
  await api.post('/auth/logout');
  // Cookies đã bị xóa, redirect về login
  window.location.href = '/login';
}
```

---

## 🔄 Migration Checklist

### 1. Xóa localStorage token management
```javascript
// ❌ XÓA các dòng này
localStorage.setItem('accessToken', token);
localStorage.getItem('accessToken');
localStorage.removeItem('accessToken');
```

### 2. Xóa manual Authorization headers
```javascript
// ❌ XÓA
headers: { Authorization: `Bearer ${token}` }
```

### 3. Xóa userId/agentId khỏi API calls
```javascript
// ❌ BEFORE
/api/profile?userId=123
/api/saved-homes?customerId=123
/api/dashboard/overview?agentId=456

// ✅ AFTER
/api/profile
/api/saved-homes
/api/dashboard/overview
```

---

## 🎨 React Example

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
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    setUser(response.data);
    return response.data;
  };

  const logout = async () => {
    await api.post('/auth/logout');
    setUser(null);
  };

  return { user, loading, login, logout };
}

// Usage
function App() {
  const { user, loading, login, logout } = useAuth();

  if (loading) return <div>Loading...</div>;
  if (!user) return <LoginPage onLogin={login} />;

  return <Dashboard user={user} onLogout={logout} />;
}
```

---

## 🚨 Common Issues

### Issue 1: CORS Error
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**Fix:** Backend đã cấu hình CORS trong `WebConfig.java`. Frontend URL phải match:
```java
.allowedOrigins("http://localhost:3000", "http://localhost:5173")
```

### Issue 2: Cookies không được gửi
**Fix:** Đảm bảo `withCredentials: true` trong axios config:
```javascript
const api = axios.create({
  withCredentials: true  // REQUIRED!
});
```

### Issue 3: 401 Unauthorized sau khi login
**Fix:** Check browser cookies (DevTools → Application → Cookies). Phải có `accessToken` và `refreshToken`.

---

## 📱 API Changes Summary

| Endpoint | Before | After |
|----------|--------|-------|
| Get Profile | `/profile?userId=123` | `/profile` |
| Update Profile | `/manage-personalinfo?userId=123` | `/manage-personalinfo` |
| Saved Homes | `/saved-homes?customerId=123` | `/saved-homes` |
| Dashboard | `/dashboard/overview?agentId=456` | `/dashboard/overview` |
| Agent Profile | `/agent/profile?agentId=456` | `/agent/profile` |

**Pattern:** Xóa tất cả `userId`, `customerId`, `agentId` từ query params!

---

## ✅ Benefits

- ✅ **Đơn giản hơn**: Không cần quản lý token manually
- ✅ **An toàn hơn**: HttpOnly cookies chống XSS
- ✅ **Tự động**: Browser tự gửi cookies
- ✅ **Ít lỗi**: Không thể fake userId

---

**Questions?** Đọc file `CONTROLLER_AUTH_GUIDE.md` để hiểu cách backend xử lý authentication.
