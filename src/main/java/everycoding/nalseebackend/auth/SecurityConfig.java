package everycoding.nalseebackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.auth.customUser.CustomUserDetailsService;
import everycoding.nalseebackend.auth.dto.response.UserDto;
import everycoding.nalseebackend.auth.filter.JwtAuthenticationFilter;
import everycoding.nalseebackend.auth.filter.JwtAuthorizationFilter;
import everycoding.nalseebackend.auth.handler.CustomLogoutSuccessHandler;
import everycoding.nalseebackend.auth.jwt.JwtAccessDeniedHandler;
import everycoding.nalseebackend.auth.jwt.JwtAuthenticationEntryPoint;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.auth.oauth2.CustomOAuth2UserService;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    //Spring Security에서 제공하는 클래스, 비밀번호를 안전하게 해싱
    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder();}

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // 스프링 시큐리티의 HTTP 보안 설정에서 CSRF 보호기능 비활성화 (Cross-Site Request Forgery, 사이트 간 요청 위조)
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화 : API 서버와 같이 폼 로그인이 필요없는 방식) 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource())) // (Cross-Origin Resource Sharing) 웹 앱의 보안을 유지하면서 다른 출처의 리소스 요청을 허용하도록 설정
                .sessionManagement(configure -> configure.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 서버가 사용자 세션을 유지하지 않음. 서버의 확장성을 높이고 client와 server 간의 결합도를 낮춘다.
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                                .defaultSuccessUrl("/index", true)// -> 이게 백엔드한테 리다이렉트하는 그러니까 엠브이씨할때 이렇게 엔드포인트 잡아요 타임리프같은거 그니까 이건 필요업성요
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler(jwtTokenProvider, customUserDetailsService))
                        .deleteCookies("RefreshToken","AccessToken")
                        .permitAll()
                )
                .addFilter(new JwtAuthenticationFilter(jwtTokenProvider, userRepository, authenticationManager(customUserDetailsService), customUserDetailsService, "/api/auth"))
                .addFilterAfter(new JwtAuthorizationFilter(userRepository, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
//                .exceptionHandling(configurer -> configurer
//                        .accessDeniedHandler(new JwtAccessDeniedHandler())
//                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
//                );
        return httpSecurity.build();
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth Login Success!");
        //토큰 발급 시작
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        log.info("test={}", authentication.getName());
        log.info("test={}", authentication.getAuthorities());
        log.info("test={}", authentication.getDetails());
        log.info("test={}", authentication.getClass());
        log.info("test={}", authentication.getPrincipal());

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

        log.info("RefreshToken in Cookie={}", refreshToken);

        String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findAny().orElse("");
        String userEmail = "";
        if(role.equals("ROLE_USER")){
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            userEmail = customUserDetails.getUsername();
        }
        User user = customUserDetailsService.selcetUser(userEmail);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getId());
        userDto.setAccessToken("Bearer " + accessToken);
        userDto.setRefreshToken(user.getRefreshToken());
        log.info("Response Body insert User");
        String result = om.registerModule(new JavaTimeModule()).writeValueAsString(userDto);
        response.getWriter().write(result); //body
//        response.sendRedirect("http://localhost:5173/oauth2/redirect");
//        response.sendRedirect("https://k547f55f71a44a.user-app.krampoline.com/oauth2/redirect/?token="+token);
        response.sendRedirect("https://app.nalsee.site");
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("*");
        corsConfiguration.setAllowCredentials(true);

//      corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "https://ide-frontend-wheat.vercel.app/login", "https://ide-frontend-six.vercel.app", "https://ide-frontend-wheat.vercel.app"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "https://app.nalsee.site","https://nalsee.site"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}