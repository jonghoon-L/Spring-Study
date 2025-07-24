package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    public Long join(Member member) {

        long start = System.currentTimeMillis(); // 시간 측정

        try {
            // 같은 이메일이 있는 중복 회원 x
            validateDuplicateMember(member); // 중복 회원 검증
            memberRepository.save(member);
            return member.getId();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("join = " + timeMs);
        }
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail())
            .ifPresent(m -> {
                throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

     /**
      * 로그인
      */ 
    public Member findByEmailAndPassword(String email, String password) {
        return memberRepository.findByEmailAndPassword(email, password)
            .orElseThrow(() -> new IllegalStateException("잘못된 이메일 또는 비밀번호입니다."));
    }

    /**
     * 로그아웃
     */
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("세션 {} 만료 처리 중", session.getId());  // 세션 ID 로그 출력
            session.invalidate();  // 세션 무효화
            logger.info("세션 {} 만료 완료", session.getId());  // 세션 만료 로그 출력
        } else {
            logger.warn("유효한 세션이 없습니다.");
        }
    }

    /**
     * 회원탈퇴
     */
    public void deleteByEmailAndPassword(String email, String password) {
        Member member = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        memberRepository.delete(member);
    }

    /**
     * 회원정보수정
     */
    public void updateMemberByEmail(String email, Member updateData) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (updateData.getName() != null && !updateData.getName().isBlank()) {
            member.setName(updateData.getName());
        }

        if (updateData.getPassword() != null && !updateData.getPassword().isBlank()) {
            member.setPassword(updateData.getPassword());
        }
    }

}
