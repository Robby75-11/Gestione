package it.epicode.prenotazioni.repository;

import it.epicode.prenotazioni.entities.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    Optional<Edificio> findByNomeAndCitta(String nome, String citta);

}
