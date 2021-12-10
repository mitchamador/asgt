package gbas.gtbch.web;

import gbas.gtbch.sapod.model.tpol.TpTvkTOsr;
import gbas.gtbch.sapod.service.TPolTOsrService;
import gbas.gtbch.web.controlleradvice.annotations.DuplicateKeyExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@DuplicateKeyExceptionHandler
@RequestMapping("/api/tpol/tosr")
public class TPolTOsrController {

    private final TPolTOsrService tPolTOsrService;

    public TPolTOsrController(TPolTOsrService tPolTOsrService) {
        this.tPolTOsrService = tPolTOsrService;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TpTvkTOsr>> getTOsrList() {
        return new ResponseEntity<>(tPolTOsrService.getContList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TpTvkTOsr> getTOsr(@PathVariable int id) {
        return new ResponseEntity<>(tPolTOsrService.getCont(id), HttpStatus.OK);
    }

    /**
     * create new TpTvkTOsr
     * @param obj - {@link TpTvkTOsr}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveTOsr(@RequestBody TpTvkTOsr obj) {
        int id = tPolTOsrService.saveCont(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/tosr/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TpTvkTOsr
     * @param id - tvk_t_osr.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateTOsr(@PathVariable int id, @RequestBody TpTvkTOsr obj) {
        obj.id = id;
        return tPolTOsrService.saveCont(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TpTvkTOsr
     * @param id - tvk_t_osr.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTOsr(@PathVariable int id) {
        return new ResponseEntity<>(tPolTOsrService.deleteCont(id), HttpStatus.OK);
    }

}
