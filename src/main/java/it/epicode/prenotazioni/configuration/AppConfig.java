package it.epicode.prenotazioni.configuration;
import it.epicode.prenotazioni.entities.Prenotazione;
import it.epicode.prenotazioni.entities.Utente;
import it.epicode.prenotazioni.entities.Edificio;
import it.epicode.prenotazioni.entities.Postazione;
import it.epicode.prenotazioni.enumeration.TipoPostazione;
import it.epicode.prenotazioni.repository.EdificioRepository;
import it.epicode.prenotazioni.repository.PrenotazioneRepository;
import it.epicode.prenotazioni.repository.UtenteRepository;
import it.epicode.prenotazioni.repository.PostazioneRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary; // Potrebbe servire per disambiguare se hai più bean dello stesso tipo

@Configuration
public class AppConfig {

    // Nota: I bean devono essere salvati nel DB. Possiamo farlo qui direttamente.
    // L'ordine dei bean è importante se un bean dipende da un altro (es. Postazione dipende da Edificio).

    @Bean
    @Primary // Utile se in futuro avessi più di un bean dello stesso tipo (es. più edifici)
    public Edificio edificioTorreguetta(EdificioRepository edificioRepository) {
        Edificio ed = new Edificio(null, "Torreguetta", "Lungomare N.Sauro", "Bari", null);
        return edificioRepository.save(ed); // Salva nel DB e restituisce l'istanza gestita da JPA
    }

    @Bean
    public Edificio edificioPalazzoB(EdificioRepository edificioRepository) {
        Edificio ed = new Edificio(null, "Palazzo B", "Piazza Dante 5", "Roma", null);
        return edificioRepository.save(ed);
    }



    @Bean
    public Edificio edificioCentroDirezionale(EdificioRepository edificioRepository) {
        Edificio ed = new Edificio(null, "Centro Direzionale", "Corso Italia 20", "Milano", null);
        return edificioRepository.save(ed);
    }

    @Bean
    @Primary // Utile se in futuro avessi più di un bean dello stesso tipo (es. più postazioni generiche)
    public Postazione postazioneP001(PostazioneRepository postazioneRepository, Edificio edificioTorreA) {
        // edificioTorreA viene iniettato automaticamente da Spring
        Postazione p = new Postazione(null, "P001", "Ufficio privato con vista", TipoPostazione.PRIVATO, 1, edificioTorreA, null);
        return postazioneRepository.save(p);
    }

    @Bean
    public Postazione postazioneP002(PostazioneRepository postazioneRepository, Edificio edificioTorreA) {
        Postazione p = new Postazione(null, "P002", "Scrivania in open space", TipoPostazione.OPENSPACE, 1, edificioTorreA, null);
        return postazioneRepository.save(p);
    }

    @Bean
    public Postazione postazioneP003(PostazioneRepository postazioneRepository, Edificio edificioPalazzoB) {
        Postazione p = new Postazione(null, "P003", "Sala riunioni piccola (4 persone)", TipoPostazione.SALA_RIUNIONI, 4, edificioPalazzoB, null);
        return postazioneRepository.save(p);
    }

    @Bean
    public Postazione postazioneP004(PostazioneRepository postazioneRepository, Edificio edificioCentroDirezionale) {
        Postazione p = new Postazione(null, "P004", "Scrivania in open space (secondo piano)", TipoPostazione.OPENSPACE, 1, edificioCentroDirezionale, null);
        return postazioneRepository.save(p);
    }
}