package gbas.gtbch.web;

import gbas.gtbch.sapod.repository.*;
import gbas.tvk.tpol3.TvkKof;
import gbas.tvk.tpol3.TvkTOsr;
import gbas.tvk.tpol3.TvkTVes;
import gbas.tvk.tpol3.service.TPRow;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/row")
public class TPolRowController {

    private final TPolRowRepository tpolRowRepository;
    private final TPolKofRepository tpolKofRepository;
    private final TPolTVesRepository tpolTVesRepository;
    private final TPolTOsrRepository tpolTOsrRepository;
    private final TPolRepository tpolRepository;

    public TPolRowController(TPolRowRepository tPolRowRepository, TPolKofRepository tpolKofRepository, TPolTVesRepository tpolTVesRepository, TPolTOsrRepository tpolTOsrRepository, TPolRepository tpolRepository) {
        this.tpolRowRepository = tPolRowRepository;
        this.tpolKofRepository = tpolKofRepository;
        this.tpolTVesRepository = tpolTVesRepository;
        this.tpolTOsrRepository = tpolTOsrRepository;
        this.tpolRepository = tpolRepository;
    }

    /**
     * get TPRow
     * @param id tvk_t_pol.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TPRow> getRow(@PathVariable int id) {
        return new ResponseEntity<>(tpolRowRepository.getRow(id), HttpStatus.OK);
    }

    /**
     * create new TPRow
     * @param row - {@link TPRow}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveRow(@RequestBody TPRow row) {
        int id = tpolRowRepository.saveRow(row);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/row/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TPRow
     * @param id - tvk_t_pol.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateKof(@PathVariable int id, @RequestBody TPRow row) {
        row.id = id;
        return tpolRowRepository.saveRow(row) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TPRow
     * @param id - tvk_t_pol.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteRow(@PathVariable int id) {
        return new ResponseEntity<>(tpolRowRepository.deleteRow(id), HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/kof", method = RequestMethod.GET)
    public ResponseEntity<List<TvkKof>> getRowKof(@PathVariable int id) {
        return new ResponseEntity<>(tpolKofRepository.getKofList(id), HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/tves", method = RequestMethod.GET)
    public ResponseEntity<List<TvkTVes>> getRowTVes(@PathVariable int id) {
        return new ResponseEntity<>(tpolTVesRepository.getVOList(id), HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/tosr", method = RequestMethod.GET)
    public ResponseEntity<List<TvkTOsr>> getRowTOsr(@PathVariable int id) {
        return new ResponseEntity<>(tpolTOsrRepository.getContList(id), HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/ttar", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getRowTTar(@PathVariable int id) {
        return new ResponseEntity<>(tpolRepository.getBaseTarifList(id), HttpStatus.OK);
    }

}
