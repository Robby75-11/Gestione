package it.epicode.prenotazioni.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "postazioni")
@Table(name = "edifici")


public class Edificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String nome;
    private String indirizzo;
    private String citta;

    // Relazione One-to-Many con Postazione
    // MappedBy indica il nome del campo nell'entità "Postazione" che è il proprietario della relazione
    // Questo lato non gestisce la chiave esterna, la gestisce Postazione
    @OneToMany(mappedBy = "edificio")
    private List<Postazione> postazioni;

}
