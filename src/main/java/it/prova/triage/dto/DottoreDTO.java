package it.prova.triage.dto;

import it.prova.triage.model.Dottore;
import it.prova.triage.model.Paziente;

public class DottoreDTO {

	private Long id;
	private String nome;
	private String cognome;
	private String codiceDipendente;
	private Paziente pazienteAttualmenteInVisita;

	public DottoreDTO() {
		super();
	}

	public DottoreDTO(Long id, String nome, String cognome, String codiceDipendente,
			Paziente pazienteAttualmenteInVisita) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceDipendente = codiceDipendente;
		this.pazienteAttualmenteInVisita = pazienteAttualmenteInVisita;
	}

	public DottoreDTO(Long id, String nome, String cognome, String codiceDipendente) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceDipendente = codiceDipendente;
	}

	public DottoreDTO(String nome, String cognome, String codiceDipendente, Paziente pazienteAttualmenteInVisita) {
		super();
		this.nome = nome;
		this.cognome = cognome;
		this.codiceDipendente = codiceDipendente;
		this.pazienteAttualmenteInVisita = pazienteAttualmenteInVisita;
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

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceDipendente() {
		return codiceDipendente;
	}

	public void setCodiceDipendente(String codiceDipendente) {
		this.codiceDipendente = codiceDipendente;
	}

	public Dottore buildDottoreModel() {
		return new Dottore(this.id, this.nome, this.cognome, this.codiceDipendente, this.pazienteAttualmenteInVisita);
	}

	public static DottoreDTO buildDottoreDTODTOFromModel(Dottore input) {
		return new DottoreDTO(input.getId(), input.getNome(), input.getCognome(), input.getCodiceDipendente(),
				input.getPazienteAttualmenteInVisita());
	}

}
