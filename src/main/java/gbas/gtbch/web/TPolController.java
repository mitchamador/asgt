package gbas.gtbch.web;

import gbas.gtbch.sapod.repository.TPolRepository;
import gbas.gtbch.sapod.repository.TPolRowRepository;
import gbas.tvk.tpol3.TpolDocument;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/tpol")
public class TPolController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final TPolRepository tpolRepository;
    private final TPolRowRepository tpolRowRepository;

    public TPolController(TPolRepository tpolRepository, TPolRowRepository tpolRowRepository) {
        this.tpolRepository = tpolRepository;
        this.tpolRowRepository = tpolRowRepository;
    }


    /**
     * get {@link TpolDocument} list
     * @param dateBegin start period date (dd.MM.yyyy)
     * @param dateEnd end period date (dd.MM.yyyy)
     */
    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public ResponseEntity<List<TpolDocument>> getDocuments(
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {
        return new ResponseEntity<>(tpolRepository.getDocuments(dateBegin, dateEnd), HttpStatus.OK);
    }

    /**
     * get {@link TPRow} list
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/rows/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TPRow>> getRows(@PathVariable int id) {
        return new ResponseEntity<>(tpolRowRepository.getRows(id), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/ttar", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getTTar() {
        return new ResponseEntity<>(tpolRepository.getBaseTarifList(), HttpStatus.OK);
    }
}