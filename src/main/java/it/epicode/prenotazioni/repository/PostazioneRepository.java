package it.epicode.prenotazioni.repository;

import it.epicode.prenotazioni.entities.Postazione;
import it.epicode.prenotazioni.enumeration.TipoPostazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostazioneRepository extends JpaRepository<Postazione, Long> {
    // Metodo per trovare postazioni per tipo e città dell'edificio
    List<Postazione> findByTipoAndEdificioCitta(TipoPostazione tipo,String citta);
    Postazione findByCodiceUnivoco(String codiceUnivoco);
}
