package gbas.gtbch.sapod.model;

import gbas.gtbch.util.UtilDate8;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rate_of_recalc")
public class RecalcRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "date_begin")
    private Date dateBegin;

    @Column(name = "rate")
    private double rate;

    @Column(name = "index")
    private double index;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return String.format("RecalcRate { dateBegin=%s, rate=%.4f, index=%4f }", UtilDate8.getStringDate(dateBegin), rate, index);
    }

}
