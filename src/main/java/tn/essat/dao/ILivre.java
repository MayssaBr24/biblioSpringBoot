package tn.essat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import tn.essat.model.Categorie;
import tn.essat.model.Livre;

@Repository
public interface ILivre extends JpaRepository<Livre, Integer> {

    List<Livre> findAllByCategorie_Id(Integer categorieId);    List<Livre> findByDisponiblesGreaterThan(Integer disponibles);

    List<Livre> findByTitreContainingOrAuteurContaining(String titre, String auteur);}
