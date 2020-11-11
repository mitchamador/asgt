package gbas.gtbch.web;

import gbas.gtbch.sapod.repository.TPolTOsrRepository;
import gbas.tvk.tpol3.TvkTOsr;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/tosr")
public class TPolTOsrController {

    private final TPolTOsrRepository tPolTOsrRepository;

    public TPolTOsrController(TPolTOsrRepository tPolTOsrRepository) {
        this.tPolTOsrRepository = tPolTOsrRepository;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TvkTOsr>> getTOsrList() {
        return new ResponseEntity<>(tPolTOsrRepository.getContList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TvkTOsr> getTOsr(@PathVariable int id) {
        return new ResponseEntity<>(tPolTOsrRepository.getCont(id), HttpStatus.OK);
    }

    /**
     * create new TvkTOsr
     * @param obj - {@link TvkTOsr}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveKof(@RequestBody TvkTOsr obj) {
        int id = tPolTOsrRepository.saveCont(obj);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/tosr/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TvkTOsr
     * @param id - tvk_t_osr.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateKof(@PathVariable int id, @RequestBody TvkTOsr obj) {
        obj.id = id;
        return tPolTOsrRepository.saveCont(obj) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TvkTOsr
     * @param id - tvk_t_osr.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteKof(@PathVariable int id) {
        return new ResponseEntity<>(tPolTOsrRepository.deleteCont(id), HttpStatus.OK);
    }

}
