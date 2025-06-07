package it.epicode.prenotazioni;

import it.epicode.prenotazioni.entities.Edificio;
import it.epicode.prenotazioni.entities.Postazione;
import it.epicode.prenotazioni.entities.Prenotazione;
import it.epicode.prenotazioni.entities.Utente;
import it.epicode.prenotazioni.enumeration.TipoPostazione;
import it.epicode.prenotazioni.repository.EdificioRepository;
import it.epicode.prenotazioni.repository.PostazioneRepository;
import it.epicode.prenotazioni.repository.PrenotazioneRepository;
import it.epicode.prenotazioni.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Runner implements CommandLineRunner {

    @Autowired private EdificioRepository edificioRepo;
    @Autowired private PostazioneRepository postazioneRepo;
    @Autowired private UtenteRepository utenteRepo;
    @Autowired private PrenotazioneRepository prenotazioneRepo;

    @Autowired @Qualifier("edificioTorreEuropa") private Edificio edificioTorreEuropa;
    @Autowired @Qualifier("postazioneOpenspaceTorreEuropa") private Postazione postazioneOpenspaceTorreEuropa;
    @Autowired @Qualifier("edificioPalazzoSinesi") private Edificio edificioPalazzoSinesi;
    @Autowired @Qualifier("postazionePrivataPalazzoSinesi") private Postazione postazionePrivataPalazzoSinesi;
    @Autowired @Qualifier("edificioVillaFiorita") private Edificio edificioVillaFiorita;
    @Autowired @Qualifier("postazioneSalaRiunioniVillaFiorita") private Postazione postazioneSalaRiunioniVillaFiorita;

    @Override
    public void run(String... args) throws Exception {

        // Salvataggio Edifici
        edificioRepo.save(edificioTorreEuropa);
        edificioRepo.save(edificioPalazzoSinesi);
        edificioRepo.save(edificioVillaFiorita);



        // Salvataggio Postazioni solo se non esistono già (controllo codice univoco)
        if (postazioneRepo.findByCodiceUnivoco(postazioneOpenspaceTorreEuropa.getCodiceUnivoco()).isEmpty()) {
            postazioneRepo.save(postazioneOpenspaceTorreEuropa);
        }
        if (postazioneRepo.findByCodiceUnivoco(postazionePrivataPalazzoSinesi.getCodiceUnivoco()).isEmpty()) {
            postazioneRepo.save(postazionePrivataPalazzoSinesi);
        }
        if (postazioneRepo.findByCodiceUnivoco(postazioneSalaRiunioniVillaFiorita.getCodiceUnivoco()).isEmpty()) {
            postazioneRepo.save(postazioneSalaRiunioniVillaFiorita);
        }

        // Creazione utenti
        Utente utente = new Utente();
        utente.setUsername("mrossi");
        utente.setNomeCompleto("Marco Rossi");
        utente.setEmail("marco.rossi@example.com");
        utenteRepo.save(utente);

        Utente utente1 = new Utente();
        utente1.setUsername("topogigio13");
        utente1.setNomeCompleto("Andrea Albergo");
        utente1.setEmail("Andrea.Albergo13@example.com");
        utenteRepo.save(utente1);

        System.out.println("👤 Nuovo utente salvato: " + utente1.getNomeCompleto());

        Scanner scanner = new Scanner(System.in);
        boolean continua = true;

        while (continua) {
        System.out.println("========= MENU PRINCIPALE =========");
        System.out.println("1 - Scegli la postazione da prenotare");
        System.out.println("2 - Visualizza postazioni OPENSPACE a Milano");
        System.out.println("3 - Visualizza tutte le prenotazioni di un utente");
        System.out.println("4 - Cerca edificio per nome e città");
        System.out.println("5 - Cerca utente per username o email");
        System.out.print("6 - Esci");
        int scelta = scanner.nextInt();
        scanner.nextLine();

                switch (scelta){
                case 1 -> {
                    System.out.println("Scegli la postazione da prenotare:");
                    System.out.println("1 - Openspace Torre Europa");
                    System.out.println("2 - Privata Palazzo Sinesi");
                    System.out.println("3 - Sala Riunioni Villa Fiorita");
                    System.out.print("Inserisci il numero scelta: ");
                    int sceltaPostazione = scanner.nextInt();
                    scanner.nextLine();

                    Postazione postazioneSelezionata;
                    switch (sceltaPostazione) {
                        case 1 -> postazioneSelezionata = postazioneOpenspaceTorreEuropa;
                        case 2 -> postazioneSelezionata = postazionePrivataPalazzoSinesi;
                        case 3 -> postazioneSelezionata = postazioneSalaRiunioniVillaFiorita;
                        default -> {
                            System.out.println("Scelta non valida. Selezionata postazione di default.");
                            postazioneSelezionata = postazioneOpenspaceTorreEuropa;
                        }
                    }
                            // Verifica prenotazione per oggi
                            LocalDate oggi = LocalDate.now();
                            boolean postazioneOccupata = prenotazioneRepo.existsByPostazioneAndDataPrenotazione(postazioneSelezionata, oggi);
                            boolean utenteHaPrenotazione = prenotazioneRepo.existsByUtenteAndDataPrenotazione(utente, oggi);

                            if (postazioneOccupata) {
                                System.out.println("⚠️ Postazione già prenotata per oggi.");
                            } else if (utenteHaPrenotazione) {
                                System.out.println(" L'utente ha già una prenotazione per oggi.");
                            } else {
                                Prenotazione p = new Prenotazione();
                                p.setUtente(utente);
                                p.setPostazione(postazioneSelezionata);
                                p.setDataPrenotazione(oggi);
                                prenotazioneRepo.save(p);
                                System.out.println("✅ Prenotazione effettuata per oggi su: " + postazioneSelezionata.getDescrizione());
                            }
                        }

                    // Visualizzazione postazioni di tipo OPENSPACE a Milano
                    case 2 -> {


                        System.out.println("\n📌 Postazioni di tipo OPENSPACE nella città di Milano:");

                    List<Postazione> postazioniMilano = postazioneRepo.findByTipoAndEdificioCitta(TipoPostazione.OPENSPACE, "Milano");
                    if (postazioniMilano.isEmpty()) {
                        System.out.println("⚠️ Nessuna postazione trovata.");
                    } else {
                        postazioniMilano.forEach(p ->
                                System.out.println("- " + p.getDescrizione() + " | Edificio: " + p.getEdificio().getNome()));
                    }
                }

            case 3 -> {
                System.out.print("Inserisci username dell'utente: ");
                String username = scanner.nextLine();
                List<Prenotazione> prenotazioniUtente = prenotazioneRepo.findByUtenteUsername(username);
                if (prenotazioniUtente.isEmpty()) {
                    System.out.println("⚠️ Nessuna prenotazione trovata.");
                } else {
                    System.out.println("📅 Prenotazioni di '" + username + "':");
                    prenotazioniUtente.forEach(p ->
                            System.out.println("- " + p.getDataPrenotazione() + ": " + p.getPostazione().getDescrizione()));
                }
            }
            case 4 -> {
                System.out.print("Inserisci nome edificio: ");
                String nome = scanner.nextLine();
                System.out.print("Inserisci città edificio: ");
                String citta = scanner.nextLine();
                Optional<Edificio> edificioTrovato = edificioRepo.findByNomeAndCitta(nome, citta);
                edificioTrovato.ifPresentOrElse(
                        e -> System.out.println("🏢 Edificio trovato: " + e.getNome() + " - " + e.getIndirizzo()),
                        () -> System.out.println("❌ Nessun edificio trovato con nome e città specificati.")
                );
            }
                    case 5 -> {
                        System.out.print("Inserisci username o premi INVIO: ");
                        String usernameInput = scanner.nextLine();
                        System.out.print("Inserisci email o premi INVIO: ");
                        String emailInput = scanner.nextLine();

                        if (usernameInput.isEmpty() && emailInput.isEmpty()) {
                            System.out.println("⚠️ Devi inserire almeno username o email.");
                        } else {
                            Optional<Utente> utenteTrovato = utenteRepo.findByUsernameOrEmail(usernameInput, emailInput);
                            utenteTrovato.ifPresentOrElse(
                                    u -> System.out.println("👤 Utente trovato: " + u.getNomeCompleto() + " | Email: " + u.getEmail()),
                                    () -> System.out.println("❌ Nessun utente trovato.")
                            );
                        }
                    }


            case 6 -> {
                System.out.println("👋 Uscita dal programma. Alla prossima!");
                continua = false;
            }
            default -> System.out.println("❌ Opzione non valida.");
           }
        }
      }
}