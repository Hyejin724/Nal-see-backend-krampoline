package everycoding.nalseebackend.auth.handler;

import everycoding.nalseebackend.auth.customUser.CustomUserDetailsService;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AccessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue().replace("Bearer ", "");
                    break;
                }
            }
        }
            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                log.info("자 인자 토큰으로 아이디 찾는다={}", accessToken);
                String email = jwtTokenProvider.getClaims(accessToken.substring(7)).getSubject();
                log.info("내가 안뜨면 토큰이 문제여");
                userService.clearRefreshToken(email);
            }

        ResponseCookie refreshTokenCookie = ResponseCookie.from("RefreshToken", null)
                .path("/").sameSite("None").httpOnly(false).secure(true).maxAge(0).build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        ResponseCookie accessTokenCookie =
                ResponseCookie
                        .from("AccessToken", null)
                        .path("/")
                        .sameSite("None")
                        .httpOnly(false)
                        .secure(true)
                        .maxAge(0)
                        .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

//            Cookie cookie = new Cookie("RefreshToken", null);
//            cookie.setPath("/");
//            cookie.setMaxAge(0);
//            cookie.setHttpOnly(true);
//            response.addCookie(cookie);

            log.info("Success logout");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().flush();
        }

}
