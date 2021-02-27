package gbas.gtbch.sapod.model;

import gbas.gtbch.util.StringTrimConverter;

import javax.persistence.*;

@Entity
@Table(name = "currency")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code")
    private int id;

    @Column(name = "name", length = 20)
    @Convert(converter = StringTrimConverter.class)
    private String name;

    @Column(name = "short_name", length = 10)
    @Convert(converter = StringTrimConverter.class)
    private String shortName;

    @Column(name = "symbol", length = 3)
    @Convert(converter = StringTrimConverter.class)
    private String symbol;

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
