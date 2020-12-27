package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.TPolDocument;
import gbas.gtbch.sapod.model.TPolSobst;
import gbas.gtbch.sapod.model.TpolGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface TPolService {
    TPolDocument getDocument(int id);

    List<TPolDocument> getDocuments(Date dateBegin, Date dateEnd);

    List<TPolDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd);

    List<CodeName> getBaseTarifList();

    List<CodeName> getBaseTarifList(int idTPol);

    List<TPolSobst> getSobstList(int idTarif, boolean checked);

    boolean saveSobstList(int idTarif, List<TPolSobst> list);

    List<TpolGroup> getGroups();

    int saveDocument(TPolDocument tPolDocument);

    boolean deleteDocument(int id);
}
