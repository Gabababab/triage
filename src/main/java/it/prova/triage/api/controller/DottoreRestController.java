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

import it.prova.triage.dto.DottoreDTO;
import it.prova.triage.dto.DottoreRequestDTO;
import it.prova.triage.dto.DottoreResponseDTO;
import it.prova.triage.exceptions.DottoreNotFoundException;
import it.prova.triage.exceptions.DottoreNotDisponibileException;
import it.prova.triage.exceptions.PazienteNotFoundException;
import it.prova.triage.model.Dottore;
import it.prova.triage.model.Paziente;
import it.prova.triage.service.DottoreService;
import it.prova.triage.service.PazienteService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/dottore", produces = { MediaType.APPLICATION_JSON_VALUE })
public class DottoreRestController {

	@Autowired
	private DottoreService dottoreService;

	@Autowired
	private PazienteService pazienteService;

	@Autowired
	private WebClient webClient;

	@GetMapping("/{idInput}")
	public Dottore getDottore(@PathVariable(required = true) Long idInput) {
		return dottoreService.get(idInput);
	}

	@GetMapping
	public List<Dottore> getAll() {
		return dottoreService.listAll();
	}

	@PostMapping("/search")
	public ResponseEntity<Page<Dottore>> searchAndPagination(@RequestBody Dottore dottoreExample,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {

		Page<Dottore> results = dottoreService.searchAndPaginate(dottoreExample, pageNo, pageSize, sortBy);

		return new ResponseEntity<Page<Dottore>>(results, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping("/assegnapaziente")
	public void assegnaPazienteADottore(@RequestParam String codiceFiscale, @RequestParam String codiceDipendente) {

		Paziente pazienteAssegnazione = pazienteService.findByCodiceFiscale(codiceFiscale);
		Dottore dottoreAssegnazione=dottoreService.findByCodice(codiceDipendente);
		
		if(pazienteAssegnazione==null)
			throw new PazienteNotFoundException("Paziente non trovato");
		if(dottoreAssegnazione==null)
			throw new DottoreNotFoundException("Dottore non trovato");
		
		ResponseEntity<DottoreResponseDTO> response = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/verifica/{codiceDipendente}").build(codiceDipendente)).retrieve()
				.toEntity(DottoreResponseDTO.class).block();

		DottoreResponseDTO dottoreResponse = response.getBody();
		if (!dottoreResponse.isInServizio() || dottoreResponse.isInVisita())
			throw new DottoreNotDisponibileException("dottore non disponibil");

		ResponseEntity<DottoreResponseDTO> responseModifica = webClient.post().uri("/impostaInVisita")
				.body(Mono.just(new DottoreRequestDTO(dottoreAssegnazione.getCodiceDipendente())), DottoreRequestDTO.class)
				.retrieve().toEntity(DottoreResponseDTO.class).block();

		if (responseModifica.getStatusCode() != HttpStatus.OK)
			throw new RuntimeException("Errore in verifica");

		pazienteAssegnazione.setDottore(dottoreAssegnazione);
		dottoreAssegnazione.setPazienteAttualmenteInVisita(pazienteAssegnazione);
		pazienteService.save(pazienteAssegnazione);
		dottoreService.save(dottoreAssegnazione);

		return;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DottoreDTO createNewDottore(@RequestBody DottoreDTO dottoreInput) {

		ResponseEntity<DottoreResponseDTO> response = webClient.post().uri("")
				.body(Mono.just(new DottoreRequestDTO(dottoreInput.getNome(), dottoreInput.getCognome(),
						dottoreInput.getCodiceDipendente())), DottoreRequestDTO.class)
				.retrieve().toEntity(DottoreResponseDTO.class).block();

		if (response.getStatusCode() != HttpStatus.CREATED)
			throw new RuntimeException("Errore nella creazione della nuova voce tramite api esterna!!!");

		Dottore dottoreInserito = dottoreService.save(dottoreInput.buildDottoreModel());
		return DottoreDTO.buildDottoreDTOFromModel(dottoreInserito);

//		return dottoreService.save(dottoreInput);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Dottore updateDottore(@RequestBody Dottore dottoreInput, @PathVariable Long id) {

		Dottore dottoreToUpdate = dottoreService.get(id);

		if (dottoreInput.getNome() != null)
			dottoreToUpdate.setNome(dottoreInput.getNome());
		if (dottoreInput.getCognome() != null)
			dottoreToUpdate.setCognome(dottoreInput.getCognome());
		if (dottoreInput.getCodiceDipendente() != null)
			dottoreToUpdate.setCodiceDipendente(dottoreInput.getCodiceDipendente());

		return dottoreService.save(dottoreToUpdate);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteDottore(@PathVariable(required = true) Long id) {
		dottoreService.delete(dottoreService.get(id));
	}

}
