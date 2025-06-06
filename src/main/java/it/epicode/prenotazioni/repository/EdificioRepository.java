package it.epicode.prenotazioni.repository;

import it.epicode.prenotazioni.entities.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {

    Edificio findByNome(String nome);
}
