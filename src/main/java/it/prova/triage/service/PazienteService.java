package it.prova.triage.service;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.triage.model.Paziente;

public interface PazienteService {

	Page<Paziente> searchAndPaginate(Paziente pazienteExample, Integer pageNo, Integer pageSize, String sortBy);

	List<Paziente> listAll();

	Paziente get(Long idInput);
	
	Paziente save(Paziente input);
	
	void delete(Paziente input);
}
