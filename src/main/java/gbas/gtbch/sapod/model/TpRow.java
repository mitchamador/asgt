package gbas.gtbch.sapod.model;

import gbas.tvk.tpol3.service.TPRow;

import java.text.DecimalFormat;

/**
 * class for formatting {@link TPRow} fields with {@link DecimalFormat} for using with thymeleaf's templates
 */
public class TpRow extends TPRow {

    public TpRow(TPRow row) {
        this.id = row.id;
        this.tPol = row.tPol;
        this.nStr = row.nStr;
        this.prim = row.prim;
        this.tipTTar = row.tipTTar;
        this.tVes = row.tVes;
        this.klas = row.klas;
        this.vesNorm = row.vesNorm;
        this.id_ves_norm = row.id_ves_norm;
        this.id_tab_ves = row.id_tab_ves;
        this.id_tab_kof = row.id_tab_kof;
        this.nTab = row.nTab;
        this.kof = row.kof;
        this.kof_sobst = row.kof_sobst;
        this.skid = row.skid;
        this.id_tarif = row.id_tarif;
        this.id_tab_kofbs = row.id_tab_kofbs;
        this.bs_tab = row.bs_tab;
        this.koleya = row.koleya;
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
