package it.prova.triage.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.prova.triage.exceptions.UserAlreadyDisabledException;
import it.prova.triage.exceptions.UserNotFoundException;
import it.prova.triage.model.User;
import it.prova.triage.model.StatoUtente;
import it.prova.triage.service.UserService;

public class UserRestController {

	@Autowired
	UserService userService;

	@GetMapping("/{idInput}")
	public User getUtente(@PathVariable(required = true) Long idInput) {

		if (userService.get(idInput) == null)
			throw new UserNotFoundException("User non presente");

		return userService.get(idInput);
	}

	@GetMapping
	public List<User> getAll() {
		return userService.listAll();
	}

	@PostMapping("/search")
	public ResponseEntity<Page<User>> searchAndPagination(@RequestBody User userExample,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {

		Page<User> results = userService.searchAndPaginate(userExample, pageNo, pageSize, sortBy);

		return new ResponseEntity<Page<User>>(results, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User createNewUser(@RequestBody User userInput) {

		if (!userInput.getUsername().isEmpty())
			throw new RuntimeException("Non è ammesso fornire un id per la creazione");

		userInput.setStato(StatoUtente.CREATO);
		return userService.save(userInput);
	}

	@PutMapping("/{id}")
	public User updateUser(@RequestBody User userInput, @PathVariable Long id) {

		User userToUpdate = userService.get(id);

		if (userToUpdate == null)
			throw new UserNotFoundException("User non presente");

		userToUpdate.setUsername(userInput.getUsername());
		userToUpdate.setEmail(userInput.getEmail());
		userToUpdate.setStato(userInput.getStato());
		return userService.save(userToUpdate);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable(required = true) Long id) {

		if (!userService.get(id).getStato().equals(StatoUtente.DISABILITATO))
			throw new UserAlreadyDisabledException("User già disabilitato");

		if (userService.get(id) == null)
			throw new UserNotFoundException("User non presente");

		userService.get(id).setStato(StatoUtente.DISABILITATO);
	}
}