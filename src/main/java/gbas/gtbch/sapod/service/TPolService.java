package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.TPolDocument;
import gbas.gtbch.sapod.model.TPolSobst;
import gbas.gtbch.sapod.model.TpolGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface TPolService {
    /**
     *
     * @param id
     * @return
     */
    TPolDocument getDocument(int id);

    /**
     *
     * @param id
     * @param editorMode
     * @return
     */
    TPolDocument getDocument(int id, boolean editorMode);

    /**
     *
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    List<TPolDocument> getDocuments(Date dateBegin, Date dateEnd);

    /**
     *
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    List<TPolDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd);

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
    List<TPolSobst> getSobstList(int idTarif, boolean checked);

    /**
     *
     * @param idTarif
     * @param list
     * @return
     */
    boolean saveSobstList(int idTarif, List<TPolSobst> list);

    /**
     *
     * @return
     */
    List<TpolGroup> getGroups();

    /**
     *
     * @param tPolDocument
     * @return
     */
    int saveDocument(TPolDocument tPolDocument);

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
