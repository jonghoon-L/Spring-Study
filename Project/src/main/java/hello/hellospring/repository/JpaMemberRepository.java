package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import java.util.List;
import jakarta.persistence.EntityManager;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class JpaMemberRepository implements MemberRepository {

    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        List<Member> result = em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByEmailAndPassword(String email, String password) {
        List<Member> result = em.createQuery("select m from Member m where m.email = :email and m.password = :password", Member.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Member> findAll() {
       return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    @Override
    public void delete(Member member) {
        em.remove(em.contains(member) ? member : em.merge(member));
    }

}
