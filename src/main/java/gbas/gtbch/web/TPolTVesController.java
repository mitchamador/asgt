package gbas.gtbch.web;

import gbas.gtbch.sapod.repository.TPolTVesRepository;
import gbas.tvk.tpol3.TvkTVes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/tves")
public class TPolTVesController {

    private final TPolTVesRepository tPolTVesRepository;

    public TPolTVesController(TPolTVesRepository tPolTVesRepository) {
        this.tPolTVesRepository = tPolTVesRepository;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TvkTVes>> getTVesList() {
        return new ResponseEntity<>(tPolTVesRepository.getVOList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TvkTVes> getTVes(@PathVariable int id) {
        return new ResponseEntity<>(tPolTVesRepository.getVO(id), HttpStatus.OK);
    }

    /**
     * create new TvkTVes
     * @param obj - {@link TvkTVes}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveKof(@RequestBody TvkTVes obj) {
        int id = tPolTVesRepository.saveVO(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/tves/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TvkTVes
     * @param id - tvk_t_ves.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateKof(@PathVariable int id, @RequestBody TvkTVes obj) {
        obj.id = id;
        return tPolTVesRepository.saveVO(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TvkTVes
     * @param id - tvk_t_ves.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolTVesRepository.deleteVO(id), HttpStatus.OK);
    }

}
