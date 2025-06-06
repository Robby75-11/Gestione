package it.epicode.prenotazioni;

import it.epicode.prenotazioni.entities.Edificio;
import it.epicode.prenotazioni.entities.Postazione;
import it.epicode.prenotazioni.entities.Prenotazione;
import it.epicode.prenotazioni.enumeration.TipoPostazione;
import it.epicode.prenotazioni.entities.Utente;
import it.epicode.prenotazioni.repository.EdificioRepository; // Aggiunto questo import!
import it.epicode.prenotazioni.repository.PostazioneRepository;
import it.epicode.prenotazioni.repository.PrenotazioneRepository;
import it.epicode.prenotazioni.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Runner implements CommandLineRunner {

    // --- Iniezione dei bean definiti in AppConfig (oggetti in memoria, ID null) ---
    @Autowired @Qualifier("edificioTorreguetta") private Edificio edificioTorreguettaBean;
    @Autowired @Qualifier("edificioPalazzoB") private Edificio edificioPalazzoBBean;
    @Autowired @Qualifier("edificioCentroDirezionale") private Edificio edificioCentroDirezionaleBean;

    @Autowired @Qualifier("postazioneP001") private Postazione postazioneP001Bean;
    @Autowired @Qualifier("postazioneP002") private Postazione postazioneP002Bean;
    @Autowired @Qualifier("postazioneP003") private Postazione postazioneP003Bean;
    @Autowired @Qualifier("postazioneP004") private Postazione postazioneP004Bean;
    @Autowired(required = false) @Qualifier("postazioneP005Milano") private Postazione postazioneP005MilanoBean;


    // --- Iniezione delle repository (per interagire con il DB) ---
    @Autowired private UtenteRepository utenteRepository;
    @Autowired private PrenotazioneRepository prenotazioneRepository;
    @Autowired private PostazioneRepository postazioneRepository;
    @Autowired private EdificioRepository edificioRepository; // *** NECESSARIO per salvare gli Edifici ***

    private Scanner scanner;

    @Override
    @Transactional // Le operazioni di DB devono essere all'interno di una transazione
    public void run(String... args) throws Exception {
        System.out.println("--- Avvio Applicazione Prenotazioni ---");

        this.scanner = new Scanner(System.in); // Inizializza lo Scanner UNA SOLA VOLTA qui.
        // Non usa try-with-resources per non chiuderlo subito.
        try {
            inizializzaDatiIniziali(); // Questo metodo ora salverà gli Edifici, Postazioni e Utenti
            mostraMenu(); // Il menu interattivo ora può usare lo scanner senza problemi di chiusura anticipata.

        } catch (Exception e) {
            System.err.println("Errore critico durante l'esecuzione: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Questo blocco finally si attiverà solo se c'è un'eccezione imprevista in run().
            // La chiusura dello scanner in caso di uscita normale è ora gestita in mostraMenu().
            System.out.println("--- Applicazione Terminata ---");
            // Optional: Se vuoi essere super sicuro che lo scanner venga chiuso anche in caso di eccezioni
            // non gestite altrove o di uscita anomala, puoi aggiungere qui:
            // if (scanner != null) {
            //     scanner.close();
            // }
        }
    }

    // --- Metodo per inizializzare i dati nel DB ---
    @Transactional // Assicura che questo blocco di salvataggi sia una transazione singola
    private void inizializzaDatiIniziali() {
        System.out.println("\n--- Inizializzazione Dati di Base (Edifici, Postazioni, Utenti) ---");

        // 1. Salva gli edifici se non esistono
        // E' FONDAMENTALE USARE L'OGGETTO RESTITUITO DAL SAVE
        // perché è quello che ha l'ID generato dal DB
        Edificio edificioTorreguettaDB = salvaEdificioSeNonEsiste(edificioTorreguettaBean);
        Edificio edificioPalazzoBDB = salvaEdificioSeNonEsiste(edificioPalazzoBBean);
        Edificio edificioCentroDirezionaleDB = salvaEdificioSeNonEsiste(edificioCentroDirezionaleBean);

        // 2. Associa gli Edifici persistiti agli oggetti Postazione prima di salvarle
        // I bean iniettati (es. postazioneP001Bean) puntano all'edificio in memoria.
        // Dobbiamo aggiornare questi riferimenti a quelli che sono stati salvati nel DB
        // e che ora hanno un ID valido.
        postazioneP001Bean.setEdificio(edificioTorreguettaDB);
        postazioneP002Bean.setEdificio(edificioTorreguettaDB);
        postazioneP003Bean.setEdificio(edificioTorreguettaDB);
        postazioneP004Bean.setEdificio(edificioCentroDirezionaleDB);
        if(postazioneP005MilanoBean != null) {
            postazioneP005MilanoBean.setEdificio(edificioCentroDirezionaleDB);
        }

        // 3. Salva le postazioni se non esistono
        // Anche qui, usa l'oggetto restituito dal save per avere l'ID
        Postazione postazioneP001DB = salvaPostazioneSeNonEsiste(postazioneP001Bean);
        Postazione postazioneP002DB = salvaPostazioneSeNonEsiste(postazioneP002Bean);
        Postazione postazioneP003DB = salvaPostazioneSeNonEsiste(postazioneP003Bean);
        Postazione postazioneP004DB = salvaPostazioneSeNonEsiste(postazioneP004Bean);
        Postazione postazioneP005MilanoDB = null;
        if(postazioneP005MilanoBean != null) {
            postazioneP005MilanoDB = salvaPostazioneSeNonEsiste(postazioneP005MilanoBean);
        }


        // 4. Salva gli utenti di test se non esistono
        Utente u1 = salvaUtenteSeNonEsiste(new Utente(null, "Topogigio", "Mario Rossi", "mario.rossi@gmail.com", null));
        Utente u2 = salvaUtenteSeNonEsiste(new Utente(null, "Gattoboy", "Andrea Albergo", "andrea.albergo@example.com", null));
        Utente u3 = salvaUtenteSeNonEsiste(new Utente(null, "Pippo", "Pippo Bianchi", "pippo.b@test.it", null));
        Utente u4 = salvaUtenteSeNonEsiste(new Utente(null, "Pluto", "Pluto Neri", "pluto.n@example.com", null));

        System.out.println("Dati iniziali caricati o già presenti nel DB.");

        // Esegui i test automatici qui
        eseguiTestPredefiniti(postazioneP001DB, postazioneP002DB, postazioneP003DB, u1, u2);
    }

    // --- Metodo per eseguire i test automatici/predefiniti ---
    private void eseguiTestPredefiniti(Postazione postazioneP001DB, Postazione postazioneP002DB, Postazione postazioneP003DB, Utente u1, Utente u2) {
        System.out.println("\n--- Esecuzione Test Predefiniti ---");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        System.out.println("\n--- Creazione Prenotazioni (logica interna al Runner) ---");
        if(postazioneP001DB != null && postazioneP003DB != null && postazioneP002DB != null && u1 != null && u2 != null) {
            creaPrenotazione(u1.getId(), postazioneP001DB.getId(), tomorrow); // Topogigio prenota P001
            creaPrenotazione(u2.getId(), postazioneP003DB.getId(), tomorrow); // Gattoboy prenota P003

            // Tentativi che dovrebbero fallire
            creaPrenotazione(u1.getId(), postazioneP002DB.getId(), tomorrow); // Topogigio tenta di prenotare P002 (stesso giorno)
            creaPrenotazione(u2.getId(), postazioneP001DB.getId(), tomorrow); // Gattoboy tenta di prenotare P001 (già presa)
        } else {
            System.err.println("ATTENZIONE: Impossibile eseguire test prenotazioni: Dati mancanti nel DB.");
        }


        System.out.println("\n--- Ricerca Postazioni (logica interna al Runner) ---");
        List<Postazione> openSpaceMilano = cercaPostazioni(TipoPostazione.OPENSPACE, "Milano");
        System.out.println("Postazioni Open Space a Milano:");
        if (openSpaceMilano.isEmpty()) {
            System.out.println("- Nessuna postazione Open Space trovata a Milano. Controlla i dati iniziali.");
        } else {
            openSpaceMilano.forEach(p -> System.out.println("- " + p.getCodiceUnivoco() + " (" + p.getDescrizione() + ") nell'edificio " + p.getEdificio().getNome()));
        }


        System.out.println("\n--- INVOCAZIONE NUOVI METODI UTENTEREPO ---");

        System.out.println("\n--- Testing findByUsername ---");
        Utente foundByUsername = utenteRepository.findByUsername("Topogigio");
        if (foundByUsername != null) {
            System.out.println("Trovato utente per username 'Topogigio': " + foundByUsername.getNomeCompleto());
        } else {
            System.out.println("Nessun utente trovato per username 'Topogigio'.");
        }

        Utente notFoundByUsername = utenteRepository.findByUsername("NonEsisto");
        if (notFoundByUsername != null) {
            System.out.println("Trovato utente per username 'NonEsisto': " + notFoundByUsername.getNomeCompleto());
        } else {
            System.out.println("Nessun utente trovato per username 'NonEsisto'. (Atteso)");
        }

        System.out.println("\n--- Testing findByUsernameOrEmail ---");
        Optional<Utente> byUsernameOrEmail1 = utenteRepository.findByUsernameOrEmail("Gattoboy", "dummy@example.com");
        byUsernameOrEmail1.ifPresentOrElse(
                u -> System.out.println("Trovato utente per username 'Gattoboy' o email 'dummy@example.com': " + u.getNomeCompleto()),
                () -> System.out.println("Nessun utente trovato per username 'Gattoboy' o email 'dummy@example.com'.")
        );

        Optional<Utente> byUsernameOrEmail2 = utenteRepository.findByUsernameOrEmail("NonEsisto", "mario.rossi@gmail.com");
        byUsernameOrEmail2.ifPresentOrElse(
                u -> System.out.println("Trovato utente per username 'NonEsisto' o email 'mario.rossi@gmail.com': " + u.getNomeCompleto()),
                () -> System.out.println("Nessun utente trovato per username 'NonEsisto' o email 'mario.rossi@gmail.com'.")
        );

        Optional<Utente> byUsernameOrEmail3 = utenteRepository.findByUsernameOrEmail("NonEsistoAncheIo", "altra.email@non.esiste");
        byUsernameOrEmail3.ifPresentOrElse(
                u -> System.out.println("Trovato utente per username 'NonEsistoAncheIo' o email 'altra.email@non.esiste': " + u.getNomeCompleto()),
                () -> System.out.println("Nessun utente trovato per username 'NonEsistoAncheIo' o email 'altra.email@non.esiste'. (Atteso)")
        );

        System.out.println("\n--- Testing countByUsername ---");
        long countTopogigio = utenteRepository.countByUsername("Topogigio");
        System.out.println("Numero di utenti con username 'Topogigio': " + countTopogigio);

        long countNonEsistente = utenteRepository.countByUsername("Inesistente");
        System.out.println("Numero di utenti con username 'Inesistente': " + countNonEsistente);

        System.out.println("\n--- Testing existsByEmail ---");
        boolean existsGmail = utenteRepository.existsByEmail("mario.rossi@gmail.com");
        System.out.println("Esiste un utente con email 'mario.rossi@gmail.com'? " + existsGmail);

        boolean existsNonExistentEmail = utenteRepository.existsByEmail("non.esisto@fake.com");
        System.out.println("Esiste un utente con email 'non.esisto@fake.com'? " + existsNonExistentEmail);
    }

    // --- Metodo per mostrare il menu interattivo ---
    private void mostraMenu() {
        int scelta;
        do {
            System.out.println("\n--- Menu Principale ---");
            System.out.println("1. Crea nuova prenotazione");
            System.out.println("2. Cerca postazioni disponibili");
            System.out.println("3. Elenca tutti gli utenti");
            System.out.println("4. Elenca tutte le postazioni");
            System.out.println("5. Elenca tutte le prenotazioni");
            System.out.println("0. Esci");
            System.out.print("Scegli un'opzione: ");

            try {
                scelta = Integer.parseInt(scanner.nextLine());
                switch (scelta) {
                    case 1:
                        gestisciNuovaPrenotazione();
                        break;
                    case 2:
                        gestisciRicercaPostazioni();
                        break;
                    case 3:
                        elencaTuttiUtenti();
                        break;
                    case 4:
                        elencaTuttePostazioni();
                        break;
                    case 5:
                        elencaTuttePrenotazioni();
                        break;
                    case 0:
                        System.out.println("Uscita dall'applicazione. Arrivederci!");
                        if (scanner != null) { // Controlla che lo scanner non sia null prima di chiuderlo
                            scanner.close();   // Chiude lo scanner esplicitamente
                        }
                        System.exit(0); // Termina l'applicazione in modo pulito
                        break;
                    default:
                        System.out.println("Opzione non valida. Riprova.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
                scelta = -1;
            } catch (Exception e) {
                System.err.println("Si è verificato un errore: " + e.getMessage());
                scelta = -1;
            }
        } while (scelta != 0);
    }

    // --- Metodi di gestione delle opzioni del menu ---
    @Transactional
    private void gestisciNuovaPrenotazione() {
        System.out.println("\n--- Nuova Prenotazione ---");
        System.out.print("Inserisci Username utente: ");
        String username = scanner.nextLine();
        Utente utente = utenteRepository.findByUsername(username);

        if (utente == null) {
            System.out.println("Utente con username '" + username + "' non trovato.");
            return;
        }
        System.out.println("Utente selezionato: " + utente.getNomeCompleto() + " (ID: " + utente.getId() + ")");

        System.out.print("Inserisci Codice Univoco postazione: ");
        String codicePostazione = scanner.nextLine();
        Optional<Postazione> postazioneOpt = Optional.ofNullable(postazioneRepository.findByCodiceUnivoco(codicePostazione));

        if (postazioneOpt.isEmpty()) {
            System.out.println("Postazione con codice '" + codicePostazione + "' non trovata.");
            return;
        }
        Postazione postazione = postazioneOpt.get();
        System.out.println("Postazione selezionata: " + postazione.getDescrizione() + " (ID: " + postazione.getId() + ") in " + postazione.getEdificio().getNome());

        LocalDate dataPrenotazione = null;
        boolean dataValida = false;
        while (!dataValida) {
            System.out.print("Inserisci Data Prenotazione (formato YYYY-MM-DD): ");
            String dataStr = scanner.nextLine();
            try {
                dataPrenotazione = LocalDate.parse(dataStr);
                if (dataPrenotazione.isBefore(LocalDate.now())) {
                    System.out.println("La data di prenotazione deve essere oggi o nel futuro.");
                } else {
                    dataValida = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido. Riprova con YYYY-MM-DD.");
            }
        }

        creaPrenotazione(utente.getId(), postazione.getId(), dataPrenotazione);
    }

    private void gestisciRicercaPostazioni() {
        System.out.println("\n--- Ricerca Postazioni ---");
        TipoPostazione tipo = null;
        boolean tipoValido = false;
        while(!tipoValido) {
            System.out.print("Inserisci Tipo Postazione (PRIVATO, OPENSPACE, SALA_RIUNIONI): ");
            String tipoStr = scanner.nextLine().toUpperCase();
            try {
                tipo = TipoPostazione.valueOf(tipoStr);
                tipoValido = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo postazione non valido. Riprova.");
            }
        }

        System.out.print("Inserisci Città: ");
        String citta = scanner.nextLine();

        List<Postazione> foundPostazioni = cercaPostazioni(tipo, citta);
        if (foundPostazioni.isEmpty()) {
            System.out.println("Nessuna postazione trovata per i criteri specificati.");
        } else {
            System.out.println("Postazioni trovate:");
            foundPostazioni.forEach(p -> System.out.println("- Codice: " + p.getCodiceUnivoco() + ", Descrizione: " + p.getDescrizione() + ", Occupanti: " + p.getNumeroMassimoOccupanti() + ", Edificio: " + p.getEdificio().getNome() + " (" + p.getEdificio().getCitta() + ")"));
        }
    }

    private void elencaTuttiUtenti() {
        System.out.println("\n--- Elenco Utenti ---");
        List<Utente> utenti = utenteRepository.findAll();
        if (utenti.isEmpty()) {
            System.out.println("Nessun utente nel database.");
        } else {
            utenti.forEach(u -> System.out.println("- ID: " + u.getId() + ", Username: " + u.getUsername() + ", Nome: " + u.getNomeCompleto() + ", Email: " + u.getEmail()));
        }
    }

    private void elencaTuttePostazioni() {
        System.out.println("\n--- Elenco Postazioni ---");
        List<Postazione> postazioni = postazioneRepository.findAll();
        if (postazioni.isEmpty()) {
            System.out.println("Nessuna postazione nel database.");
        } else {
            postazioni.forEach(p -> System.out.println("- ID: " + p.getId() + ", Codice: " + p.getCodiceUnivoco() + ", Tipo: " + p.getTipo() + ", Edificio: " + p.getEdificio().getNome() + " (" + p.getEdificio().getCitta() + ")"));
        }
    }

    private void elencaTuttePrenotazioni() {
        System.out.println("\n--- Elenco Prenotazioni ---");
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        if (prenotazioni.isEmpty()) {
            System.out.println("Nessuna prenotazione nel database.");
        } else {
            prenotazioni.forEach(pr -> System.out.println("- ID: " + pr.getId() + ", Utente: " + pr.getUtente().getUsername() + ", Postazione: " + pr.getPostazione().getCodiceUnivoco() + ", Data: " + pr.getDataPrenotazione()));
        }
    }

    // --- Metodi di utilità per il salvataggio condizionato (gestiti dal Runner) ---
    private Edificio salvaEdificioSeNonEsiste(Edificio edificio) {
        // Cerca per nome perché è un campo "logico" per l'identificazione
        Optional<Edificio> existingEdificio = Optional.ofNullable(edificioRepository.findByNome(edificio.getNome()));
        if (existingEdificio.isEmpty()) {
            edificioRepository.save(edificio);
            System.out.println("Salvataggio Edificio: " + edificio.getNome() + " (ID: " + edificio.getId() + ")");
            return edificio; // Restituisce l'istanza con ID generato
        } else {
            System.out.println("Edificio esistente: " + edificio.getNome() + ", utilizzando l'esistente (ID: " + existingEdificio.get().getId() + ").");
            edificio.setId(existingEdificio.get().getId()); // Aggiorna l'ID del bean in memoria con quello del DB
            return existingEdificio.get();
        }
    }

    private Postazione salvaPostazioneSeNonEsiste(Postazione postazione) {
        // Cerca per codice univoco perché è il campo "logico" per l'identificazione
        Optional<Postazione> existingPostazione = Optional.ofNullable(postazioneRepository.findByCodiceUnivoco(postazione.getCodiceUnivoco()));
        if (existingPostazione.isEmpty()) {
            postazioneRepository.save(postazione);
            System.out.println("Salvataggio Postazione: " + postazione.getCodiceUnivoco() + " (ID: " + postazione.getId() + ")");
            return postazione; // Restituisce l'istanza con ID generato
        } else {
            System.out.println("Postazione esistente: " + postazione.getCodiceUnivoco() + ", utilizzando l'esistente (ID: " + existingPostazione.get().getId() + ").");
            postazione.setId(existingPostazione.get().getId()); // Aggiorna l'ID del bean in memoria
            postazione.setEdificio(existingPostazione.get().getEdificio()); // Assicurati che l'edificio sia quello persistito!
            return existingPostazione.get();
        }
    }

    private Utente salvaUtenteSeNonEsiste(Utente utente) {
        // Cerca per username o email per evitare duplicati
        Utente existingUtente = utenteRepository.findByUsername(utente.getUsername());
        if (existingUtente == null) {
            utenteRepository.save(utente);
            System.out.println("Salvataggio Utente: " + utente.getUsername() + " (ID: " + utente.getId() + ")");
            return utente; // Restituisce l'istanza con ID generato
        } else {
            System.out.println("Utente esistente: " + utente.getUsername() + ", utilizzando l'esistente (ID: " + existingUtente.getId() + ").");
            return existingUtente;
        }
    }

    private void creaPrenotazione(Long utenteId, Long postazioneId, LocalDate data) {
        try {
            Optional<Utente> utenteOpt = utenteRepository.findById(utenteId);
            Optional<Postazione> postazioneOpt = postazioneRepository.findById(postazioneId);

            if (utenteOpt.isEmpty()) {
                System.err.println("Errore: Utente con ID " + utenteId + " non trovato.");
                return;
            }
            if (postazioneOpt.isEmpty()) {
                System.err.println("Errore: Postazione con ID " + postazioneId + " non trovata.");
                return;
            }

            Utente utente = utenteOpt.get();
            Postazione postazione = postazioneOpt.get();

            if (prenotazioneRepository.existsByPostazioneAndDataPrenotazione(postazione, data)) {
                System.out.println("ATTENZIONE: La postazione " + postazione.getCodiceUnivoco() +
                        " è già prenotata per la data " + data + ".");
                return;
            }

            if (prenotazioneRepository.existsByUtenteAndDataPrenotazione(utente, data)) {
                System.out.println("ATTENZIONE: L'utente " + utente.getUsername() +
                        " ha già una prenotazione per la data " + data + ".");
                return;
            }

            Prenotazione nuovaPrenotazione = new Prenotazione(null, data, utente, postazione);
            nuovaPrenotazione = prenotazioneRepository.save(nuovaPrenotazione);
            System.out.println("Prenotazione creata: " + nuovaPrenotazione);

        } catch (Exception e) {
            System.err.println("Errore nella creazione prenotazione: " + e.getMessage());
        }
    }

    public List<Postazione> cercaPostazioni(TipoPostazione tipo, String citta) {
        return postazioneRepository.findByTipoAndEdificioCitta(tipo, citta);
    }
}