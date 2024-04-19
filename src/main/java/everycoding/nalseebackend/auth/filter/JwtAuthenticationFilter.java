package everycoding.nalseebackend.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.auth.customUser.CustomUserDetailsService;
import everycoding.nalseebackend.auth.dto.request.LoginRequestDto;
import everycoding.nalseebackend.auth.dto.response.UserDto;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.sse.UserStatusController;
import everycoding.nalseebackend.user.repository.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationFilter authenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserStatusController userStatusController;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            String url, UserStatusController userStatusController) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.userStatusController = userStatusController;
        setFilterProcessesUrl(url);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        log.info("loginRequestDto initialize");

        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("LoginRequestDto insert request");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 사용자의 로그인 정보를 기반으로 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword());
        log.info("UsernamePasswordAuthenticationToken Complete");
        try {
            log.info("Try...");
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.info("Something Wrong");
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("Credential Login Success!");
        //토큰 발급 시작
        String accessToken = jwtTokenProvider.createAccessToken(authResult);
        String refreshToken = jwtTokenProvider.createRefreshToken(authResult);
        log.info(accessToken);
        log.info(refreshToken);
        ObjectMapper om = new ObjectMapper();

        //        Cookie accessTokenCookie = new Cookie("AccessToken", accessToken);
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge(60*60);
//        // SameSite 속성을 쿠키 문자열에 직접 추가
//        String accessTokenCookieString = "AccessToken=" + accessToken + "; Path=/; HttpOnly; Max-Age=3600; Secure = true; SameSite=None";
//        response.addCookie(accessTokenCookie);
//        response.addHeader("Set-Cookie", accessTokenCookieString);

        ResponseCookie accessTokenCookie = ResponseCookie.from("AccessToken", accessToken)
                .path("/").sameSite("None").httpOnly(false).secure(true).maxAge(60*60).build();
        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        log.info("AccessToken in Cookie={}", accessToken);

//        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
//        String refreshTokenCookieString = "RefreshToken=" + refreshToken + "; Path=/; HttpOnly; Max-Age=" + (60 * 60 * 24 * 7) + "; Secure = true; SameSite=None";
//        response.addHeader("Set-Cookie", refreshTokenCookieString);
//        response.addCookie(refreshTokenCookie);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("RefreshToken", refreshToken)
                .path("/").sameSite("None").httpOnly(false).secure(true).maxAge(60*60).build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        String role = authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).findAny().orElse("");
        String userEmail = "";
        if (role.equals("ROLE_USER")) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
            userEmail = customUserDetails.getUsername();
        }
        User user = customUserDetailsService.selcetUser(userEmail);
        user.setRefreshToken(refreshToken);
        userStatusController.updateUserStatus(user.getId(), true);
        userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getId());
        userDto.setAccessToken("Bearer " + accessToken);
        userDto.setRefreshToken(user.getRefreshToken());
        log.info("Response Body insert User");
        String result = om.registerModule(new JavaTimeModule()).writeValueAsString(userDto);
        response.getWriter().write(result);
    }
}
