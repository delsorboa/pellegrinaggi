package it.pellegrinaggi.service;

import it.pellegrinaggi.model.Adesione;
import it.pellegrinaggi.model.Partecipante;
import it.pellegrinaggi.model.PartecipanteEvento;
import it.pellegrinaggi.model.PartecipanteSistemazione;
import it.pellegrinaggi.model.PartecipanteSpostamento;
import it.pellegrinaggi.model.ResponsabileSpostamento;
import it.pellegrinaggi.model.ResponsabileSpostamentoReport;
import it.pellegrinaggi.model.Servizio;
import it.pellegrinaggi.model.ServizioReport;
import it.pellegrinaggi.model.Spostamento;
import it.pellegrinaggi.model.SpostamentoReport;
import it.pellegrinaggi.repository.AdesioneRepository;
import it.pellegrinaggi.repository.PartecipanteRepository;
import it.pellegrinaggi.repository.PartecipanteSistemazioneRepository;
import it.pellegrinaggi.repository.PartecipanteSpostamentoRepository;
import it.pellegrinaggi.repository.QualificaRepository;
import it.pellegrinaggi.repository.ResponsabileSpostamentoRepository;
import it.pellegrinaggi.repository.ServizioRepository;
import it.pellegrinaggi.repository.SpostamentoRepository;
import it.pellegrinaggi.model.Camera;
import it.pellegrinaggi.model.Eventi;
import it.pellegrinaggi.model.MedagliaReport;
import it.pellegrinaggi.model.Sistemazione;
import it.pellegrinaggi.model.SistemazioneReport;
import it.pellegrinaggi.repository.CameraRepository;
import it.pellegrinaggi.repository.PartecipanteEventoRepository;
import it.pellegrinaggi.repository.SistemazioneRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;


@Service
public class JasperReportService {

    private final AdesioneRepository adesioneRepository;

    private final PartecipanteSpostamentoRepository
            partecipanteSpostamentoRepository;

    private final SpostamentoRepository
            spostamentoRepository;

    private final QualificaRepository
            qualificaRepository;
    

    private ResponsabileSpostamentoRepository responsabileSpostamentoRepository;
    
    
    private PartecipanteRepository partecipanteRepository;
    
    private final CameraRepository cameraRepository;
    
    
    private final SistemazioneRepository sistemazioneRepository;
    
    private final PartecipanteSistemazioneRepository
    partecipanteSistemazioneRepository;
    
    private final PartecipanteEventoRepository
    partecipanteEventoRepository;

    private final ServizioRepository
    servizioRepository;




  




    public JasperReportService(AdesioneRepository adesioneRepository,
			PartecipanteSpostamentoRepository partecipanteSpostamentoRepository,
			SpostamentoRepository spostamentoRepository, QualificaRepository qualificaRepository,
			ResponsabileSpostamentoRepository responsabileSpostamentoRepository,
			PartecipanteRepository partecipanteRepository, CameraRepository cameraRepository,
			SistemazioneRepository sistemazioneRepository,
			PartecipanteSistemazioneRepository partecipanteSistemazioneRepository,
			PartecipanteEventoRepository partecipanteEventoRepository, ServizioRepository servizioRepository) {
		super();
		this.adesioneRepository = adesioneRepository;
		this.partecipanteSpostamentoRepository = partecipanteSpostamentoRepository;
		this.spostamentoRepository = spostamentoRepository;
		this.qualificaRepository = qualificaRepository;
		this.responsabileSpostamentoRepository = responsabileSpostamentoRepository;
		this.partecipanteRepository = partecipanteRepository;
		this.cameraRepository = cameraRepository;
		this.sistemazioneRepository = sistemazioneRepository;
		this.partecipanteSistemazioneRepository = partecipanteSistemazioneRepository;
		this.partecipanteEventoRepository = partecipanteEventoRepository;
		this.servizioRepository = servizioRepository;
	}









