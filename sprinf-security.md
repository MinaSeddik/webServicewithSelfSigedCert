

## Spring security and Session Management

- Session is created by application server **HTTP Session** (JSESSIONID)

```java
public void login(HttpServletRequest req, String user, String pass) {
    
    UsernamePasswordAuthenticationToken authReq
      = new UsernamePasswordAuthenticationToken(user, pass);
    Authentication auth = authManager.authenticate(authReq);
    
    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    HttpSession session = req.getSession(true);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
}
```