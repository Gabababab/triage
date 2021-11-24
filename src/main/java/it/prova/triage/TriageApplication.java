package it.prova.triage;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.prova.triage.model.Authority;
import it.prova.triage.model.AuthorityName;
import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;
import it.prova.triage.model.User;
import it.prova.triage.security.repository.AuthorityRepository;
import it.prova.triage.security.repository.UserRepository;
import it.prova.triage.service.PazienteService;

@SpringBootApplication
public class TriageApplication {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(TriageApplication.class, args);
	}

	@Bean
	public CommandLineRunner initTriage(PazienteService pazienteService) {
		return (args) -> {
			
			pazienteService.save(new Paziente("mario", "rossi", "CF45", StatoPaziente.IN_ATTESA_VISITA));
			
	
			User user = userRepository.findByUsername("admin").orElse(null);
		
			if (user == null) {
		
				/**
				 * Inizializzo i dati del mio test
				 */
		
				Authority authorityAdmin = new Authority();
				authorityAdmin.setName(AuthorityName.ROLE_ADMIN);
				authorityAdmin = authorityRepository.save(authorityAdmin);
		
				Authority authoritySubOperator = new Authority();
				authoritySubOperator.setName(AuthorityName.ROLE_SUB_OPERATOR);
				authoritySubOperator = authorityRepository.save(authoritySubOperator);
		
				List<Authority> authorities = Arrays.asList(new Authority[] { authorityAdmin, authoritySubOperator });
		
				user = new User();
				user.setNome("Gabriele");
				user.setCognome("lol");
				user.setAuthorities(authorities);
				user.setEnabled(true);
				user.setUsername("admin");
				user.setPassword(passwordEncoder.encode("admin"));
				user.setEmail("admin@example.com");
		
				user = userRepository.save(user);
		
			}
		
			User subOperator = userRepository.findByUsername("subOperator").orElse(null);
		
			if (subOperator == null) {
		
				/**
				 * Inizializzo i dati del mio test
				 */
		
				Authority authorityUser = authorityRepository.findByName(AuthorityName.ROLE_SUB_OPERATOR);
				if (authorityUser == null) {
					authorityUser = new Authority();
					authorityUser.setName(AuthorityName.ROLE_SUB_OPERATOR);
					authorityUser = authorityRepository.save(authorityUser);
				}
		
				List<Authority> authorities = Arrays.asList(new Authority[] { authorityUser });
		
				subOperator = new User();
				subOperator.setNome("Claudio");
				subOperator.setCognome("Bisio");
				subOperator.setAuthorities(authorities);
				subOperator.setEnabled(true);
				subOperator.setUsername("commonUser");
				subOperator.setPassword(passwordEncoder.encode("commonUser"));
				subOperator.setEmail("commonUser@example.com");
		
				subOperator = userRepository.save(subOperator);
		
			}
		};
	}
}
