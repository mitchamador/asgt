package gbas.gtbch.web;

import gbas.gtbch.sapod.model.matherials.*;
import gbas.gtbch.sapod.service.MatherialService;
import gbas.gtbch.sapod.service.MeasureService;
import gbas.tvk.objects.service.SborDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/matherials")
public class MatherialsController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private final MatherialService matherialService;
    private final MeasureService measureService;

    public MatherialsController(MatherialService matherialService, MeasureService measureService) {
        this.matherialService = matherialService;
        this.measureService = measureService;
    }

    /**
     *
     * @param dateBegin
     * @param dateEnd
     * @param all
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<MatherialListItem>> getMatherials(
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd,
            @RequestParam(value = "all", required = false) boolean all) {

        return new ResponseEntity<>(matherialService.getMatherials(dateBegin, dateEnd, all), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<Matherial> getMatherial(@PathVariable int id) {
        return new ResponseEntity<>(matherialService.getMatherial(id), HttpStatus.OK);
    }

    /**
     * create new {@link Matherial}
     * @param obj - {@link Matherial}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Integer> saveMatherial(HttpServletRequest request, @RequestBody Matherial obj, @RequestParam(required = false) boolean full) {
        int id = matherialService.saveMatherial(obj, full);
        return id != 0 ? ResponseEntity.created(URI.create(request.getRequestURI() + "/" + id)).body(id) : ResponseEntity.noContent().build();
    }

    /**
     * update {@link Matherial}
     * @param id - id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity<Integer> updateMatherial(@PathVariable int id, @RequestBody Matherial obj, @RequestParam(required = false) boolean full) {
        obj.setId(id);
        return matherialService.saveMatherial(obj, full) != 0 ? ResponseEntity.ok().body(obj.getId()) : ResponseEntity.noContent().build();
    }

    /**
     * delete {@link Matherial}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherial(@PathVariable int id) {
        logger.info("delete matherial with id = {}", id);
        return new ResponseEntity<>(matherialService.deleteMatherial(id), HttpStatus.OK);
    }

    /**
     * get {@link MatherialPrice} list
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/price", method = RequestMethod.GET)
    public ResponseEntity<List<MatherialPrice>> getMatherialPrice(@PathVariable int id) {
        return new ResponseEntity<>(matherialService.getMatherialPriceList(id), HttpStatus.OK);
    }

    /**
     * create new {@link MatherialPrice}
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/price", method = RequestMethod.POST)
    public ResponseEntity createMatherialPrice(HttpServletRequest request, @PathVariable int id, @RequestBody MatherialPrice matherialPrice) {
        matherialPrice.setIdMatherial(id);
        int idItem = matherialService.saveMatherialPrice(matherialPrice);
        return idItem != 0 ? ResponseEntity.created(URI.create(request.getRequestURI() + "/" + idItem)).body(idItem) : ResponseEntity.noContent().build();
    }

    /**
     * update {@link MatherialPrice}
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/price", method = RequestMethod.PUT)
    public ResponseEntity updateMatherialPrice(@PathVariable int id, @RequestBody MatherialPrice matherialPrice) {
        matherialPrice.setIdMatherial(id);
        int idItem = matherialService.saveMatherialPrice(matherialPrice);
        return idItem != 0 ? ResponseEntity.ok().body(idItem) : ResponseEntity.noContent().build();
    }

    /**
     * delete all {@link MatherialPrice} items for {@link Matherial}
     * @param idMatherial
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/price", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherialPrice(@PathVariable(name = "id") int idMatherial) {
        return new ResponseEntity<>(matherialService.deleteMatherialPrice(idMatherial), HttpStatus.OK);
    }

    /**
     * delete {@link MatherialPrice} item
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/price/{id_item:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherialPriceItem(@PathVariable int id, @PathVariable(name = "id_item") int idItem) {
        return new ResponseEntity<>(matherialService.deleteMatherialPriceItem(idItem), HttpStatus.OK);
    }

    /**
     * get {@link MatherialKoef} list
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/koef", method = RequestMethod.GET)
    public ResponseEntity<List<MatherialKoef>> getMatherialKoef(@PathVariable int id) {
        return new ResponseEntity<>(matherialService.getMatherialKoefList(id), HttpStatus.OK);
    }

    /**
     * create new {@link MatherialKoef}
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/koef", method = RequestMethod.POST)
    public ResponseEntity createMatherialKoef(HttpServletRequest request, @PathVariable int id, @RequestBody MatherialKoef matherialKoef) {
        matherialKoef.setIdMatherial(id);
        int idItem = matherialService.saveMatherialKoef(matherialKoef);
        return idItem != 0 ? ResponseEntity.created(URI.create(request.getRequestURI() + "/" + idItem)).body(idItem) : ResponseEntity.noContent().build();
    }

    /**
     * update {@link MatherialKoef}
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/koef", method = RequestMethod.PUT)
    public ResponseEntity updateMatherialKoef(@PathVariable int id, @RequestBody MatherialKoef matherialKoef) {
        matherialKoef.setIdMatherial(id);
        int idItem = matherialService.saveMatherialKoef(matherialKoef);
        return idItem != 0 ? ResponseEntity.ok().body(idItem) : ResponseEntity.noContent().build();
    }

    /**
     * delete all {@link MatherialKoef} items for {@link Matherial}
     * @param idMatherial
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/koef", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherialKoef(@PathVariable(name = "id") int idMatherial) {
        return new ResponseEntity<>(matherialService.deleteMatherialKoef(idMatherial), HttpStatus.OK);
    }

    /**
     * delete {@link MatherialKoef} item
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/koef/{id_item:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherialKoefItem(@PathVariable int id, @PathVariable(name = "id_item") int idItem) {
        return new ResponseEntity<>(matherialService.deleteMatherialKoefItem(idItem), HttpStatus.OK);
    }

    /**
     * get {@link MatherialNds} list
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/nds", method = RequestMethod.GET)
    public ResponseEntity<List<MatherialNds>> getMatherialNds(@PathVariable int id) {
        return new ResponseEntity<>(matherialService.getMatherialNdsList(id), HttpStatus.OK);
    }

    /**
     * create new {@link MatherialNds}
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/nds", method = RequestMethod.POST)
    public ResponseEntity createMatherialNds(HttpServletRequest request, @PathVariable int id, @RequestBody MatherialNds matherialNds) {
        matherialNds.setId(0);
        matherialNds.setIdMatherial(id);
        int idItem = matherialService.saveMatherialNds(matherialNds);
        return idItem != 0 ? ResponseEntity.created(URI.create(request.getRequestURI() + "/" + idItem)).body(idItem) : ResponseEntity.noContent().build();
    }

    /**
     * update {@link MatherialNds}
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/nds", method = RequestMethod.PUT)
    public ResponseEntity updateMatherialNds(@PathVariable int id, @RequestBody MatherialNds matherialNds) {
        matherialNds.setIdMatherial(id);
        int idItem = matherialService.saveMatherialNds(matherialNds);
        return idItem != 0 ? ResponseEntity.ok().body(idItem) : ResponseEntity.noContent().build();
    }

    /**
     * delete all {@link MatherialNds} items for {@link Matherial}
     * @param idMatherial
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/nds", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherialNds(@PathVariable(name = "id") int idMatherial) {
        return new ResponseEntity<>(matherialService.deleteMatherialNds(idMatherial), HttpStatus.OK);
    }

    /**
     * delete {@link MatherialNds} item
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/nds/{id_item:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteMatherialNdsItem(@PathVariable int id, @PathVariable(name = "id_item") int idItem) {
        return new ResponseEntity<>(matherialService.deleteMatherialNdsItem(idItem), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/measures", method = RequestMethod.GET)
    public ResponseEntity<List<Measure>> getMeasures() {
        return new ResponseEntity<>(measureService.getMeasureList(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/descriptors", method = RequestMethod.GET)
    public ResponseEntity<List<SborDescriptorItem>> getDescriptors() {
        return new ResponseEntity<>(Arrays.stream(SborDescriptor.values())
                .map(sd -> new SborDescriptorItem(sd.getCode(), sd.getName(), sd.getStaticSborOsob()))
                .sorted(Comparator.comparing(SborDescriptorItem::getName)) //.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }
}
