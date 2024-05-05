package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;


import java.util.List;
import java.util.Optional;
@Repository
public class TeamJpaRepository {
    @PersistenceContext
    private EntityManager em;

    public Team save(Team team){
        em.persist(team);
        return team;
    }
    public void delete(Team team){
        em.remove(team);
    }

    public List<Team> findAll(){
        return em.createQuery("select m from Team m", Team.class)
                .getResultList();
    }

    public Long count(){
        return em.createQuery("select count(m) from Team m", Long.class)
                .getSingleResult();
    }

    public Optional<Team> findById(Long id){
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public Team find(Long id){
        return em.find(Team.class, id);
    }
}
