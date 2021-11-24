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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage.dto.DottoreDTO;
import it.prova.triage.dto.DottoreResponseDTO;
import it.prova.triage.dto.PazienteDTO;
import it.prova.triage.exceptions.PazienteNotFoundException;
import it.prova.triage.model.Dottore;
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
	
	@Autowired
	private WebClient webClient;
	
	@GetMapping("/{idInput}")
	public Paziente getPaziente(@PathVariable(required = true) Long idInput) {
		return pazienteService.get(idInput);
	}
	
	@GetMapping("/verificapaziente/{idInput}")
	public PazienteDTO verificaPaziente(@PathVariable(value="id", required = true) Long idInput) {
		
		Paziente pazienteModel = pazienteService.get(idInput);

		// ora invoco il sistema esterno per capire se il dipendente ha una posizione
		// previdenziale
		// nel caso affermativo valorizzo apposito campo
		// il block significa agire in maniera sincrona, attendendo la risposta
		DottoreResponseDTO dottoreResponseDTO = webClient.get()
				.uri("/" + pazienteModel.getId())
				.retrieve()
				.bodyToMono(DottoreResponseDTO.class)
				.block();

		PazienteDTO result = PazienteDTO.buildPazienteDTOFromModel(pazienteModel);

		if (dottoreResponseDTO != null
				&& dottoreResponseDTO.getPazienteAttualmenteInVisita().getId().equals(result.getId()))
			result.setDottore(DottoreDTO.buildDottoreDTOFromModel(dottoreService.get(dottoreResponseDTO.getId())));

		return result;
		
//		return pazienteService.get(idInput);
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
	public Paziente createNewPaziente(@RequestBody Paziente pazienteInput) {
		pazienteInput.setStato(StatoPaziente.IN_ATTESA_VISITA);
		return pazienteService.save(pazienteInput);
	}

	@PutMapping("/{id}")
	public Paziente updatePaziente(@RequestBody Paziente pazienteInput, @PathVariable Long id) {
		
		Paziente pazienteToUpdate = pazienteService.get(id);
		
		if(pazienteToUpdate == null)
			throw new PazienteNotFoundException("Paziente non presente");
		
		pazienteToUpdate.setCodiceFiscale(pazienteInput.getCodiceFiscale());
		pazienteToUpdate.setNome(pazienteInput.getNome());
		pazienteToUpdate.setCognome(pazienteInput.getCognome());
		pazienteToUpdate.setDottore(pazienteInput.getDottore());
		pazienteToUpdate.setStato(pazienteInput.getStato());
		return pazienteService.save(pazienteToUpdate);
	}

	@DeleteMapping("/{id}")
	public void deletePaziente(@PathVariable(required = true) Long id) {
		if(!pazienteService.get(id).getStato().equals(StatoPaziente.DIMESSO))
			throw new PazienteNotFoundException("Paziente non dimesso");
			
		pazienteService.delete(pazienteService.get(id));
	}
}