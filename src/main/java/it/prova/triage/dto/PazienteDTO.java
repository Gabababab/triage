package it.prova.triage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;

public class PazienteDTO {

	private Long id;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private StatoPaziente stato;
	@JsonIgnoreProperties(value = { "pazienteAttualmenteInVisita" })
	private DottoreDTO dottore;

	public PazienteDTO() {
		super();
	}

	public PazienteDTO(Long id, String nome, String cognome, String codiceFiscale, DottoreDTO dottore) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.dottore = dottore;
	}

	public PazienteDTO(Long id, String nome, String cognome, String codiceFiscale, StatoPaziente stato,
			DottoreDTO dottore) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.stato = stato;
		this.dottore = dottore;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public StatoPaziente getStato() {
		return stato;
	}

	public void setStato(StatoPaziente stato) {
		this.stato = stato;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public DottoreDTO getDottore() {
		return dottore;
	}

	public void setDottore(DottoreDTO dottore) {
		this.dottore = dottore;
	}

	public Paziente buildPazienteModel() {
		return new Paziente(this.id, this.nome, this.cognome, this.codiceFiscale, this.stato,
				this.dottore.buildDottoreModel());
	}

	public static PazienteDTO buildPazienteDTOFromModel(Paziente input) {
		return new PazienteDTO(input.getId(), input.getNome(), input.getCognome(), input.getCodiceFiscale(),
				input.getStato(), DottoreDTO.buildDottoreDTOFromModel(input.getDottore()));
	}

}
