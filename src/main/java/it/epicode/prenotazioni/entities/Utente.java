package it.epicode.prenotazioni.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utenti")
@ToString(exclude = "prenotazioni")// Esclude le prenotazioni per evitare loop infiniti nel toString

public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private  String email;

    // Relazione One-to-Many con Prenotazione
    // MappedBy indica il nome del campo nell'entità "Prenotazione" che è il proprietario della relazione
    @OneToMany(mappedBy = "utente")
    private List<Prenotazione> prenotazioni;


}
