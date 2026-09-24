package it.pellegrinaggi.service;

import it.pellegrinaggi.model.Camera;
import it.pellegrinaggi.model.Partecipante;
import it.pellegrinaggi.model.PartecipanteSistemazione;
import it.pellegrinaggi.model.Sistemazione;
import it.pellegrinaggi.repository.CameraRepository;
import it.pellegrinaggi.repository.PartecipanteRepository;
import it.pellegrinaggi.repository.PartecipanteSistemazioneRepository;
import it.pellegrinaggi.repository.SistemazioneRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PartecipanteSistemazioneService {


    private final PartecipanteSistemazioneRepository
            partecipanteSistemazioneRepository;

    private final CameraRepository cameraRepository;

    private final SistemazioneRepository sistemazioneRepository;

    private final PartecipanteRepository partecipanteRepository;


    public PartecipanteSistemazioneService(
            PartecipanteSistemazioneRepository
                    partecipanteSistemazioneRepository,

            CameraRepository cameraRepository,

            SistemazioneRepository sistemazioneRepository,

            PartecipanteRepository partecipanteRepository) {

        this.partecipanteSistemazioneRepository =
                partecipanteSistemazioneRepository;

        this.cameraRepository =
                cameraRepository;

        this.sistemazioneRepository =
                sistemazioneRepository;

        this.partecipanteRepository =
                partecipanteRepository;
    }


    // ============================================================
    // ASSEGNA PARTECIPANTI
    // ============================================================

    @Transactional
    public void assegnaPartecipanti(
            Integer adesioneId,
            Integer idSistemazione,
            Integer idCamera,
            String nuovaCamera,
            List<Integer> idPartecipanti) {

        /*
         * Recuperiamo la sistemazione
         */
        Sistemazione sistemazione =
                sistemazioneRepository
                        .findById(idSistemazione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sistemazione non trovata"
                                )
                        );

        /*
         * Il pellegrinaggio viene ricavato dalla sistemazione.
         */
        Integer idPellegrinaggio =
                sistemazione.getIdPellegrinaggio();

        Camera camera;


        // ============================================================
        // CAMERA ESISTENTE
        // ============================================================

        if (idCamera != null) {

            camera =
                    cameraRepository
                            .findById(idCamera)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Camera non trovata"
                                    )
                            );


            /*
             * Controlliamo che la camera appartenga
             * alla sistemazione selezionata.
             */
            if (camera.getSistemazione() == null ||
                    !camera.getSistemazione()
                            .getId()
                            .equals(sistemazione.getId())) {

                throw new IllegalArgumentException(
                        "La camera non appartiene alla sistemazione selezionata"
                );
            }

        }


        // ============================================================
        // NUOVA CAMERA
        // ============================================================

        else {

            if (nuovaCamera == null ||
                    nuovaCamera.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Specificare la descrizione della nuova camera"
                );
            }


            camera = new Camera();

            camera.setSistemazione(
                    sistemazione
            );

            camera.setDescrizione(
                    nuovaCamera.trim()
            );


            camera =
                    cameraRepository.save(camera);
        }


        // ============================================================
        // PARTECIPANTI
        // ============================================================

        if (idPartecipanti == null ||
                idPartecipanti.isEmpty()) {

            return;
        }


        /*
         * IMPORTANTE:
         *
         * Salviamo l'id della camera in una variabile final.
         *
         * Serve perché la variabile "camera" viene assegnata
         * nei due rami precedenti e quindi Java non la considera
         * effectively final quando viene utilizzata nella lambda.
         */
        final Integer idCameraFinale =
                camera.getId();


        for (Integer idPartecipante :
                idPartecipanti) {


            /*
             * Recuperiamo il partecipante.
             */
            Partecipante partecipante =
                    partecipanteRepository
                            .findById(idPartecipante)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Partecipante non trovato: "
                                                    + idPartecipante
                                    )
                            );


            /*
             * Controlliamo che il partecipante abbia
             * un'adesione a questo pellegrinaggio.
             */
            boolean appartiene =
                    partecipante
                            .getAdesioni()
                            .stream()
                            .anyMatch(adesione ->
                                    adesione
                                            .getPellegrinaggio()
                                            .getId()
                                            .equals(
                                                    idPellegrinaggio
                                            )
                            );


            if (!appartiene) {

                throw new IllegalArgumentException(
                        "Il partecipante "
                                + partecipante.getCognome()
                                + " "
                                + partecipante.getNome()
                                + " non appartiene al pellegrinaggio"
                );
            }


            /*
             * Recuperiamo SOLO la sistemazione del partecipante
             * relativa a questo pellegrinaggio.
             *
             * Questo è importante perché lo stesso partecipante
             * può avere sistemazioni in pellegrinaggi diversi.
             */
            List<PartecipanteSistemazione> vecchie =
                    partecipanteSistemazioneRepository
                            .findByPartecipanteIdAndCameraSistemazioneIdPellegrinaggio(
                                    partecipante.getId(),
                                    idPellegrinaggio
                            );


            /*
             * Controlliamo se il partecipante è già
             * nella camera selezionata.
             */
            boolean giaAssociato =
                    vecchie.stream()
                            .anyMatch(ps ->
                                    ps.getCamera()
                                            .getId()
                                            .equals(
                                                    idCameraFinale
                                            )
                            );


            /*
             * Se è già nella camera non facciamo nulla.
             */
            if (giaAssociato) {
                continue;
            }


            /*
             * Se il partecipante era già sistemato
             * in un'altra camera dello stesso pellegrinaggio,
             * eliminiamo SOLO quella vecchia associazione.
             *
             * Eventuali sistemazioni dello stesso partecipante
             * in altri pellegrinaggi NON vengono toccate.
             */
            if (!vecchie.isEmpty()) {

                partecipanteSistemazioneRepository
                        .deleteAll(vecchie);
            }


            /*
             * Creiamo la nuova associazione.
             */
            PartecipanteSistemazione nuova =
                    new PartecipanteSistemazione();


            nuova.setPartecipante(
                    partecipante
            );


            nuova.setCamera(
                    camera
            );


            partecipanteSistemazioneRepository
                    .save(nuova);
        }
    }



    // ============================================================
    // ALLOGGIA CON
    // ============================================================

    @Transactional
    public void alloggiaCon(
            Integer idPartecipanteSistemazione,
            List<Integer> idPartecipanti) {


        /*
         * Recuperiamo l'associazione principale.
         */

        PartecipanteSistemazione principale =
                partecipanteSistemazioneRepository
                        .findById(
                                idPartecipanteSistemazione
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sistemazione non trovata"
                                )
                        );


        Camera camera =
                principale.getCamera();


        Integer idPellegrinaggio =
                camera.getSistemazione()
                        .getIdPellegrinaggio();


        if (idPartecipanti == null ||
                idPartecipanti.isEmpty()) {

            return;
        }


        for (Integer idPartecipante :
                idPartecipanti) {


            Partecipante partecipante =
                    partecipanteRepository
                            .findById(idPartecipante)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Partecipante non trovato"
                                    )
                            );


            /*
             * Controlliamo se è già nella camera
             * NELLO STESSO PELLEGRINAGGIO.
             */

            boolean giaNellaCamera =
                    partecipanteSistemazioneRepository
                            .findByPartecipanteIdAndCameraIdAndCameraSistemazioneIdPellegrinaggio(
                                    partecipante.getId(),
                                    camera.getId(),
                                    idPellegrinaggio
                            )
                            .isPresent();


            if (giaNellaCamera) {
                continue;
            }


            /*
             * Recuperiamo SOLO le vecchie associazioni
             * di questo pellegrinaggio.
             */

            List<PartecipanteSistemazione> vecchie =
                    partecipanteSistemazioneRepository
                            .findByPartecipanteIdAndCameraSistemazioneIdPellegrinaggio(
                                    partecipante.getId(),
                                    idPellegrinaggio
                            );


            /*
             * Eliminiamo solo la sistemazione
             * relativa a questo pellegrinaggio.
             */

            if (!vecchie.isEmpty()) {

                partecipanteSistemazioneRepository
                        .deleteAll(vecchie);
            }


            /*
             * Nuova associazione.
             */

            PartecipanteSistemazione nuova =
                    new PartecipanteSistemazione();

            nuova.setPartecipante(
                    partecipante
            );

            nuova.setCamera(
                    camera
            );


            partecipanteSistemazioneRepository
                    .save(nuova);
        }
    }


    // ============================================================
    // SPOSTA
    // ============================================================

    @Transactional
    public void sposta(
            Integer idPartecipanteSistemazione,
            Integer idCamera) {


        PartecipanteSistemazione ps =
                partecipanteSistemazioneRepository
                        .findById(
                                idPartecipanteSistemazione
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Associazione non trovata"
                                )
                        );


        Camera nuovaCamera =
                cameraRepository
                        .findById(idCamera)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Camera non trovata"
                                )
                        );


        Integer pellegrinaggioAttuale =
                ps.getCamera()
                        .getSistemazione()
                        .getIdPellegrinaggio();


        Integer nuovoPellegrinaggio =
                nuovaCamera
                        .getSistemazione()
                        .getIdPellegrinaggio();


        /*
         * Non permettiamo di spostare un partecipante
         * in una camera di un altro pellegrinaggio.
         */

        if (!pellegrinaggioAttuale
                .equals(nuovoPellegrinaggio)) {

            throw new IllegalArgumentException(
                    "La nuova camera appartiene " +
                    "ad un altro pellegrinaggio"
            );
        }


        /*
         * Se è la stessa camera non facciamo nulla.
         */

        if (ps.getCamera()
                .getId()
                .equals(nuovaCamera.getId())) {

            return;
        }


        /*
         * Cambiamo semplicemente la camera.
         */

        ps.setCamera(
                nuovaCamera
        );


        partecipanteSistemazioneRepository
                .save(ps);
    }


    // ============================================================
    // ELIMINA
    // ============================================================

    @Transactional
    public void elimina(
            Integer partecipanteId,
            Integer idPartecipanteSistemazione) {


        PartecipanteSistemazione ps =
                partecipanteSistemazioneRepository
                        .findById(
                                idPartecipanteSistemazione
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Associazione non trovata"
                                )
                        );


        /*
         * Controllo di sicurezza.
         */

        if (!ps.getPartecipante()
                .getId()
                .equals(partecipanteId)) {

            throw new IllegalArgumentException(
                    "L'associazione non appartiene al partecipante"
            );
        }


        /*
         * Elimina SOLO il collegamento
         * partecipante -> camera.
         *
         * Non elimina il partecipante.
         * Non elimina la camera.
         * Non elimina l'hotel.
         */

        partecipanteSistemazioneRepository
                .delete(ps);
    }
    
    @Transactional
    public void sposta(
            Integer idPartecipanteSistemazione,
            Integer idCamera,
            String nuovaCamera) {

        PartecipanteSistemazione ps =
                partecipanteSistemazioneRepository
                        .findById(idPartecipanteSistemazione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Associazione non trovata"
                                )
                        );


        Camera cameraAttuale =
                ps.getCamera();


        Sistemazione sistemazione =
                cameraAttuale.getSistemazione();


        Integer idPellegrinaggio =
                sistemazione.getIdPellegrinaggio();


        Camera nuovaCameraEntity;


        // =========================================================
        // CAMERA ESISTENTE
        // =========================================================

        if (idCamera != null) {

            nuovaCameraEntity =
                    cameraRepository
                            .findById(idCamera)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Camera non trovata"
                                    )
                            );


            /*
             * La camera deve appartenere alla stessa
             * sistemazione/pellegrinaggio.
             */
            if (!nuovaCameraEntity
                    .getSistemazione()
                    .getId()
                    .equals(
                            sistemazione.getId()
                    )) {

                throw new IllegalArgumentException(
                        "La camera appartiene ad un'altra sistemazione"
                );
            }


        }

        // =========================================================
        // NUOVA CAMERA
        // =========================================================

        else {

            if (nuovaCamera == null ||
                    nuovaCamera.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Se non viene selezionata una camera esistente, " +
                        "è necessario specificare la nuova camera"
                );
            }


            String descrizione =
                    nuovaCamera.trim();


            /*
             * Se esiste già una camera con questa descrizione
             * nella stessa sistemazione, la riutilizziamo.
             */
            nuovaCameraEntity =
                    cameraRepository
                            .findBySistemazioneIdAndDescrizione(
                                    sistemazione.getId(),
                                    descrizione
                            )
                            .orElseGet(() -> {

                                Camera camera =
                                        new Camera();

                                camera.setSistemazione(
                                        sistemazione
                                );

                                camera.setDescrizione(
                                        descrizione
                                );

                                return cameraRepository.save(
                                        camera
                                );
                            });
        }


        // =========================================================
        // STESSA CAMERA
        // =========================================================

        if (cameraAttuale.getId()
                .equals(
                        nuovaCameraEntity.getId()
                )) {

            return;
        }


        // =========================================================
        // CONTROLLO DUPLICATO
        // =========================================================

        boolean giaPresente =
                partecipanteSistemazioneRepository
                        .findByPartecipanteIdAndCameraIdAndCameraSistemazioneIdPellegrinaggio(
                                ps.getPartecipante().getId(),
                                nuovaCameraEntity.getId(),
                                idPellegrinaggio
                        )
                        .isPresent();


        if (giaPresente) {

            /*
             * Se esiste già l'associazione,
             * eliminiamo quella vecchia solo se diversa.
             */
            partecipanteSistemazioneRepository
                    .delete(ps);

            return;
        }


        // =========================================================
        // SPOSTAMENTO
        // =========================================================

        ps.setCamera(
                nuovaCameraEntity
        );


        partecipanteSistemazioneRepository
                .save(ps);
    }
    
    
  


}
