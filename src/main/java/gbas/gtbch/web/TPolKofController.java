package gbas.gtbch.web;

import gbas.gtbch.sapod.repository.TPolKofRepository;
import gbas.tvk.tpol3.TvkKof;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/kof")
public class TPolKofController {

    private final TPolKofRepository tPolKofRepository;

    public TPolKofController(TPolKofRepository tpolKofRepository) {
        this.tPolKofRepository = tpolKofRepository;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TvkKof>> getKofList() {
        return new ResponseEntity<>(tPolKofRepository.getKofList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TvkKof> getKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolKofRepository.getKof(id), HttpStatus.OK);
    }

    /**
     * create new TvkKof
     * @param obj {@link TvkKof}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveKof(@RequestBody TvkKof obj) {
        int id = tPolKofRepository.saveKof(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/obj/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TvkKof
     * @param id tvk_kof.id
     * @param obj {@link TvkKof}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateKof(@PathVariable int id, @RequestBody TvkKof obj) {
        obj.id = id;
        return tPolKofRepository.saveKof(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TvkKof
     * @param id - tvk_kof.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolKofRepository.deleteKof(id), HttpStatus.OK);
    }

}
