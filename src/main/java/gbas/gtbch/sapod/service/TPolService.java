package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.tpol.TpDocument;
import gbas.gtbch.sapod.model.tpol.TpGroup;
import gbas.gtbch.sapod.model.tpol.TpSobst;

import java.util.Date;
import java.util.List;

public interface TPolService {
    /**
     *
     * @param id
     * @return
     */
    TpDocument getDocument(int id);

    /**
     *
     * @param id
     * @param editorMode
     * @return
     */
    TpDocument getDocument(int id, boolean editorMode);

    /**
     *
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    List<TpDocument> getDocuments(Date dateBegin, Date dateEnd);

    /**
     *
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    List<TpDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd);

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
}
