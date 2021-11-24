package it.prova.triage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import it.prova.triage.model.User;

public interface UserService {
	
	List<User> listAll();

	Page<User> searchAndPaginate(User userExample, Integer pageNo, Integer pageSize, String sortBy);

	User get(Long idInput);

	User save(User input);

	void delete(User input);
	
	User findByUsername(String username);
}
