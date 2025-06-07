package it.epicode.prenotazioni.configuration;

import it.epicode.prenotazioni.entities.Edificio;
import it.epicode.prenotazioni.entities.Postazione;
import it.epicode.prenotazioni.enumeration.TipoPostazione;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean("edificioTorreEuropa")
    @Primary // Nome del bean più specifico
    public Edificio edificioBean() { // Il nome del metodo può rimanere, ma il bean sarà identificato dal nome specificato
        Edificio edificio = new Edificio();
        edificio.setNome("Torre Europa");
        edificio.setIndirizzo("Via Milano, 1");
        edificio.setCitta("Milano");
        return edificio;
    }

    @Bean("postazioneOpenspaceTorreEuropa")
    public Postazione postazioneBean(Edificio edificioTorreEuropa) { // Usiamo il nome del bean qui per chiarezza
        Postazione postazione = new Postazione();
        postazione.setCodiceUnivoco("POST-001");
        postazione.setDescrizione("Postazione openspace con 4 sedie");
        postazione.setTipo(TipoPostazione.OPENSPACE);
        postazione.setNumeroMassimoOccupanti(4);
        postazione.setEdificio(edificioTorreEuropa);
        return postazione;
    }

    // Nuovo bean: Edificio Palazzo Sinesi (Bari)
    @Bean("edificioPalazzoSinesi")
    public Edificio edificioPalazzoSinesi() {
        Edificio edificio = new Edificio();
        edificio.setNome("Palazzo Sinesi");
        edificio.setIndirizzo("Via Dante, 15/A");
        edificio.setCitta("Bari");
        return edificio;
    }

    // Nuovo bean: Postazione collegata a Palazzo Sinesi
    @Bean("postazionePrivataPalazzoSinesi")
    public Postazione postazionePalazzoSinesi(Edificio edificioPalazzoSinesi) {
        Postazione postazione = new Postazione();
        postazione.setCodiceUnivoco("POST-002");
        postazione.setDescrizione("Postazione privata tranquilla");
        postazione.setTipo(TipoPostazione.PRIVATO);
        postazione.setNumeroMassimoOccupanti(1);
        postazione.setEdificio(edificioPalazzoSinesi);
        return postazione;
    }

    @Bean("edificioVillaFiorita")
    public Edificio edificioVillaFiorita() {
        Edificio edificio = new Edificio();
        edificio.setNome("Villa Fiorita");
        edificio.setIndirizzo("Via Appia, 150");
        edificio.setCitta("Roma");
        return edificio;
    }

    @Bean("postazioneSalaRiunioniVillaFiorita")
    public Postazione postazioneVillaFiorita(Edificio edificioVillaFiorita) {
        Postazione postazione = new Postazione();
        postazione.setCodiceUnivoco("POST-003");
        postazione.setDescrizione("Postazione SALA_RIUNIONI");
        postazione.setTipo(TipoPostazione.SALA_RIUNIONI);
        postazione.setNumeroMassimoOccupanti(1);
        postazione.setEdificio(edificioVillaFiorita);
        return postazione;
    }

}