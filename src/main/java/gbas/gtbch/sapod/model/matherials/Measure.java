package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Единица измерения
 * GTMAIN.measure
 */
@JsonIgnoreProperties(value = {"shortName", "type"})
public class Measure {

    /**
     * Идентификатор int	measure.measure
     *
     */
    private int id;

    /**
     * нНаименование единицы измерения varchar(40)	measure.name
     */
    private String name;

    /**

     */
    private String shortName;

    /**
     *
     */
    private short type;

    public Measure() {
    }

    public Measure(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }
}
