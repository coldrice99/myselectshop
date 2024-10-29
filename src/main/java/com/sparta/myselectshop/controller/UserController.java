package com.sparta.myselectshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.dto.UserInfoDto;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import com.sparta.myselectshop.service.KakaoService;
import com.sparta.myselectshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final FolderService folderService;
    private final KakaoService kakaoService;

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRoleEnum role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRoleEnum.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }

    // 회원별로 등록한 폴더들을 가져와서 index.html 쪽에 model을 통해 데이터를 전달.
    @GetMapping("/user-folder")
    public String getUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        model.addAttribute("folders", folderService.getFolders(userDetails.getUser()));

        return "index :: #fragment";
    }


    /*
       카카오 사용자 정보 가져오기 API :
       직접 쿠키를 만들어서 set
       카카오 서버에서 넘어오는 인가 코드를 @RequestParam으로 받는다.
       이전 회원 기능을 구현할 떄는 헤더에 jwt를 넣었는데 이번 클라이언트 구조상 직접 쿠키를 생성해서
       jwt를 넣은 다음에 브라우저에 자동으로 set 될 수 있게 구현 (HttpServlet)
     */
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // jwt 토큰
        String token = kakaoService.kakaoLogin(code);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7)); // bearer을 제외한 순수 토큰
        cookie.setPath("/");
        response.addCookie(cookie); // 브라우저의 jwt 값이 set

        return "redirect:/";
    }
}