	/**
     * ============================================================
     * GENERA FOGLIO DI SERVIZIO
     * ============================================================
     */
    public byte[] generaFoglioServizio(
            Integer idAdesione)  throws JRException, IOException  {


        /*
         * ========================================================
         * ADESIONE
         * ========================================================
         */

        Adesione adesione =
                adesioneRepository
                        .findById(idAdesione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Adesione non trovata"
                                )
                        );


        /*
         * ========================================================
         * FORMATTAZIONE DATE
         * ========================================================
         */

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );


        /*
         * ========================================================
         * PARAMETRI JASPER
         * ========================================================
         */

        Map<String, Object> parameters =
                new HashMap<>();


        /*
         * ========================================================
         * LOGO
         * ========================================================
         */

        try {

            ClassPathResource logoResource =
                    new ClassPathResource("reports/images/scudo.jpg");

            BufferedImage logo;

            try (InputStream inputStream = logoResource.getInputStream()) {
                logo = ImageIO.read(inputStream);
            }

            if (logo == null) {
                throw new IllegalArgumentException(
                        "Impossibile leggere l'immagine del logo"
                );
            }

            parameters.put("LOGO", logo);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Logo non trovato o non leggibile: reports/images/scudo.jpg",
                    e
            );
        }





        /*
         * ========================================================
         * PARTECIPANTE
         * ========================================================
         */

        parameters.put(
                "COGNOME",
                adesione
                        .getPartecipante()
                        .getCognome()
        );


        parameters.put(
                "NOME",
                adesione
                        .getPartecipante()
                        .getNome()
        );


        /*
         * ========================================================
         * QUALIFICA
         * ========================================================
         *
         * Partecipante contiene:
         *
         * private Integer idQualifica;
         *
         * Recuperiamo quindi la descrizione dalla tabella
         * qualifica.
         */

        String qualifica = "";


        Integer idQualifica =
                adesione
                        .getPartecipante()
                        .getIdQualifica();


        if (idQualifica != null) {

            qualifica =
                    qualificaRepository
                            .findById(idQualifica)
                            .map(q -> q.getDescrizione())
                            .orElse("");
        }


        parameters.put(
                "QUALIFICA",
                qualifica
        );


        /*
         * ========================================================
         * PELLEGRINAGGIO
         * ========================================================
         */

        parameters.put(
                "ID_PELLEGRINAGGIO",
                adesione
                        .getPellegrinaggio()
                        .getId()
        );


        parameters.put(
                "PELLEGRINAGGIO",
                adesione
                        .getPellegrinaggio()
                        .getNome()
        );


        parameters.put(
                "DATA_PARTENZA",
                adesione
                        .getPellegrinaggio()
                        .getDataPartenza()
                        .format(formatter)
        );


        parameters.put(
                "DATA_RITORNO",
                adesione
                        .getPellegrinaggio()
                        .getDataRitorno()
                        .format(formatter)
        );


        /*
         * ========================================================
         * ADESIONE
         * ========================================================
         */

        parameters.put(
                "ID_ADESIONE",
                adesione.getId()
        );


        parameters.put(
                "DATA_ADESIONE",
                adesione
                        .getDataAdesione()
                        .format(formatter)
        );


        /*
         * ========================================================
         * DATI SERVIZIO
         * ========================================================
         *
         * Per ora lasciamo questi parametri vuoti.
         */

        parameters.put(
                "DATA_SERVIZIO",
                ""
        );

        parameters.put(
                "LUOGO_SERVIZIO",
                ""
        );

        parameters.put(
                "NOTE_SERVIZIO",
                ""
        );


        /*
         * ========================================================
         * RECUPERO SPOSTAMENTI DEL PARTECIPANTE
         * ========================================================
         */

        List<PartecipanteSpostamento> associazioni =
                partecipanteSpostamentoRepository
                        .findByIdIdPartecipante(
                                adesione
                                        .getPartecipante()
                                        .getId()
                        );


        List<SpostamentoReport> spostamentiReport =
                new ArrayList<>();


        /*
         * ========================================================
         * COSTRUZIONE LISTA SPOSTAMENTI
         * ========================================================
         */

        for (
                PartecipanteSpostamento associazione
                : associazioni
        ) {


            /*
             * ID SPOSTAMENTO
             */

            Integer idSpostamento =
                    associazione
                            .getId()
                            .getIdSpostamento();


            /*
             * RECUPERA SPOSTAMENTO
             */

            Spostamento spostamento =
                    spostamentoRepository
                            .findById(idSpostamento)
                            .orElse(null);


            if (spostamento == null) {
                continue;
            }


            /*
             * ====================================================
             * SOLO SPOSAMENTI DEL PELLEGRINAGGIO CORRENTE
             * ====================================================
             */

            if (
                    spostamento.getPellegrinaggio() == null
                    ||
                    !spostamento
                            .getPellegrinaggio()
                            .getId()
                            .equals(
                                    adesione
                                            .getPellegrinaggio()
                                            .getId()
                            )
            ) {

                continue;
            }


            /*
             * ====================================================
             * MEZZO
             * ====================================================
             */

            Integer idMezzo = null;

            String descrizioneMezzo = "";


            if (spostamento.getTipMezzo() != null) {

                idMezzo =
                        spostamento
                                .getTipMezzo()
                                .getId();


                descrizioneMezzo =
                        spostamento
                                .getTipMezzo()
                                .getDescrizione();
            }
            
            
            String descrizioneViaggio = "";

            if (spostamento.getTipViaggio() != null) {

                descrizioneViaggio =
                        spostamento.getTipViaggio().getDescrizione();
            }



            /*
             * ====================================================
             * DATA PARTENZA
             * ====================================================
             */

            String dataPartenza = "";


            if (
                    spostamento.getDataPartenza()
                    != null
            ) {

                dataPartenza =
                        spostamento
                                .getDataPartenza()
                                .format(formatter);
            }


            /*
             * ====================================================
             * DATA ARRIVO
             * ====================================================
             */

            String dataArrivo = "";


            if (
                    spostamento.getDataArrivo()
                    != null
            ) {

                dataArrivo =
                        spostamento
                                .getDataArrivo()
                                .format(formatter);
            }
            
            /*
             * ====================================================
             * RESPONSABILI SPOSTAMENTO
             * ====================================================
             */
            
            List<ResponsabileSpostamentoReport> responsabiliReport =
                    new ArrayList<>();


            List<ResponsabileSpostamento> responsabili =
                    responsabileSpostamentoRepository
                            .findByIdIdSpostamento(
                                    spostamento.getId()
                            );


            for (ResponsabileSpostamento responsabile : responsabili) {

                Integer idPartecipanteResponsabile =
                        responsabile
                                .getId()
                                .getIdPartecipante();


                Partecipante partecipanteResponsabile =
                        partecipanteRepository
                                .findById(idPartecipanteResponsabile)
                                .orElse(null);


                if (partecipanteResponsabile == null) {
                    continue;
                }


                responsabiliReport.add(
                        new ResponsabileSpostamentoReport(
                          
                                partecipanteResponsabile
                                        .getCognome(),

                                partecipanteResponsabile
                                        .getNome()
                        )
                );
            }



            /*
             * ====================================================
             * CREA DTO
             * ====================================================
             */

            spostamentiReport.add(
                    new SpostamentoReport(

                            idMezzo,

                            descrizioneMezzo,
                                                  
                            dataPartenza,

                            dataArrivo,

                            spostamento
                                    .getLuogoPartenza(),

                            spostamento
                                    .getLuogoArrivo(),

                            spostamento
                                    .getNote(),
                                    
                            descrizioneViaggio,
                                    
                          responsabiliReport
                    )
            );
        }


        /*
         * ========================================================
         * ORDINAMENTO
         * ========================================================
         *
         * 1. data partenza crescente
         *
         * 2. a parità di data:
         *    id mezzo 2 (AUTOBUS) prima degli altri
         *
         */

        spostamentiReport.sort(
        	    Comparator
        	        .comparing(
        	            SpostamentoReport::getDataPartenza,
        	            Comparator.nullsLast(String::compareTo)
        	        )
        	        .thenComparing(
        	            s -> s.getIdMezzo() != null && s.getIdMezzo() == 2
        	                    ? 0
        	                    : 1
        	        )
        	        .thenComparing(
        	            SpostamentoReport::getIdMezzo,
        	            Comparator.nullsLast(Integer::compareTo)
        	        )
        	);



        /*
         * ========================================================
         * PARAMETRO SPOSTAMENTI
         * ========================================================
         *
         * Passiamo direttamente la lista a Jasper.
         *
         * Nel JRXML potremo utilizzarla con un subDataset.
         */

        parameters.put(
                "SPOSTAMENTI",
                spostamentiReport
        );
        
        
        /*
         * ========================================================
         * RECUPERO SISTEMAZIONI DEL PARTECIPANTE
         * ========================================================
         */

        Integer idPartecipante =
                adesione
                        .getPartecipante()
                        .getId();

        Integer idPellegrinaggio =
                adesione
                        .getPellegrinaggio()
                        .getId();


        List<PartecipanteSistemazione> associazioniSistemazione =
                partecipanteSistemazioneRepository
                        .findByPartecipanteIdAndCameraSistemazioneIdPellegrinaggio(
                                idPartecipante,
                                idPellegrinaggio
                        );


        List<SistemazioneReport> sistemazioniReport =
                new ArrayList<>();


        for (
                PartecipanteSistemazione associazione
                : associazioniSistemazione
        ) {

            /*
             * ====================================================
             * CAMERA
             * ====================================================
             */

            if (associazione.getCamera() == null) {
                continue;
            }


            Integer idCamera =
                    associazione
                            .getCamera()
                            .getId();


            /*
             * ====================================================
             * SISTEMAZIONE
             * ====================================================
             */

            if (associazione.getCamera().getSistemazione() == null) {
                continue;
            }


            var sistemazione =
                    associazione
                            .getCamera()
                            .getSistemazione();


            /*
             * ====================================================
             * DESCRIZIONI
             * ====================================================
             */

            String descrizioneSistemazione =
                    sistemazione.getDescrizione() == null
                            ? ""
                            : sistemazione.getDescrizione();


            String descrizioneCamera =
                    associazione
                            .getCamera()
                            .getDescrizione() == null
                            ? ""
                            : associazione
                                    .getCamera()
                                    .getDescrizione();


            /*
             * ====================================================
             * ALLLOGGIA CON
             * ====================================================
             *
             * Recuperiamo tutti gli altri partecipanti
             * associati alla stessa camera.
             */

            List<PartecipanteSistemazione> occupantiCamera =
                    partecipanteSistemazioneRepository
                            .findByCameraId(idCamera);


            List<String> nominativi =
                    new ArrayList<>();


            for (
                    PartecipanteSistemazione occupante
                    : occupantiCamera
            ) {

                if (occupante.getPartecipante() == null) {
                    continue;
                }


                Integer idOccupante =
                        occupante
                                .getPartecipante()
                                .getId();


                /*
                 * Non inseriamo il partecipante
                 * proprietario del foglio di servizio.
                 */

                if (
                        idOccupante == null
                        ||
                        idOccupante.equals(idPartecipante)
                ) {
                    continue;
                }


                String cognome =
                        occupante
                                .getPartecipante()
                                .getCognome();

                String nome =
                        occupante
                                .getPartecipante()
                                .getNome();


                String nominativo =
                        (
                                cognome == null
                                        ? ""
                                        : cognome
                        )
                        + " "
                        +
                        (
                                nome == null
                                        ? ""
                                        : nome
                        );


                nominativo =
                        nominativo.trim();


                if (!nominativo.isEmpty()) {

                    nominativi.add(
                            nominativo
                    );
                }
            }


            /*
             * ====================================================
             * ORDINA ALLOGGIA CON
             * ====================================================
             */

            nominativi.sort(
                    String.CASE_INSENSITIVE_ORDER
            );


            String alloggiaCon =
                    String.join(
                            ", ",
                            nominativi
                    );


            /*
             * ====================================================
             * CREA DTO
             * ====================================================
         */

            sistemazioniReport.add(
                    new SistemazioneReport(

                            associazione.getId(),

                            idPartecipante,

                            idCamera,

                            sistemazione.getId(),

                            descrizioneSistemazione,

                            descrizioneCamera,

                            alloggiaCon
                    )
            );
        }


        /*
         * ========================================================
         * PARAMETRO SISTEMAZIONI
         * ========================================================
         */

        parameters.put(
                "SISTEMAZIONI",
                sistemazioniReport
        );
        
        /*
         * ========================================================
         * RECUPERO SERVIZI DEL PARTECIPANTE
         * ========================================================
         */

        List<PartecipanteEvento> eventiPartecipante =
                partecipanteEventoRepository
                        .findByIdPartecipanteAndIdPellegrinaggio(
                                idPartecipante,
                                idPellegrinaggio
                        );


        List<ServizioReport> serviziReport =
                new ArrayList<>();


        for (PartecipanteEvento partecipanteEvento
                : eventiPartecipante) {


            /*
             * ====================================================
             * EVENTO
             * ====================================================
             */

            if (partecipanteEvento.getEvento() == null) {
                continue;
            }


            Eventi evento =
                    partecipanteEvento.getEvento();


            /*
             * ====================================================
             * SOLO EVENTI DI TIPO 1 = SERVIZIO
             * ====================================================
             */

            if (
                    evento.getTipoEvento() == null
                    ||
                    evento.getTipoEvento().getId() == null
                    ||
                    !evento.getTipoEvento().getId().equals(1)
            ) {
                continue;
            }


            /*
             * ====================================================
             * CONTROLLO PELLEGRINAGGIO
             * ====================================================
             */

            if (
                    evento.getIdPellegrinaggio() == null
                    ||
                    !evento
                            .getIdPellegrinaggio()
                            .equals(idPellegrinaggio)
            ) {
                continue;
            }


            /*
             * ====================================================
             * ID TABELLA
             * ====================================================
             */

            Integer idTabella = null;

            if (evento.getTabella() != null) {
                idTabella = evento.getTabella().getId();
            }


            /*
             * ====================================================
             * VALORE SERVIZIO
             * ====================================================
             *
             * id_tabella = 1
             * ----------------
             * SCELTA SI/NO
             * Non mostriamo id_valore_tabella.
             *
             * id_tabella = 2
             * ----------------
             * SERVIZI
             * id_valore_tabella = servizi.id
             */

            Integer idValoreServizio =
                    partecipanteEvento.getIdValoreTabella();


            String valoreServizio = "";


            if (Integer.valueOf(2).equals(idTabella)) {

                if (idValoreServizio != null) {

                    valoreServizio =
                            servizioRepository
                                    .findByIdAndIdPellegrinaggio(
                                            idValoreServizio,
                                            idPellegrinaggio
                                    )
                                    .map(Servizio::getDescrizione)
                                    .orElse("");
                }
            }


            /*
             * ====================================================
             * RESPONSABILI SERVIZIO
             * ====================================================
             *
             * RUOLI:
             *
             * 1 = Capo Servizio
             * 4 = Assistenza Medica
             * 5 = Assistenza Spirituale
             *
             * Recuperiamo tutti i partecipanti associati
             * allo stesso evento e pellegrinaggio per
             * ciascuno dei tre ruoli.
             */


            /*
             * ====================================================
             * CAPI SERVIZIO - RUOLO 1
             * ====================================================
             */

            String capi = recuperaNominativiRuolo(
                    evento.getId(),
                    idPellegrinaggio,
                    1
            );


            /*
             * ====================================================
             * ASSISTENZA MEDICA - RUOLO 4
             * ====================================================
             */

            String assistenzaMedica = recuperaNominativiRuolo(
                    evento.getId(),
                    idPellegrinaggio,
                    4
            );


            /*
             * ====================================================
             * ASSISTENZA SPIRITUALE - RUOLO 5
             * ====================================================
             */

            String assistenzaSpirituale = recuperaNominativiRuolo(
                    evento.getId(),
                    idPellegrinaggio,
                    5
            );



            /*
             * ====================================================
             * ACCOMPAGNA / CON
             * ====================================================
             *
             * SOLO per:
             *
             * descrizione = "Servizio in Aeroporto"
             *
             * id_tabella = 1
             *
             * La presenza del record in partecipante_evento
             * rappresenta il SI.
             */

            String accompagna = "";
            String con = "";


            boolean servizioInAeroporto =
                    "Servizio in Aeroporto".equalsIgnoreCase(
                            evento.getDescrizione() == null
                                    ? ""
                                    : evento.getDescrizione().trim()
                    )
                    &&
                    Integer.valueOf(1).equals(idTabella);


            if (servizioInAeroporto) {


                /*
                 * ====================================================
                 * EVENTI TIPO 3 DEL PARTECIPANTE
                 * ====================================================
                 */

                List<PartecipanteEvento> eventiAccompagno =
                        eventiPartecipante
                                .stream()
                                .filter(
                                        pe -> pe.getEvento() != null
                                )
                                .filter(
                                        pe ->
                                                pe.getEvento()
                                                        .getTipoEvento() != null
                                )
                                .filter(
                                        pe ->
                                                pe.getEvento()
                                                        .getTipoEvento()
                                                        .getId() != null
                                )
                                .filter(
                                        pe ->
                                                pe.getEvento()
                                                        .getTipoEvento()
                                                        .getId()
                                                        .equals(3)
                                )
                                .filter(
                                        pe ->
                                                pe.getEvento()
                                                        .getIdPellegrinaggio() != null
                                                &&
                                                pe.getEvento()
                                                        .getIdPellegrinaggio()
                                                        .equals(
                                                                idPellegrinaggio
                                                        )
                                )
                                .filter(
                                        pe ->
                                                pe.getIdPartecipante() != null
                                                &&
                                                pe.getIdPartecipante()
                                                        .equals(idPartecipante)
                                )
                                .toList();


                List<String> listaAccompagna =
                        new ArrayList<>();


                List<String> listaCon =
                        new ArrayList<>();


                /*
                 * ====================================================
                 * COSTRUZIONE ACCOMPAGNA / CON
                 * ====================================================
                 */

                for (PartecipanteEvento peTipo3
                        : eventiAccompagno) {


                    Eventi eventoTipo3 =
                            peTipo3.getEvento();


                    if (eventoTipo3 == null) {
                        continue;
                    }


                    String descrizioneTipo3 =
                            eventoTipo3.getDescrizione() == null
                                    ? ""
                                    : eventoTipo3
                                            .getDescrizione()
                                            .trim();


                    /*
                     * ====================================================
                     * ACCOMPAGNA
                     * ====================================================
                     *
                     * Deve esistere realmente una riga
                     * partecipante_evento.
                     *
                     * id_valore_tabella =
                     * partecipante accompagnato.
                     */

                    if (
                            "ACCOMPAGNA".equalsIgnoreCase(
                                    descrizioneTipo3
                            )
                    ) {

                        Integer idPartecipanteAccompagnato =
                                peTipo3.getIdValoreTabella();


                        if (idPartecipanteAccompagnato != null) {

                            Partecipante partecipanteAccompagnato =
                                    partecipanteRepository
                                            .findById(
                                                    idPartecipanteAccompagnato
                                            )
                                            .orElse(null);


                            if (partecipanteAccompagnato != null) {

                                String cognome =
                                        partecipanteAccompagnato
                                                .getCognome() == null
                                                ? ""
                                                : partecipanteAccompagnato
                                                        .getCognome();


                                String nome =
                                        partecipanteAccompagnato
                                                .getNome() == null
                                                ? ""
                                                : partecipanteAccompagnato
                                                        .getNome();


                                String nominativo =
                                        (
                                                cognome
                                                + " "
                                                + nome
                                        ).trim();


                                if (!nominativo.isEmpty()) {

                                    listaAccompagna.add(
                                            nominativo
                                    );
                                }
                            }
                        }
                    }


                    /*
                     * ====================================================
                     * CON
                     * ====================================================
                     *
                     * Anche CON viene mostrato SOLO se esiste
                     * realmente la riga partecipante_evento.
                     *
                     * NON viene mai aggiunto automaticamente
                     * il partecipante del foglio.
                     */

                    if (
                            "CON".equalsIgnoreCase(
                                    descrizioneTipo3
                            )
                    ) {

                        Integer idPartecipanteCon =
                                peTipo3.getIdPartecipante();


                        if (
                                idPartecipanteCon != null
                                &&
                                !idPartecipanteCon.equals(
                                        idPartecipante
                                )
                        ) {

                            Partecipante partecipanteCon =
                                    partecipanteRepository
                                            .findById(
                                                    idPartecipanteCon
                                            )
                                            .orElse(null);


                            if (partecipanteCon != null) {

                                String cognome =
                                        partecipanteCon
                                                .getCognome() == null
                                                ? ""
                                                : partecipanteCon
                                                        .getCognome();


                                String nome =
                                        partecipanteCon
                                                .getNome() == null
                                                ? ""
                                                : partecipanteCon
                                                        .getNome();


                                String nominativo =
                                        (
                                                cognome
                                                + " "
                                                + nome
                                        ).trim();


                                if (!nominativo.isEmpty()) {

                                    listaCon.add(
                                            nominativo
                                    );
                                }
                            }
                        }
                    }
                }


                /*
                 * ====================================================
                 * ORDINA E RIMUOVE DUPLICATI
                 * ====================================================
                 */

                listaAccompagna =
                        listaAccompagna
                                .stream()
                                .distinct()
                                .sorted(
                                        String.CASE_INSENSITIVE_ORDER
                                )
                                .toList();


                listaCon =
                        listaCon
                                .stream()
                                .distinct()
                                .sorted(
                                        String.CASE_INSENSITIVE_ORDER
                                )
                                .toList();


                accompagna =
                        String.join(
                                ", ",
                                listaAccompagna
                        );


                con =
                        String.join(
                                ", ",
                                listaCon
                        );
            }


            /*
             * ====================================================
             * CREA DTO SERVIZIO
             * ====================================================
             */

            serviziReport.add(
                    new ServizioReport(
                            evento.getId(),
                            evento.getDescrizione(),
                            valoreServizio,
                            capi,
                            assistenzaMedica,
                            assistenzaSpirituale,
                            accompagna,
                            con
                    )
            );

        }
        
        /*
         * ========================================================
         * ORDINA SERVIZI PER ID EVENTO
         * ========================================================
         */

        serviziReport.sort(
                Comparator.comparing(
                        ServizioReport::getId,
                        Comparator.nullsLast(Integer::compareTo)
                )
        );


        /*
         * ========================================================
         * PARAMETRO SERVIZI
         * ========================================================
         */

        parameters.put(
                "SERVIZI",
                serviziReport
        );


        
        
        /*
         * ========================================================
         * RECUPERO MEDAGLIE DEL PARTECIPANTE
         * ========================================================
         *
         * tipoEvento.id = 2
         */

        List<MedagliaReport> medaglieReport =
                new ArrayList<>();


        for (PartecipanteEvento partecipanteEvento
                : eventiPartecipante) {

            /*
             * ====================================================
             * EVENTO
             * ====================================================
             */

            if (partecipanteEvento.getEvento() == null) {
                continue;
            }


            Eventi evento =
                    partecipanteEvento.getEvento();


            /*
             * ====================================================
             * SOLO EVENTI DI TIPO 2 = MEDAGLIA
             * ====================================================
             */

            if (
                    evento.getTipoEvento() == null
                    ||
                    evento.getTipoEvento().getId() == null
                    ||
                    !evento
                            .getTipoEvento()
                            .getId()
                            .equals(2)
            ) {
                continue;
            }


            /*
             * ====================================================
             * CONTROLLO PELLEGRINAGGIO
             * ====================================================
             */

            if (
                    evento.getIdPellegrinaggio() == null
                    ||
                    !evento
                            .getIdPellegrinaggio()
                            .equals(idPellegrinaggio)
            ) {
                continue;
            }


            /*
             * ====================================================
             * NOTE
             * ====================================================
             */

            String note =
                    evento.getNote() == null
                            ? ""
                            : evento.getNote();


            /*
             * ====================================================
             * CREA DTO
             * ====================================================
             */

            medaglieReport.add(
                    new MedagliaReport(
                            evento.getId(),
                            note
                    )
            );
        }


        /*
         * ========================================================
         * PARAMETRO JASPER
         * ========================================================
         */

        parameters.put(
                "MEDAGLIE",
                medaglieReport
        );
        
        parameters.put(
                "HAS_SPOSTAMENTI",
                !spostamentiReport.isEmpty()
        );


        parameters.put(
                "HAS_SISTEMAZIONI",
                !sistemazioniReport.isEmpty()
        );

        parameters.put(
                "HAS_SERVIZI",
                !serviziReport.isEmpty()
        );

        parameters.put(
                "HAS_MEDAGLIE",
                !medaglieReport.isEmpty()
        );


        
        











        /*
         * ========================================================
         * CARICA REPORT
         * ========================================================
         */

        ClassPathResource reportResource =
                new ClassPathResource(
                        "reports/foglio_servizio.jrxml"
                );


        if (!reportResource.exists()) {

            throw new IllegalArgumentException(
                    "Report foglio_servizio.jrxml non trovato"
            );
        }


        /*
         * ========================================================
         * COMPILA JRXML
         * ========================================================
         */

        JasperReport jasperReport;


        try (
                InputStream reportInputStream =
                        reportResource.getInputStream()
        ) {

            jasperReport =
                    JasperCompileManager
                            .compileReport(
                                    reportInputStream
                            );
        }


        /*
         * ========================================================
         * GENERA REPORT
         * ========================================================
         */
        
        parameters.put(
                "EXPORT_EXCEL",
                Boolean.TRUE
        );

        parameters.put(
                JRParameter.IS_IGNORE_PAGINATION,
                Boolean.TRUE
        );

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        parameters,
                        new JREmptyDataSource()
                );


        /*
         * ========================================================
         * ESPORTA PDF
         * ========================================================
         */

