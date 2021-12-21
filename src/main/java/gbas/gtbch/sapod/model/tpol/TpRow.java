package gbas.gtbch.sapod.model.tpol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.tvk.tpol3.service.TPRow;

import java.text.DecimalFormat;

/**
 * class for formatting {@link TPRow} fields with {@link DecimalFormat} for using with thymeleaf's templates
 */
@JsonIgnoreProperties(value = {"id_ves_norm", "id_tab_ves", "id_tab_kof", "id_tab_kofbs"})
public class TpRow extends TPRow {

    public TpRow() {
    }

    private DecimalFormat df = new DecimalFormat("###########0.##########");

    public int getId() {
        return id;
    }

    public int gettPol() {
        return tPol;
    }

    public int getnStr() {
        return nStr;
    }

    public String getPrim() {
        return prim;
    }

    public String getTipTTar() {
        return df.format(tipTTar);
    }

    public String gettVes() {
        return df.format(tVes);
    }

    public double getKlas() {
        return klas;
    }

    public String getVesNorm() {
        return df.format(vesNorm);
    }

    public int getnTab() {
        return nTab;
    }

    public String getKof() {
        return df.format(kof);
    }

    public String getKof_sobst() {
        return df.format(kof_sobst);
    }

    public int getSkid() {
        return skid;
    }

    public int getBs_tab() {
        return bs_tab;
    }

    public int getKoleya() {
        return koleya;
    }

}
