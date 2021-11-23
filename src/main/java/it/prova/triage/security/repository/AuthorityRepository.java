package it.prova.triage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.prova.triage.model.Authority;
import it.prova.triage.model.AuthorityName;



public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	Authority findByName(AuthorityName name);

}