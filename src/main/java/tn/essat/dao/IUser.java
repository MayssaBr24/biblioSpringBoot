package tn.essat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.essat.model.User;

import java.util.List;

@Repository
public interface IUser extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByNameContainingOrUsernameContaining(String name, String username);
    List<User> findByRole_Role(String role);
    long countByRole_Role(String role);
    long countByEnabled(boolean enabled);
}