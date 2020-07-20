package gbas.gtbch.sapod.model;

import gbas.tvk.util.UtilDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tpol_import_date")
public class TpImportDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "date_import")
    private Date dateImport;

    @Column(name = "date_create")
    private Date dateCreate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateImport() {
        return dateImport;
    }

    public void setDateImport(Date dateImport) {
        this.dateImport = dateImport;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Override
    public String toString() {
        return "TpImportDate{" +
                "dateImport=" + dateImport +
                ", dateCreate=" + dateCreate +
                '}';
    }

    public String getTpDateString() {
        return dateCreate != null ? ("ТП от " + UtilDate.getStringDateTime(dateCreate)) : "";
    }
}
