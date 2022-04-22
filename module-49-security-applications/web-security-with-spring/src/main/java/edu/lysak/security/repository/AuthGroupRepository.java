package edu.lysak.security.repository;

import edu.lysak.security.domain.AuthGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthGroupRepository extends JpaRepository<AuthGroup, Long> {
    List<AuthGroup> findByEmail(String email);
}
