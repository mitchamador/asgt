package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Коэффициенты к услугам
 * GTMAIN.tvk_kof
 */
@JsonIgnoreProperties(value = {"idMatherial", "codeMatherial"})
public class MatherialKoef {

    /**
     * Идентификатор	int	tvk_kof_sbor.id
     */
    private int id;

    /**
     * Ссылка на справочник услуг (matherial.matherial)	int	tvk_kof_sbor.id_object
     */
    private int idMatherial;

    /**
     * Код услуги	char(10)	tvk_kof_sbor.code_object
     */
    private String codeMatherial;

    /**
     * Дата начала действия коэффициента	datetime	tvk_kof_sbor.date_begin
     */
    private Date date;

    /**
     * Значение коэффициента	float	tvk_kof_sbor.koef
     */
    private double koef;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMatherial() {
        return idMatherial;
    }

    public void setIdMatherial(int idMatherial) {
        this.idMatherial = idMatherial;
    }

    public String getCodeMatherial() {
        return codeMatherial;
    }

    public void setCodeMatherial(String codeMatherial) {
        this.codeMatherial = codeMatherial;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getKoef() {
        return koef;
    }

    public void setKoef(double koef) {
        this.koef = koef;
    }
}