//        return JasperExportManager
//                .exportReportToPdf(
//                        jasperPrint
//                );
        
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(
                new SimpleExporterInput(jasperPrint)
        );

        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(outputStream)
        );

        SimpleXlsxReportConfiguration configuration =
                new SimpleXlsxReportConfiguration();

        configuration.setOnePagePerSheet(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setRemoveEmptySpaceBetweenColumns(true);
        configuration.setWhitePageBackground(false);

        exporter.setConfiguration(configuration);

        exporter.exportReport();

        return outputStream.toByteArray();

    }
    
    
    private String recuperaNominativiRuolo(
            Integer idEvento,
            Integer idPellegrinaggio,
            Integer idRuolo) {

        List<PartecipanteEvento> partecipanti =
                partecipanteEventoRepository
                        .findByEvento_IdAndIdPellegrinaggioAndRuolo_Id(
                                idEvento,
                                idPellegrinaggio,
                                idRuolo
                        );


        List<String> nominativi =
                new ArrayList<>();


        for (PartecipanteEvento partecipanteEvento
                : partecipanti) {

            if (partecipanteEvento.getIdPartecipante() == null) {
                continue;
            }


            Partecipante partecipante =
                    partecipanteRepository
                            .findById(
                                    partecipanteEvento
                                            .getIdPartecipante()
                            )
                            .orElse(null);


            if (partecipante == null) {
                continue;
            }


            String cognome =
                    partecipante.getCognome() == null
                            ? ""
                            : partecipante.getCognome();


            String nome =
                    partecipante.getNome() == null
                            ? ""
                            : partecipante.getNome();


            String nominativo =
                    (
                            cognome
                            + " "
                            + nome
                    ).trim();


            if (!nominativo.isEmpty()) {

                nominativi.add(
                        nominativo
                );
            }
        }


        return nominativi
                .stream()
                .distinct()
                .sorted(
                        String.CASE_INSENSITIVE_ORDER
                )
                .collect(
                        java.util.stream.Collectors.joining(", ")
                );
    }

    
    
    
    
    

}
