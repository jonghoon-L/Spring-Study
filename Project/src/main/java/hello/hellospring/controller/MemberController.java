package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import hello.hellospring.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    public MemberController(MemberService memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    // 회원 가입 폼을 보여주는 페이지
    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    // 회원 가입 처리하는 메서드
    @PostMapping("/members/new")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());

        memberService.join(member);

        return "redirect:/";
    }

    // 회원 목록을 보여주는 페이지 및 메서드
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

    // 로그인 폼을 보여주는 페이지
    @GetMapping("/members/login")
    public String loginForm() {
        return "members/loginForm";  
    }

    // 로그인 처리하는 메서드 -> req, res 형식으로 수정
    @PostMapping("/members/login")
    public ResponseEntity<?> login(@RequestBody Member loginRequest) {
        try {
            // email과 password를 DTO에서 가져옴
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            // 이메일과 비밀번호로 회원 조회
            Member member = memberService.findByEmailAndPassword(email, password);

            // 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            // 응답을 JSON 형식으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("member", member);

            return ResponseEntity.ok(response);  // 200 OK와 함께 JSON 응답 반환
        } catch (IllegalStateException e) {
            // 오류가 발생하면 400 Bad Request와 함께 오류 메시지 반환
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    // 로그아웃 처리하는 메서드
    @PostMapping("/members/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization, HttpServletRequest request) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.replace("Bearer ", ""); // "Bearer "를 제거하고 실제 토큰만 받기

            logger.info("로그아웃 요청 - 토큰: {}", token);
            memberService.logout(request); // 세션 무효화 처리
            return ResponseEntity.ok("로그아웃 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 토큰 형식입니다.");
        }
    }

    @PostMapping("/members/delete")
    public ResponseEntity<?> deleteMember(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> requestBody
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 토큰 형식입니다.");
        }

        String token = authorization.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String password = requestBody.get("password");

        try {
            memberService.deleteByEmailAndPassword(email, password);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴에 실패했습니다.");
        }
    }

    @PostMapping("/members/update")
    public ResponseEntity<?> updateMember(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Member updateRequest
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 토큰 형식입니다.");
        }

        String token = authorization.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token); // 이메일은 토큰에서 추출

        try {
            memberService.updateMemberByEmail(email, updateRequest);
            return ResponseEntity.ok("회원 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 수정에 실패했습니다.");
        }
    }



}
