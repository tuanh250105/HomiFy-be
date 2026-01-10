# Cookie-Based Authentication Guide

## 🎯 Overview
Backend now uses **HttpOnly Cookies** for JWT storage instead of returning tokens in response body. Frontend doesn't need to manually attach `Authorization` headers - cookies are automatically sent by the browser!

## 🔐 Security Benefits
- ✅ **XSS Protection**: HttpOnly cookies cannot be accessed by JavaScript
- ✅ **No localStorage risk**: Tokens not exposed to client-side code
- ✅ **Automatic sending**: Browser handles token attachment
- ✅ **No userId in response**: User ID embedded in JWT token

## 📋 What Changed?

### Backend Changes
1. **JwtFilter** - Reads JWT from `accessToken` cookie (with Authorization header fallback)
2. **AuthController** - All auth endpoints now set HttpOnly cookies
3. **UserResponse** - `accessToken` and `refreshToken` removed from JSON response (marked `@JsonIgnore`)
4. **WebConfig** - CORS enabled with `allowCredentials(true)`

### API Endpoints Updated
- `POST /api/auth/login` - Sets cookies automatically
- `POST /api/auth/login/customer` - Sets cookies
- `POST /api/auth/login/agent` - Sets cookies
- `POST /api/auth/register/*` - Registration flow (cookies set after OTP verification)
- `POST /api/auth/verify-otp` - Sets cookies after successful verification
- `POST /api/auth/google/*` - Google OAuth sets cookies
- `POST /api/auth/choose-role` - Updates cookies with new role
- `POST /api/auth/refresh` - Refreshes cookies (no body needed!)
- `POST /api/auth/logout` - **NEW**: Clears all auth cookies

## 🌐 Frontend Implementation

### 1. Setup Axios with Credentials

```javascript
// api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true  // ⭐ CRITICAL: Enable cookies
});

export default api;
```

### 2. Login Example (React/Vue/Angular)

```javascript
import api from './api';

// Login
async function login(email, password) {
  try {
    const response = await api.post('/auth/login', {
      email,
      password,
      rememberMe: true
    });
    
    // ✅ Cookies set automatically!
    // Response only contains: { email, username, fullName, phone, role }
    console.log('User:', response.data);
    return response.data;
    
  } catch (error) {
    console.error('Login failed:', error.response?.data);
    throw error;
  }
}
```

### 3. Making Authenticated Requests

```javascript
// No need to manually add headers! Cookies sent automatically
async function getProtectedData() {
  try {
    const response = await api.get('/protected-endpoint');
    return response.data;
  } catch (error) {
    if (error.response?.status === 401) {
      // Token expired or invalid
      console.log('Unauthorized - redirecting to login');
    }
  }
}
```

### 4. Refresh Token

```javascript
// No request body needed - reads from cookie
async function refreshAccessToken() {
  try {
    const response = await api.post('/auth/refresh');
    console.log('Token refreshed!');
    return response.data;
  } catch (error) {
    console.error('Refresh failed:', error);
    // Redirect to login
  }
}
```

### 5. Logout

```javascript
async function logout() {
  try {
    await api.post('/auth/logout');
    console.log('Logged out - cookies cleared');
    // Redirect to login page
  } catch (error) {
    console.error('Logout error:', error);
  }
}
```

### 6. Automatic Token Refresh (Interceptor)

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true
});

// Response interceptor for automatic refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // If 401 and not already retrying
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Attempt refresh
        await api.post('/auth/refresh');
        
        // Retry original request
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed - redirect to login
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

## 🍪 Cookie Details

### Access Token Cookie
- **Name**: `accessToken`
- **HttpOnly**: `true` (cannot be accessed by JavaScript)
- **Secure**: `false` (set to `true` in production with HTTPS)
- **Path**: `/`
- **Max-Age**: `86400` seconds (24 hours)

### Refresh Token Cookie
- **Name**: `refreshToken`
- **HttpOnly**: `true`
- **Secure**: `false` (set to `true` in production)
- **Path**: `/`
- **Max-Age**: `604800` seconds (7 days)

## ⚙️ CORS Configuration

Frontend must match backend's allowed origins in `WebConfig.java`:

```java
.allowedOrigins("http://localhost:3000", "http://localhost:5173")
.allowCredentials(true)
```

Add your frontend URL if different!

## 🚨 Important Notes

1. **HTTPS in Production**: Set `cookie.setSecure(true)` for production
2. **SameSite**: Consider adding `SameSite=Lax` or `SameSite=Strict` for CSRF protection
3. **Domain**: For subdomain sharing, set `cookie.setDomain(".yourdomain.com")`
4. **No localStorage**: Never store tokens in localStorage - defeats the purpose!

## 🔄 Migration from Header-Based Auth

If you have existing code using `Authorization: Bearer <token>`:

**Before:**
```javascript
const token = localStorage.getItem('accessToken');
axios.get('/api/data', {
  headers: { Authorization: `Bearer ${token}` }
});
```

**After:**
```javascript
// Just remove token handling - cookies work automatically!
axios.get('/api/data', { withCredentials: true });
```

## 🧪 Testing

### Test Cookie Creation
```bash
# Login and check cookies
curl -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Use cookies for authenticated request
curl -b cookies.txt http://localhost:8080/api/protected-endpoint
```

### Check Cookies in Browser
1. Open DevTools → Application → Cookies
2. Look for `accessToken` and `refreshToken`
3. Verify `HttpOnly` flag is checked ✅

## 📚 Response Examples

### Login Response (No Tokens!)
```json
{
  "email": "user@example.com",
  "username": "john_doe",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "CUSTOMER"
}
```

Cookies set in `Set-Cookie` headers (invisible to JavaScript).

### Error Response
```json
{
  "message": "Invalid credentials. Please check your email and password."
}
```

## 🎉 Benefits Summary

| Feature | Before (Header) | After (Cookie) |
|---------|----------------|----------------|
| Token Storage | localStorage | HttpOnly Cookie |
| XSS Vulnerable | ✅ Yes | ❌ No |
| Manual Header | ✅ Required | ❌ Not needed |
| Auto-Send | ❌ No | ✅ Yes |
| CSRF Protection | ✅ Good | ⚠️ Need SameSite |
| Code Complexity | High | Low |

---

**Questions?** Check the source code in:
- `JwtFilter.java` - Cookie reading logic
- `AuthController.java` - Cookie setting logic
- `WebConfig.java` - CORS configuration
