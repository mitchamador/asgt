package gbas.gtbch.web;

import gbas.gtbch.sapod.service.TPolTVesService;
import gbas.gtbch.web.controlleradvice.annotations.DuplicateKeyExceptionHandler;
import gbas.tvk.tpol3.TvkTVes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@DuplicateKeyExceptionHandler
@RequestMapping("/api/tpol/tves")
public class TPolTVesController {

    private final TPolTVesService tPolTVesService;

    public TPolTVesController(TPolTVesService tPolTVesService) {
        this.tPolTVesService = tPolTVesService;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TvkTVes>> getTVesList() {
        return new ResponseEntity<>(tPolTVesService.getVOList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TvkTVes> getTVes(@PathVariable int id) {
        return new ResponseEntity<>(tPolTVesService.getVO(id), HttpStatus.OK);
    }

    /**
     * create new TvkTVes
     * @param obj - {@link TvkTVes}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveKof(@RequestBody TvkTVes obj) {
        int id = tPolTVesService.saveVO(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/tves/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TvkTVes
     * @param id - tvk_t_ves.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateKof(@PathVariable int id, @RequestBody TvkTVes obj) {
        obj.id = id;
        return tPolTVesService.saveVO(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TvkTVes
     * @param id - tvk_t_ves.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolTVesService.deleteVO(id), HttpStatus.OK);
    }

}
