package gbas.gtbch.web;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.TPolDocument;
import gbas.gtbch.sapod.model.TPolSobst;
import gbas.gtbch.sapod.model.TpolGroup;
import gbas.gtbch.sapod.repository.TPolRepository;
import gbas.gtbch.sapod.repository.TPolRowRepository;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
     * get {@link TPolDocument} list
     * @param dateBegin start period date (dd.MM.yyyy)
     * @param dateEnd end period date (dd.MM.yyyy)
     */
    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public ResponseEntity<List<TPolDocument>> getDocuments(
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {
        return new ResponseEntity<>(tpolRepository.getDocuments(dateBegin, dateEnd), HttpStatus.OK);
    }

    /**
     * get {@link TPolDocument} list
     * @param typeCode tvk_tarif.type_code ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')
     * @param dateBegin start period date (dd.MM.yyyy)
     * @param dateEnd end period date (dd.MM.yyyy)
     */
    @RequestMapping(value = "/documents/{typeCode}", method = RequestMethod.GET)
    public ResponseEntity<List<TPolDocument>> getDocumentsType(
            @PathVariable String typeCode,
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {
        return new ResponseEntity<>(tpolRepository.getDocuments(typeCode, dateBegin, dateEnd), HttpStatus.OK);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public ResponseEntity<List<TpolGroup>> getGroups() {
        return new ResponseEntity<>(tpolRepository.getGroups(), HttpStatus.OK);
    }

    /**
     * get {@link TPolDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<TPolDocument> getDocument(@PathVariable int id) {
        return new ResponseEntity<>(tpolRepository.getDocument(id), HttpStatus.OK);
    }

    /**
     * create new {@link TPolDocument}
     * @param obj - {@link TPolDocument}
     */
    @RequestMapping(value = "/document", method = RequestMethod.POST)
    public ResponseEntity saveDocument(@RequestBody TPolDocument obj) {
        int id = tpolRepository.saveDocument(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/document/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update {@link TPolDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity updateDocument(@PathVariable int id, @RequestBody TPolDocument obj) {
        obj.id = id;
        return tpolRepository.saveDocument(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete {@link TPolDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteDocument(@PathVariable int id) {
        return new ResponseEntity<>(tpolRepository.deleteDocument(id), HttpStatus.OK);
    }

    /**
     * get {@link TPRow} list
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/rows", method = RequestMethod.GET)
    public ResponseEntity<List<TPRow>> getRows(@PathVariable int id) {
        return new ResponseEntity<>(tpolRowRepository.getRows(id), HttpStatus.OK);
    }

    /**
     * get {@link TPolSobst} list
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/sobsts", method = RequestMethod.GET)
    public ResponseEntity<List<TPolSobst>> getDocumentSobsts(@PathVariable int id, @RequestParam(defaultValue = "false") boolean checked) {
        return new ResponseEntity<>(tpolRepository.getSobstList(id, checked), HttpStatus.OK);
    }

    /**
     * save {@link TPolSobst} list
     * @param id tvk_tarif.id
     * @param list List<{@link TPolSobst}>
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/sobsts", method = RequestMethod.POST)
    public ResponseEntity<Boolean> setDocumentSobsts(@PathVariable int id, @RequestBody List<TPolSobst> list) {
        return new ResponseEntity<>(tpolRepository.saveSobstList(id, list), HttpStatus.OK);
    }

    /**
     * get base tarif list
     * @return
     */
    @RequestMapping(value = "/ttar", method = RequestMethod.GET)
    public ResponseEntity<List<CodeName>> getTTar() {
        return new ResponseEntity<>(tpolRepository.getBaseTarifList(), HttpStatus.OK);
    }

    /**
     * get all {@link TPolSobst} list
     */
    @RequestMapping(value = "/sobsts", method = RequestMethod.GET)
    public ResponseEntity<List<TPolSobst>> getSobsts() {
        return new ResponseEntity<>(tpolRepository.getSobstList(0, false), HttpStatus.OK);
    }

}