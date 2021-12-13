package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.gtbch.sapod.model.Currency;

import java.util.Date;

/**
 * Ставки к услугам
 * GTMAIN.price2
 */

@JsonIgnoreProperties(value = {"idMatherial", "codeMatherial", "numNod"})
public class MatherialPrice {

    /**
     * Идентификатор	int	price2.codp
     */
    private int id;

    /**
     * Ссылка на справочник услуг (matherial.matherial)	int	price2.matherial
     */
    private int idMatherial;

    /**
     * todo remove
     * код услуги price2.code_object
     */
    private String codeMatherial;

    /**
     * Дата начала действия ставки	datetime	price2.dat_home
     */
    private Date date;

    /**
     * Значение ставки	decimal(18,4)	price2.cena_1
     */
    private double rate;

    /**
     * Ссылка на валюту	int	price2.code
     */
    private Currency currency;

    /**
     * Код отделения (если = 0 - для всех НОД)	int	price2.num_nod
     */
    private int numNod;

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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getNumNod() {
        return numNod;
    }

    public void setNumNod(int numNod) {
        this.numNod = numNod;
    }
}
