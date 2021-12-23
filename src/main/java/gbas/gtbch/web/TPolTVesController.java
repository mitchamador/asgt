package gbas.gtbch.web;

import gbas.gtbch.sapod.model.tpol.TpTvkTVes;
import gbas.gtbch.sapod.service.TPolTVesService;
import gbas.gtbch.web.controlleradvice.annotations.DuplicateKeyExceptionHandler;
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
    public ResponseEntity<List<TpTvkTVes>> getTVesList() {
        return new ResponseEntity<>(tPolTVesService.getVOList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<TpTvkTVes> getTVes(@PathVariable int id) {
        return new ResponseEntity<>(tPolTVesService.getVO(id), HttpStatus.OK);
    }

    /**
     * create new TpTvkTVes
     * @param obj - {@link TpTvkTVes}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveTVes(@RequestBody TpTvkTVes obj) {
        int id = tPolTVesService.saveVO(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/tves/" + id)).build() : ResponseEntity.noContent().build();
    }

    /**
     * update TpTvkTVes
     * @param id - tvk_t_ves.id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity updateTVes(@PathVariable int id, @RequestBody TpTvkTVes obj) {
        obj.id = id;
        return tPolTVesService.saveVO(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }

    /**
     * delete TpTvkTVes
     * @param id - tvk_t_ves.id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTVes(@PathVariable int id) {
        return new ResponseEntity<>(tPolTVesService.deleteVO(id), HttpStatus.OK);
    }

}
