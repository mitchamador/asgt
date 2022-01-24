package gbas.gtbch.web;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.tpol.TpDocument;
import gbas.gtbch.sapod.model.tpol.TpGroup;
import gbas.gtbch.sapod.model.tpol.TpRow;
import gbas.gtbch.sapod.model.tpol.TpSobst;
import gbas.gtbch.sapod.service.TPolRowService;
import gbas.gtbch.sapod.service.TPolService;
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

    private final TPolService tpolService;
    private final TPolRowService tpolRowService;

    public TPolController(TPolService tpolService, TPolRowService tpolRowService) {
        this.tpolService = tpolService;
        this.tpolRowService = tpolRowService;
    }


    /**
     * get {@link TpDocument} list
     * @param dateBegin start period date (dd.MM.yyyy)
     * @param dateEnd end period date (dd.MM.yyyy)
     */
    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public ResponseEntity<List<TpDocument>> getDocuments(
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {
        return new ResponseEntity<>(tpolService.getDocuments(dateBegin, dateEnd), HttpStatus.OK);
    }

    /**
     * get {@link TpDocument} list
     * @param typeCode tvk_tarif.type_code ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')
     * @param dateBegin start period date (dd.MM.yyyy)
     * @param dateEnd end period date (dd.MM.yyyy)
     */
    @RequestMapping(value = "/documents/{typeCode}", method = RequestMethod.GET)
    public ResponseEntity<List<TpDocument>> getDocumentsType(
            @PathVariable String typeCode,
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {
        return new ResponseEntity<>(tpolService.getDocuments(typeCode, dateBegin, dateEnd), HttpStatus.OK);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public ResponseEntity<List<TpGroup>> getGroups() {
        return new ResponseEntity<>(tpolService.getGroups(), HttpStatus.OK);
    }

    /**
     * get {@link TpDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<TpDocument> getDocument(@PathVariable int id, @RequestParam(name = "editor", defaultValue = "false", required = false) boolean editorMode) {
        return new ResponseEntity<>(tpolService.getDocument(id, editorMode), HttpStatus.OK);
    }

    /**
     * copy all {@link TpRow}'s from source {@link TpDocument} to destination {@link TpDocument}
     * @param sourceId - tvk_tarif.id
     * @param destinationId - destination tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/copy", method = RequestMethod.POST)
    public ResponseEntity<Integer> copyDocument(@PathVariable(name = "id") int sourceId, @RequestParam(name = "doc_id") int destinationId) {
        return new ResponseEntity<>(tpolService.copyDocument(sourceId, destinationId), HttpStatus.OK);
    }

    /**
     * create new {@link TpDocument}
     * @param obj - {@link TpDocument}
     */
    @RequestMapping(value = "/document", method = RequestMethod.POST)
    public ResponseEntity<Integer> saveDocument(@RequestBody TpDocument obj) {
        int id = tpolService.saveDocument(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/document/" + id)).body(id) : ResponseEntity.noContent().build();
    }

    /**
     * update {@link TpDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity<Integer> updateDocument(@PathVariable int id, @RequestBody TpDocument obj) {
        obj.id = id;
        return tpolService.saveDocument(obj) != 0 ? ResponseEntity.ok().body(obj.id) : ResponseEntity.noContent().build();
    }

    /**
     * delete {@link TpDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteDocument(@PathVariable int id) {
        logger.info("delete tp document with id = {}", id);
        return new ResponseEntity<>(tpolService.deleteDocument(id), HttpStatus.OK);
    }

    /**
     * get {@link TpRow} list
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/rows", method = RequestMethod.GET)
    public ResponseEntity<List<TpRow>> getRows(@PathVariable int id) {
        return new ResponseEntity<>(tpolRowService.getRows(id), HttpStatus.OK);
    }

    /**
     * get {@link TpSobst} list
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/sobsts", method = RequestMethod.GET)
    public ResponseEntity<List<TpSobst>> getDocumentSobsts(@PathVariable int id, @RequestParam(defaultValue = "false") boolean checked) {
        return new ResponseEntity<>(tpolService.getSobstList(id, checked), HttpStatus.OK);
    }

    /**
     * save {@link TpSobst} list
     * @param id tvk_tarif.id
     * @param list List<{@link TpSobst}>
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/sobsts", method = RequestMethod.POST)
    public ResponseEntity<Boolean> setDocumentSobsts(@PathVariable int id, @RequestBody List<TpSobst> list) {
        return new ResponseEntity<>(tpolService.saveSobstList(id, list), HttpStatus.OK);
    }

    /**
     * get base tarif list
     * @return
     */
    @RequestMapping(value = "/ttar", method = RequestMethod.GET)
    public ResponseEntity<List<CodeName>> getTTar() {
        return new ResponseEntity<>(tpolService.getBaseTarifList(), HttpStatus.OK);
    }

    /**
     * get all {@link TpSobst} list
     */
    @RequestMapping(value = "/sobsts", method = RequestMethod.GET)
    public ResponseEntity<List<TpSobst>> getSobsts() {
        return new ResponseEntity<>(tpolService.getSobstList(0, false), HttpStatus.OK);
    }

}