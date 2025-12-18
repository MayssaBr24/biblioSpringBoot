package tn.essat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.essat.model.Categorie;

@Repository
public interface ICategorie extends JpaRepository<Categorie, Integer> {

}
