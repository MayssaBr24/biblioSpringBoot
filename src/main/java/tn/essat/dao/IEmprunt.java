package tn.essat.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.essat.model.Emprunt;
import tn.essat.model.Livre;
import tn.essat.model.User;


@Repository
public interface IEmprunt extends JpaRepository<Emprunt, Integer> {
    List<Emprunt> findByUser(User user);
    List<Emprunt> findByLivre(Livre livre);
    List<Emprunt> findByRendu(boolean rendu);
    List<Emprunt> findByDateEmpruntBetween(LocalDate start, LocalDate end);
}
