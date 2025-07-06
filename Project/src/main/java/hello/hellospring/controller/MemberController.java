package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
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

    // 로그인 처리하는 메서드
    @PostMapping("/members/login")
    public String login(String email, String password, Model model) {
        Member member = memberService.findByEmailAndPassword(email, password);

        if (member != null) {
            // 로그인 성공 시, 로그인한 사용자 정보를 보여주거나 다른 작업을 처리
            model.addAttribute("member", member);
            return "members/memberHome";  // 로그인 후 사용자 홈 화면
        } else {
            // 로그인 실패 시, 다시 로그인 폼을 보여줌
            model.addAttribute("error", "잘못된 이메일 또는 비밀번호입니다.");
            return "members/loginForm";  // 로그인 실패 시 로그인 폼으로 돌아감
        }
    }
}
