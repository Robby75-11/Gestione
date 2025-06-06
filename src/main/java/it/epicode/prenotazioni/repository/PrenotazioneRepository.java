package it.epicode.prenotazioni.repository;


import it.epicode.prenotazioni.entities.Postazione;
import it.epicode.prenotazioni.entities.Prenotazione;
import it.epicode.prenotazioni.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    // Query per verificare se una postazione è già prenotata per una certa data
    boolean existsByPostazioneAndDataPrenotazione(Postazione postazione, LocalDate dataPrenotazione);

    // Query per verificare se un utente ha già una prenotazione per una certa data (su qualsiasi postazione)
    boolean existsByUtenteAndDataPrenotazione(Utente utente, LocalDate dataPrenotazione);

    List<Prenotazione> findByUtente(Utente utente);
}
