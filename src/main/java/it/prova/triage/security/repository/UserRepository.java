package it.prova.triage.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.prova.triage.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

}