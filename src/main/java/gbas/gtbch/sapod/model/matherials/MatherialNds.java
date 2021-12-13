package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * НДС к услугам
 * GTMAIN.tvk_nds
 */
@JsonIgnoreProperties(value = {"idMatherial", "codeMatherial"})
public class MatherialNds {

    /**
     * Идентификатор	int	tvk_nds.id
     */
    private int id;

    /**
     * Ссылка на справочник услуг (matherial.matherial)	int	tvk_nds.id_matherial
     */
    private int idMatherial;

    /**
     * Код услуги	char(10)	tvk_nds.code_object
     */
    private String codeMatherial;

    /**
     * Дата начала действия процента НДС	datetime	tvk_nds.date_begin
     */
    private Date date;

    /**
     * Значение процента НДС	int	tvk_nds.value
     */
    private int value;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
