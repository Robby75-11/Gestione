package it.epicode.prenotazioni.entities;

import it.epicode.prenotazioni.enumeration.TipoPostazione;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "postazioni")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "prenotazioni")

public class Postazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codiceUnivoco;

    private String descrizione;

    @Enumerated(EnumType.STRING)
    private TipoPostazione tipo;

    @Column(nullable = false)
    private  Integer numeroMassimoOccupanti;


    // Relazione Many-to-One con Edificio
    // Questa è il lato proprietario della relazione, quindi gestisce la chiave esterna
    @ManyToOne
    @JoinColumn(name = "edificio_id", nullable = false) // Colonna della chiave esterna nella tabella "postazioni"
    private Edificio edificio;

    // Relazione One-to-Many con Prenotazione
    // MappedBy indica il nome del campo nell'entità "Prenotazione" che è il proprietario della relazione
    @OneToMany(mappedBy = "postazione")
    private List<Prenotazione> prenotazioni;



}
