package gbas.gtbch.sapod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "rate_of_exchange")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @OneToOne()
    @JoinColumn(name = "code", referencedColumnName = "code")
    Currency currency;

    @Column(name = "from_date")
    Date fromDate;

    @Column(name = "how_much")
    double howMuch;

    @Column(name = "rate")
    double rate;

    @OneToOne()
    @JoinColumn(name = "base_currency", referencedColumnName = "code")
    Currency baseCurrency;

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
}
