package gbas.gtbch.web;

import gbas.gtbch.sapod.model.tpol.TpTvkKof;
import gbas.gtbch.sapod.service.TPolKofService;
import gbas.gtbch.web.controlleradvice.annotations.DuplicateKeyExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@DuplicateKeyExceptionHandler
@RequestMapping("/api/tpol/kof")
public class TPolKofController {

    private final TPolKofService tPolKofService;

    public TPolKofController(TPolKofService tpolKofService) {
        this.tPolKofService = tpolKofService;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TpTvkKof>> getKofList() {
        return new ResponseEntity<>(tPolKofService.getKofList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TpTvkKof> getKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolKofService.getKof(id), HttpStatus.OK);
    }

    /**
     * create new TpTvkKof
     * @param obj {@link TpTvkKof}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveKof(@RequestBody TpTvkKof obj) {
        int id = tPolKofService.saveKof(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/kof/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TpTvkKof
     * @param id tvk_kof.id
     * @param obj {@link TpTvkKof}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateKof(@PathVariable int id, @RequestBody TpTvkKof obj) {
        obj.id = id;
        return tPolKofService.saveKof(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TpTvkKof
     * @param id - tvk_kof.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolKofService.deleteKof(id), HttpStatus.OK);
    }

}
