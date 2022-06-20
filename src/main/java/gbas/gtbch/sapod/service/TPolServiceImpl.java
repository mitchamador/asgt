package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.tpol.*;
import gbas.gtbch.sapod.repository.TPolRepository;
import gbas.gtbch.util.cache.annotation.CacheClear;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TPolServiceImpl implements TPolService {

    private final TPolRepository tPolRepository;

    public TPolServiceImpl(TPolRepository tPolRepository) {
        this.tPolRepository = tPolRepository;
    }

    /**
     * get {@link TpDocument}
     *
     * @param id
     * @return
     */
    @Override
    public TpDocument getDocument(int id) {
        return getDocument(id, false);
    }

    /**
     *
     * @param id
     * @param editorMode
     * @return
     */
    @Override
    public TpDocument getDocument(int id, boolean editorMode) {
        return tPolRepository.getDocument(id, editorMode);
    }

    /**
     * get list of {@link TpDocument}
     *
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    @Override
    public List<TpDocument> getDocuments(Date dateBegin, Date dateEnd) {
        return tPolRepository.getDocuments(dateBegin, dateEnd);
    }

    /**
     * get list of {@link TpDocument}
     *
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    @Override
    public List<TpDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd) {
        return tPolRepository.getDocuments(typeCode, dateBegin, dateEnd);
    }

    /**
     * @param id
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    private List<TpDocument> getDocuments(int id, String typeCode, Date dateBegin, Date dateEnd) {
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
    public List<TpSobst> getSobstList(int idTarif, boolean checked) {
        return tPolRepository.getSobstList(idTarif, checked);
    }

    /**
     * save {@link TpSobst} list
     *
     * @param idTarif {@link TpDocument#id}
     * @param list    {@link TpSobst} list
     */
    @Override
    @CacheClear({"tp"})
    public boolean saveSobstList(int idTarif, List<TpSobst> list) {
        return tPolRepository.saveSobstList(idTarif, list);
    }

    /**
     * get tpol groups
     *
     * @return
     */
    @Override
    public List<TpGroup> getGroups() {
        return tPolRepository.getGroups();
    }

    /**
     * save {@link TpDocument}
     *
     * @param tpDocument {@link TpDocument}
     * @return
     */
    @Override
    @CacheClear({"tp"})
    public int saveDocument(TpDocument tpDocument) {
        return tPolRepository.saveDocument(tpDocument);
    }

    private int getTPNumber(String type_code) {
        return tPolRepository.getTPNumber(type_code);
    }

    /**
     * delete {@link TpDocument}
     *
     * @param id {@link TpDocument#id}
     * @return
     */
    @Override
    @CacheClear({"tp"})
    public boolean deleteDocument(int id) {
        return tPolRepository.deleteDocument(id);
    }

    @Override
    @CacheClear({"tp"})
    public Integer copyDocument(int sourceId, int destinationId) {
        return tPolRepository.copyDocument(sourceId, destinationId);
    }

    @Override
    public List<TpClient> getTpClients() {
        return tPolRepository.getTpClients();
    }

    @Override
    public List<TpLinkedClient> getLinkedTpClients(int idTarif) {
        return tPolRepository.getLinkedTpClients(idTarif);
    }

    @Override
    @CacheClear({"tp"})
    public boolean saveLinkedTpClients(int idTarif, List<TpLinkedClient> clientList) {
        return tPolRepository.saveLinkedTpClients(idTarif, clientList);
    }

    @Override
    @CacheClear({"tp"})
    public boolean deleteTpLinkedClients(int idTarif) {
        return tPolRepository.deleteLinkedTpClients(idTarif);
    }
}
