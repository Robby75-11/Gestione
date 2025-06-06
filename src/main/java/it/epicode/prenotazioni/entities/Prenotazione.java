package it.epicode.prenotazioni.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "prenotazioni", uniqueConstraints = {
        // Questa constraint assicura che una postazione possa essere prenotata una sola volta per una data specifica.
        // Se un utente non può prenotare più di una postazione per una data specifica,
        // potrei aggiungere anche: @UniqueConstraint(columnNames = {"utente_id", "data_prenotazione"})
        @UniqueConstraint(columnNames = {"postazione_id", "data_prenotazione"})
})

@Data
@NoArgsConstructor

@AllArgsConstructor
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "data_prenotazione")
    private LocalDate dataPrenotazione;

    // Relazione Many-to-One con Utente
    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false) // Colonna della chiave esterna nella tabella "prenotazioni"
    private Utente utente;

    // Relazione Many-to-One con Postazione
    @ManyToOne
    @JoinColumn(name = "postazione_id", nullable = false) // Colonna della chiave esterna nella tabella "prenotazioni"
    private Postazione postazione;




}
