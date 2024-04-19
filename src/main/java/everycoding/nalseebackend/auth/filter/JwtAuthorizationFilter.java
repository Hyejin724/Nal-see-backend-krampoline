package everycoding.nalseebackend.auth.filter;


import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.sse.UserStatusController;
import everycoding.nalseebackend.user.repository.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserStatusController userStatusController;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String accessToken = null;
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("RefreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                } else if ("AccessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }

        boolean tokenProcessed = false;
        if(refreshToken != null) {
            String tokenValidationResult = jwtTokenProvider.validateToken(refreshToken.replace("Bearer ", ""));
            if ("token expired".equals(tokenValidationResult)) {
                // 액세스 토큰이 만료된 경우, 로그를 기록하고 401 에러를 반환합니다.
                log.info("AccessToken expired");
                addCookie(response, "RefreshToken", null, 0); // 리프레시 토큰 쿠키 삭제
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Refresh Token expired. Please log in again.");
                // 기존 액세스 토큰 쿠키를 삭제하기 위해 Max-Age를 0으로 설정한 쿠키를 생성하고 응답에 추가합니다.
                return; // 요청 처리 중단
            }
        }

        if (accessToken != null) {
            String tokenValidationResult = jwtTokenProvider.validateToken(accessToken.replace("Bearer ", ""));
            if ("Success".equals(tokenValidationResult)) {
                // 토큰이 유효한 경우 사용자 인증 처리
                Authentication authentication = getUsernamePasswordAuthentication(accessToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    tokenProcessed = true;
                }
                Claims claims = jwtTokenProvider.getClaims(accessToken);
                String subject = claims.getSubject();
                Optional<User> byEmail = userRepository.findByEmail(subject);
                User user = byEmail.orElseThrow();
                Long id = user.getId();
                userStatusController.updateUserStatus(id, true);

            } else if ("token expired".equals(tokenValidationResult)) {
                // 액세스 토큰이 만료된 경우, 로그를 기록하고 401 에러를 반환합니다.
                log.info("AccessToken expired");
                Claims claims = jwtTokenProvider.getClaims(accessToken);
                String subject = claims.getSubject();
                Optional<User> byEmail = userRepository.findByEmail(subject);
                User user = byEmail.orElseThrow();
                Long id = user.getId();
                userStatusController.updateUserStatus(id, false);
                deleteCookie(response, "AccessToken");
                // 기존 액세스 토큰 쿠키를 삭제하기 위해 Max-Age를 0으로 설정한 쿠키를 생성하고 응답에 추가합니다.
                return; // 요청 처리 중단
            }
        }

        if (!tokenProcessed && refreshToken != null) {
            // 액세스 토큰이 처리되지 않았고, 유효한 리프레시 토큰이 있는 경우 새로운 액세스 토큰 발급
            String tokenValidationResult = jwtTokenProvider.validateToken(refreshToken.replace("Bearer ", ""));
            if ("Success".equals(tokenValidationResult)) {
                String newAccessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);
                addCookie(response, "AccessToken", newAccessToken, 60*60);
                log.info("액세스 토큰이 처리되지 않았고, 유효한 리프레시 토큰이 있는 경우 새로운 액세스 토큰 발급");

                // 새로 발급된 액세스 토큰으로 사용자 인증 처리
                Authentication authentication = getUsernamePasswordAuthentication(newAccessToken.replace("Bearer ", ""));
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("새로 발급된 액세스 토큰으로 사용자 인증 처리");
                }
            }
        }
        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(String token) {
        Claims claims = jwtTokenProvider.getClaims(token);
        String email = claims.getSubject();

        if (email != null) {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                CustomUserDetails customUserDetails = CustomUserDetails.create(user);
                return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            }
        }
        return null;
    }

    private void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {

        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .path("/").sameSite("None").httpOnly(false).secure(true).maxAge(maxAge).build();
        response.addHeader("Set-Cookie", cookie.toString());

    }

    private void deleteCookie(HttpServletResponse response, String cookieName) throws IOException{
        addCookie(response, cookieName, null, 0); //maxAge를 0으로 하여 쿠키를 삭제한다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "AccessToken expired");
    }
}