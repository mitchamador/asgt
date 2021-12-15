package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * Услуга
 * GTMAIN.matherial
 */
@JsonIgnoreProperties(value = {"nDoc", "dateBegin", "dateEnd", "codeGroup", "osobName", "osobVal"})
public class Matherial {

    /**
     * Индентификатор int	matherial.matherial
     */
    private int id;

    /**
     * Код статьи	Код из справочника  статей дополнительных работ (услуг)	char(3) matherial.code_ekisufr
     */
    private String codeEkisufr;

    /**
     * Код работы(услуги)	Кодировка по маске
     * «Код статьи».«порядковый номер условия»	char(10)	matherial.code (будет равно коду ГТ в новой версии справочника) matherial.code_calc
     */
    private String code;

    /**
     * Полное наименование	Без ограничения длины в том виде, в котором отображается в первичном учетном документе	varchar(150) matherial.name
     */
    private String name;

    /**
     * todo
     * Нормативный документ	Указываем документ на основании, которого работа (услуга) объявлена на Дороге	(C100)	matherial.n_doc
     */
    private String nDoc;

    /**
     * todo
     * Дата с	Дата начала действия статьи	datetime matherial.date_b
     */
    private Date dateBegin;

    /**
     * todo
     * Дата по	Дата окончания действия статьи	datetime matherial.date_e
     */
    private Date dateEnd;

    /**
     * Единица измерения 1 (левая) (int	matherial.measure = measure.measure, matherial.measure_name_left = measure.name)
     */
    private Measure measureLeft;

    /**
     * Единица измерения 2 (правая) (int	matherial.measure_right = measure.measure, matherial.measure_name_right = measure.name)
     */
    private Measure measureRight;

    /**
     * Формула расчета	Предназначение	int	matherial.descriptor (int)
     */
    private int descriptor;

    /**
     * список ставок
     */
    private List<MatherialPrice> priceList;

    /**
     * список коэффициентов
     */
    private List<MatherialKoef> koefList;

    /**
     * список значений НДС
     */
    private List<MatherialNds> ndsList;

    /**
     * matherial.osob_name
     */
    private String osobName;

    /**
     * matherial.osob_val
     */
    private int osobVal;

    /**
     * matherial.code_group
     */
    private String codeGroup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeEkisufr() {
        return codeEkisufr;
    }

    public void setCodeEkisufr(String codeEkisufr) {
        this.codeEkisufr = codeEkisufr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getnDoc() {
        return nDoc;
    }

    public void setnDoc(String nDoc) {
        this.nDoc = nDoc;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Measure getMeasureLeft() {
        return measureLeft;
    }

    public void setMeasureLeft(Measure measureLeft) {
        this.measureLeft = measureLeft;
    }

    public Measure getMeasureRight() {
        return measureRight;
    }

    public void setMeasureRight(Measure measureRight) {
        this.measureRight = measureRight;
    }

    public int getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(int descriptor) {
        this.descriptor = descriptor;
    }

    public List<MatherialPrice> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<MatherialPrice> priceList) {
        this.priceList = priceList;
    }

    public List<MatherialKoef> getKoefList() {
        return koefList;
    }

    public void setKoefList(List<MatherialKoef> koefList) {
        this.koefList = koefList;
    }

    public List<MatherialNds> getNdsList() {
        return ndsList;
    }

    public void setNdsList(List<MatherialNds> ndsList) {
        this.ndsList = ndsList;
    }

    public String getOsobName() {
        return osobName;
    }

    public void setOsobName(String osobName) {
        this.osobName = osobName;
    }

    public int getOsobVal() {
        return osobVal;
    }

    public void setOsobVal(int osobVal) {
        this.osobVal = osobVal;
    }

    public String getCodeGroup() {
        return codeGroup;
    }

    public void setCodeGroup(String codeGroup) {
        this.codeGroup = codeGroup;
    }
}
