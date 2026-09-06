# 🔐 JWT + Spring Security — Revision Notes
> Based on your **SecurityApp** project code

---

## 🗺️ Full JWT Flow (Request Lifecycle)

```
[Client]
   |
   |──── POST /auth/signup ────────────────────────────────────▶ UserService.signup()
   |                                                              • ModelMapper maps SignupDTO → User
   |                                                              • passwordEncoder.encode(password) ← BCrypt
   |                                                              • userRepository.save(user)
   |
   |──── POST /auth/login ─────────────────────────────────────▶ AuthService / AuthController
   |                                                              • AuthenticationManager.authenticate()
   |                                                              • Internally calls loadUserByUsername()
   |                                                              • If valid → JwtService.generateToken(user)
   |◀─────────────────────────────────────────── returns JWT ───
   |
   |──── GET /posts/** ──── Header: Authorization: Bearer <jwt>
   |         |
   |         ▼
   |   JwtAuthFilter.doFilterInternal()        ← runs on EVERY request
   |         |
   |         ├── reads "Authorization" header
   |         ├── checks starts with "Bearer"
   |         ├── token = header.substring(7).trim()   ← strips "Bearer "
   |         ├── JwtService.getUserIdFromToken(token) ← parses & verifies JWT
   |         ├── UserService.getUserById(userId)       ← loads User from DB
   |         ├── sets SecurityContextHolder auth       ← marks user as authenticated
   |         └── filterChain.doFilter()               ← passes to next filter / controller
```

---

## 🧩 Key Classes & Their Role

### 1. `JwtService.java` — Token Generator & Parser
```java
// Generates a signed JWT for the user
generateToken(User user)
    • Jwts.builder()
    • .subject(user.getId().toString())   ← user ID in subject
    • .claim("email", user.getEmail())    ← extra claims
    • .expiration(new Date(...))           ← token expiry
    • .signWith(getSecretKey())            ← HMAC-SHA signing
    • .compact()                           ← returns JWT string

// Parses JWT and extracts userId
getUserIdFromToken(String token)
    • Jwts.parser().verifyWith(secretKey).build()
    • .parseSignedClaims(token)
    • .getPayload().getSubject()           ← gets userId back
```
> ⚠️ **Key Bug Fixed**: `split("Bearer")[1]` left a leading space → JJWT crash.
> ✅ **Fix**: `substring(7).trim()` cleanly strips `"Bearer "`.

---

### 2. `JwtAuthFilter.java` — The Gatekeeper (runs every request)
```java
extends OncePerRequestFilter   // runs exactly once per request

doFilterInternal(request, response, filterChain)
    1. Read header:   request.getHeader("Authorization")
    2. Guard clause:  if null or not "Bearer" → skip (permitAll routes go through)
    3. Extract token: header.substring(7).trim()
    4. Get userId:    jwtService.getUserIdFromToken(token)
    5. Load user:     userService.getUserById(userId)
    6. Set auth:      SecurityContextHolder.getContext().setAuthentication(...)
    7. Continue:      filterChain.doFilter(request, response)
```
> 🔑 **Why `SecurityContextHolder`?** — Spring Security reads auth from here for every request.
> Once set, `@PreAuthorize`, `hasRole()` etc. all work automatically.

---

### 3. `UserService.java` — Core User Logic
```java
// Spring Security calls this during login to load user
loadUserByUsername(String email)   // implements UserDetailsService
    • userRepository.findByEmail(email)
    • throws UsernameNotFoundException if not found

// Signup — with BCrypt encoding ✅
signup(SignupDTO signupDTO)
    • modelMapper.map(signupDTO, User.class)
    • passwordEncoder.encode(password)    ← BCrypt hashes it
    • userRepository.save(user)

// Used by JwtAuthFilter after token parse
getUserById(Long userId)
    • userRepository.findById(userId)
```

---

### 4. `WebSecurityConfig.java` — Security Rules
```java
SecurityFilterChain securityFilterChain(HttpSecurity http)
    • .requestMatchers("/auth/**").permitAll()   ← no token needed
    • .anyRequest().authenticated()              ← everything else needs JWT
    • .csrf().disable()                          ← REST APIs don't need CSRF
    • .sessionManagement(STATELESS)              ← no sessions, JWT handles state
    • .addFilterBefore(jwtAuthFilter,            ← run JWT filter BEFORE Spring's
         UsernamePasswordAuthenticationFilter)      default login filter

AuthenticationManager bean
    • config.getAuthenticationManager()
    • Used in login flow to verify credentials

PasswordEncoder bean
    • new BCryptPasswordEncoder()               ← used in UserService.signup()
```

---

### 5. `User.java` — Entity + UserDetails
```java
implements UserDetails   // Spring Security interface

getUsername()  →  returns email   // Spring uses this for authentication
getPassword()  →  returns password (BCrypt hash from DB)
getAuthorities() →  List.of()     // no roles yet (empty)
```
> ⚠️ Spring Security compares the **stored BCrypt hash** with the **raw password** on login.
> That's why `passwordEncoder.encode()` in signup is **critical**.

---

## 🔑 Secret Key Flow
```
application.properties:  jwt.secretKey=your-secret-here

JwtService:
    @Value("${jwt.secretKey}") String jwtSecretKey
    
    getSecretKey()
        → Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8))
        → returns SecretKey for signing & verifying tokens
```

---

## ⚡ Quick Bug Checklist
| Mistake | Effect | Fix |
|--------|--------|-----|
| `split("Bearer")[1]` | leading space → JJWT crash | `substring(7).trim()` |
| Wrong return type in `getUserById` | compile error | return `User`, not `Long` |
| Two `UserDetailsService` beans | Spring uses wrong one | keep only `UserService` impl |
| No `PasswordEncoder` → password stored as plain text | security risk | always `encode()` before save |
| Filter not added in `SecurityFilterChain` | JWT never checked | `.addFilterBefore(jwtAuthFilter, ...)` |

---

## 📦 Dependency Roles (pom.xml)
| Dependency | Purpose |
|-----------|---------|
| `spring-boot-starter-security` | Auth, filters, `SecurityFilterChain` |
| `spring-boot-starter-data-jpa` | `UserRepository`, DB access |
| `jjwt-api` + `jjwt-impl` + `jjwt-jackson` | JWT generation & parsing |
| `modelmapper` | `SignupDTO` → `User` mapping |
| `lombok` | `@RequiredArgsConstructor`, `@Data`, `@Builder` |
