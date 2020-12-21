package gbas.gtbch.sapod.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rate_of_exchange")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne()
    @JoinColumn(name = "code", referencedColumnName = "code")
    private Currency currency;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "how_much")
    private double howMuch;

    @Column(name = "rate")
    private double rate;

    @OneToOne()
    @JoinColumn(name = "base_currency", referencedColumnName = "code")
    private Currency baseCurrency;

    @Transient
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public double getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(double howMuch) {
        this.howMuch = howMuch;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getComment() {
        return comment == null ? "" : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
