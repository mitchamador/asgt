package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.matherials.*;

import java.util.Date;
import java.util.List;

public interface MatherialService {

    /**
     * get list of {@link MatherialListItem}
     * @return
     */
    List<MatherialListItem> getMatherials();

    /**
     * get list of {@link MatherialListItem}
     * @return
     */
    List<MatherialListItem> getMatherials(Date periodBegin, Date periodEnd, boolean all);

    /**
     * get {@link Matherial} code
     * @param id
     * @return
     */
    String getCodeMatherial(int id);

    /**
     * get {@link Matherial}
     * @param id
     * @return
     */
    Matherial getMatherial(int id);

    /**
     * save {@link Matherial}
     * @param matherial
     * @param full save with dependencies (price2, tvk_nds, tvk_kof_sbor)
     * @return
     */
    int saveMatherial(Matherial matherial, boolean full);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteMatherial(int id);

    /**
     * get {@link MatherialKoef} list
     * @param idMatherial
     * @return
     */
    List<MatherialKoef> getMatherialKoefList(int idMatherial);

    /**
     * get {@link MatherialPrice} list
     * @param idMatherial
     * @return
     */
    List<MatherialPrice> getMatherialPriceList(int idMatherial);

    /**
     * get {@link MatherialNds} list
     * @param idMatherial
     * @return
     */
    List<MatherialNds> getMatherialNdsList(int idMatherial);

    /**
     * git {@link MatherialKoef}
     * @param idItem
     * @return
     */
    MatherialKoef getMatherialKoef(int idItem);

    /**
     * get {@link MatherialPrice}
     * @param idItem
     * @return
     */
    MatherialPrice getMatherialPrice(int idItem);

    /**
     * get {@link MatherialNds}
     * @param idItem
     * @return
     */
    MatherialNds getMatherialNds(int idItem);

    /**
     * save {@link MatherialPrice}
     * @param matherialPrice
     * @return
     */
    int saveMatherialPrice(MatherialPrice matherialPrice);

    /**
     * save {@link MatherialKoef}
     * @param matherialKoef
     * @return
     */
    int saveMatherialKoef(MatherialKoef matherialKoef);

    /**
     * save {@link MatherialNds}
     * @param matherialNds
     * @return
     */
    int saveMatherialNds(MatherialNds matherialNds);

    /**
     * delete all {@link MatherialPrice} items for matherial
     * @param idMatherial matherial's id
     * @return
     */
    boolean deleteMatherialPrice(int idMatherial);

    /**
     * delete all {@link MatherialKoef} items for matherial
     * @param idMatherial matherial's id
     * @return
     */
    boolean deleteMatherialKoef(int idMatherial);

    /**
     * delete all {@link MatherialNds} items for matherial
     * @param idMatherial matherial's id
     * @return
     */
    boolean deleteMatherialNds(int idMatherial);

    /**
     * delete {@link MatherialPrice} item
     * @param idMatherialPrice
     * @return
     */
    boolean deleteMatherialPriceItem(int idMatherialPrice);

    /**
     * delete {@link MatherialKoef} item
     * @param idMatherialKoef
     * @return
     */
    boolean deleteMatherialKoefItem(int idMatherialKoef);

    /**
     * delete {@link MatherialNds} item
     * @param idMatherialNds
     * @return
     */
    boolean deleteMatherialNdsItem(int idMatherialNds);
}
