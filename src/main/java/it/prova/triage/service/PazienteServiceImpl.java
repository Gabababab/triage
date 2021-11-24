package it.prova.triage.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.prova.triage.exceptions.PazienteNotFoundException;
import it.prova.triage.model.Paziente;
import it.prova.triage.repository.PazienteRepository;

@Service
public class PazienteServiceImpl implements PazienteService{

	@Autowired
	private PazienteRepository pazienteRepository;

	@Override
	public List<Paziente> listAll() {
		return (List<Paziente>) pazienteRepository.findAll();
	}

	@Override
	public Page<Paziente> searchAndPaginate(Paziente pazienteExample, Integer pageNo, Integer pageSize, String sortBy) {

		Specification<Paziente> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (!StringUtils.isEmpty(pazienteExample.getNome()))
				predicates.add(cb.like(cb.upper(root.get("nome")), "%" + pazienteExample.getNome().toUpperCase() + "%"));

			if (!StringUtils.isEmpty(pazienteExample.getCognome()))
				predicates.add(
						cb.like(cb.upper(root.get("cognome")), "%" + pazienteExample.getCognome().toUpperCase() + "%"));

			if (!StringUtils.isEmpty(pazienteExample.getCodiceFiscale()))
				predicates.add(cb.like(cb.upper(root.get("codiceFiscale")),
						"%" + pazienteExample.getCodiceFiscale().toUpperCase() + "%"));

			if (!StringUtils.isEmpty(pazienteExample.getStato()))
			predicates.add(cb.equal(cb.upper(root.get("stato")), pazienteExample.getStato()));
			
			if(pazienteExample.getDottore()!=null)
				predicates.add(cb.isTrue(root.get("dottore")));

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		return pazienteRepository.findAll(specificationCriteria, paging);
	}

	@Override
	public Paziente get(Long idInput) {
		return pazienteRepository.findById(idInput)
				.orElseThrow(() -> new PazienteNotFoundException("Element with id " + idInput + " not found."));
	}

	@Override
	public Paziente save(Paziente input) {
		return pazienteRepository.save(input);
	}

	@Override
	public void delete(Paziente input) {
		pazienteRepository.delete(input);
	}

	@Override
	public Paziente findByCodiceFiscale(String cf) {
		return pazienteRepository.findByCodiceFiscale(cf);
	}
}
