package gbas.gtbch.sapod.model.tpol;

import gbas.tvk.tpol3.TpolDocument;

import java.util.Date;
import java.util.List;

public class TpDocument extends TpolDocument {

    public String getName() {
        return name;
    }

    public String getN_contract() {
        return n_contract;
    }

    public int getN_pol() {
        return n_pol;
    }

    public String getType_code() {
        return type_code;
    }

    public Date getDate_begin() {
        return date_begin;
    }

    public Date getDate_end() {
        return date_end;
    }

    public int getCodTipTar() {
        return codTipTar;
    }

    public short getCodDobor() {
        return codDobor;
    }

    public short getPr_calc() {
        return pr_calc;
    }

    /**
     * checked {@link TpSobst} list
     */
    public List<TpSobst> sobstList;

    public List<TpSobst> getSobstList() {
        return sobstList;
    }
}
