package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.TPolDocument;
import gbas.gtbch.sapod.model.TPolSobst;
import gbas.gtbch.sapod.model.TpolGroup;
import gbas.gtbch.sapod.repository.TPolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TPolServiceImpl implements TPolService {

    private final TPolRepository tPolRepository;

    public TPolServiceImpl(TPolRepository tPolRepository) {
        this.tPolRepository = tPolRepository;
    }


    /**
     * get {@link TPolDocument}
     *
     * @param id
     * @return
     */
    @Override
    public TPolDocument getDocument(int id) {
        return getDocument(id, false);
    }

    /**
     *
     * @param id
     * @param editorMode
     * @return
     */
    @Override
    public TPolDocument getDocument(int id, boolean editorMode) {
        return tPolRepository.getDocument(id, editorMode);
    }

    /**
     * get list of {@link TPolDocument}
     *
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    @Override
    public List<TPolDocument> getDocuments(Date dateBegin, Date dateEnd) {
        return tPolRepository.getDocuments(dateBegin, dateEnd);
    }

    /**
     * get list of {@link TPolDocument}
     *
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    @Override
    public List<TPolDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd) {
        return tPolRepository.getDocuments(typeCode, dateBegin, dateEnd);
    }

    /**
     * @param id
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    private List<TPolDocument> getDocuments(int id, String typeCode, Date dateBegin, Date dateEnd) {

        return tPolRepository.getDocuments(id, typeCode, dateBegin, dateEnd);
    }

    /**
     * @return
     */
    @Override
    public List<CodeName> getBaseTarifList() {
        return tPolRepository.getBaseTarifList();
    }

    /**
     * TPRow.tipTTar
     *
     * @param idTPol
     * @return
     */
    @Override
    public List<CodeName> getBaseTarifList(int idTPol) {
        return tPolRepository.getBaseTarifList(idTPol);
    }

    @Override
    public List<TPolSobst> getSobstList(int idTarif, boolean checked) {
        return tPolRepository.getSobstList(idTarif, checked);
    }

    /**
     * save {@link TPolSobst} list
     *
     * @param idTarif {@link TPolDocument#id}
     * @param list    {@link TPolSobst} list
     */
    @Override
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean saveSobstList(int idTarif, List<TPolSobst> list) {

        return tPolRepository.saveSobstList(idTarif, list);
    }

    /**
     * get tpol groups
     *
     * @return
     */
    @Override
    public List<TpolGroup> getGroups() {
        return tPolRepository.getGroups();
    }

    /**
     * save {@link TPolDocument}
     *
     * @param tPolDocument {@link TPolDocument}
     * @return
     */
    @Override
    public int saveDocument(TPolDocument tPolDocument) {
        return tPolRepository.saveDocument(tPolDocument);
    }

    private int getTPNumber(String type_code) {
        return tPolRepository.getTPNumber(type_code);
    }

    /**
     * delete {@link TPolDocument}
     *
     * @param id {@link TPolDocument#id}
     * @return
     */
    @Override
    public boolean deleteDocument(int id) {
        return tPolRepository.deleteDocument(id);
    }

    @Override
    public Integer copyDocument(int sourceId, int destinationId) {
        return tPolRepository.copyDocument(sourceId, destinationId);
    }
}
