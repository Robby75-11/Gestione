package it.epicode.prenotazioni.repository;

import it.epicode.prenotazioni.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {


    // 5. Trovare utenti con username o email specifici
    // Spring Data JPA combinerà le condizioni con un OR.
    Optional<Utente> findByUsernameOrEmail(String username, String email);

    // 6. Contare gli utenti con un certo username
    // Utile per verificare se un username esiste già.
    long countByUsername(String username);





}
