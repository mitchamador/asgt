package gbas.gtbch.sapod.model;

import gbas.tvk.tpol3.TpolDocument;

import java.util.Date;
import java.util.List;

public class TPolDocument extends TpolDocument {

    public String getName() {
        return name;
    }

    public String getNContract() {
        return n_contract;
    }

    public int getNPol() {
        return n_pol;
    }
    public String getTypeCode() {
        return type_code;
    }

    public Date getDateBegin() {
        return date_begin;
    }

    public Date getDateEnd() {
        return date_end;
    }

    public int getCodTipTar() {
        return codTipTar;
    }

    public short getCodDobor() {
        return codDobor;
    }

    /**
     * checked {@link TPolSobst} list
     */
    public List<TPolSobst> sobstList;
}
