package it.prova.triage.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.prova.triage.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User>{

}