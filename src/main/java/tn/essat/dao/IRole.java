package tn.essat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.essat.model.Role;

import java.util.Optional;

@Repository
public interface IRole extends JpaRepository<Role, Integer> {

    Optional<Role> findByRole(String userRole);
}
