package gbas.gtbch.sapod.model;

import gbas.tvk.tpol3.TpolDocument;

import java.util.Date;
import java.util.List;

public class TPolDocument extends TpolDocument {

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
     * checked {@link TPolSobst} list
     */
    public List<TPolSobst> sobstList;

    public List<TPolSobst> getSobstList() {
        return sobstList;
    }
}
