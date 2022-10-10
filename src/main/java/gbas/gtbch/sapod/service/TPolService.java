package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.tpol.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TPolService {

    /**
     *
     * @param id
     * @param editorMode
     * @return
     */
    TpDocument getDocument(int id, boolean editorMode);

    /**
     *
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    List<TpDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd, Map<String, String> filterMap);

    /**
     *
     * @return
     */
    List<CodeName> getBaseTarifList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<CodeName> getBaseTarifList(int idTPol);

    /**
     *
     * @param idTarif
     * @param checked
     * @return
     */
    List<TpSobst> getSobstList(int idTarif, boolean checked);

    /**
     *
     * @param idTarif
     * @param list
     * @return
     */
    boolean saveSobstList(int idTarif, List<TpSobst> list);

    /**
     *
     * @return
     */
    List<TpGroup> getGroups();

    /**
     *
     * @param tpDocument
     * @return
     */
    int saveDocument(TpDocument tpDocument);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteDocument(int id);

    /**
     *
     * @param sourceId
     * @param destinationId
     * @return
     */
    Integer copyDocument(int sourceId, int destinationId);

    /**
     * get list of clients for linked client (GTPENSI.FORWARD_BCH union GTPENSI.CONSIGN_BCH)
     * @return
     */
    List<TpClient> getTpClients();

    /**
     * get linked clients for tarif
     * @param idTarif
     * @return
     */
    List<TpLinkedClient> getLinkedTpClients(int idTarif);

    /**
     * save list of linked clients
     * @param idTarif
     * @param clientList
     * @return
     */
    boolean saveLinkedTpClients(int idTarif, List<TpLinkedClient> clientList);

    /**
     * delete all linked clients for tvk_tarif
     * @param idTarif
     * @return
     */
    boolean deleteTpLinkedClients(int idTarif);


}
