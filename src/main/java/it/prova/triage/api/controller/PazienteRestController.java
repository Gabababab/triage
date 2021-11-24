package it.prova.triage.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage.dto.PazienteDTO;
import it.prova.triage.exceptions.PazienteNotDimessoException;
import it.prova.triage.exceptions.PazienteNotFoundException;
import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;
import it.prova.triage.service.DottoreService;
import it.prova.triage.service.PazienteService;

@RestController
@RequestMapping(value = "/api/paziente", produces = { MediaType.APPLICATION_JSON_VALUE })
public class PazienteRestController {

	@Autowired
	PazienteService pazienteService;
	
	@Autowired
	DottoreService dottoreService;
	
	@GetMapping("/{idInput}")
	public Paziente getPaziente(@PathVariable(required = true) Long idInput) {
		return pazienteService.get(idInput);
	}
	
	@GetMapping
	public List<Paziente> getAll() {
		return pazienteService.listAll();
	}

	@PostMapping("/search")
	public ResponseEntity<Page<Paziente>> searchAndPagination(@RequestBody Paziente pazienteExample,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {

		Page<Paziente> results = pazienteService.searchAndPaginate(pazienteExample, pageNo, pageSize, sortBy);

		return new ResponseEntity<Page<Paziente>>(results, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PazienteDTO createNewPaziente(@RequestBody PazienteDTO pazienteInput) {
		
		if (pazienteInput.getId() != null)
			throw new RuntimeException("Non è ammesso fornire un id per la creazione");
		
		
		pazienteInput.setStato(StatoPaziente.IN_ATTESA_VISITA);
		Paziente pazienteInserito=pazienteService.save(pazienteInput.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteInserito);
	}

	@PutMapping("/{id}")
	public Paziente updatePaziente(@RequestBody Paziente pazienteInput, @PathVariable Long id) {
		
		Paziente pazienteToUpdate = pazienteService.get(id);
		
		if(pazienteToUpdate == null)
			throw new PazienteNotFoundException("Paziente non presente");
		
		if(pazienteInput.getCodiceFiscale()!=null)
			pazienteToUpdate.setCodiceFiscale(pazienteInput.getCodiceFiscale());
		if(pazienteInput.getNome()!=null)
			pazienteToUpdate.setNome(pazienteInput.getNome());
		if(pazienteInput.getCognome()!=null)
			pazienteToUpdate.setCognome(pazienteInput.getCognome());
		if(pazienteInput.getStato()!=null)
			pazienteToUpdate.setStato(pazienteInput.getStato());
		return pazienteService.save(pazienteToUpdate);
	}

	@DeleteMapping("/{id}")
	public void deletePaziente(@PathVariable(required = true) Long id) {
		
		if(!pazienteService.get(id).getStato().equals(StatoPaziente.DIMESSO))
			throw new PazienteNotDimessoException("Paziente non dimesso");
			
		pazienteService.delete(pazienteService.get(id));
	}
}