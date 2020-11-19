package gbas.gtbch.sapod.repository;

import gbas.tvk.nsi.cash.Func;
import gbas.tvk.tpol3.TpolDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TPolRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TPolRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<TpolDocument> getDocuments(Date dateBegin, Date dateEnd) {
        return getDocuments(null, dateBegin, dateEnd);
    }

    public List<TpolDocument> getDocuments(String codeType, Date dateBegin, Date dateEnd) {
        List<Object> args = new ArrayList<>();

        String datesSql = "";

        if (dateBegin != null && dateEnd != null) {
            datesSql = "(date_end >= ? AND date_begin <= ?) AND\n";
            args.add(dateBegin);
            args.add(dateEnd);
        } else if (dateBegin != null) {
            datesSql = "date_begin >= ? AND\n";
            args.add(dateBegin);
        } else if (dateEnd != null) {
            datesSql = "date_end <= ? AND\n";
            args.add(dateEnd);
        }

        String codeTypeSql;
        if (codeType != null) {
            codeTypeSql = "type_code = ?\n";
            args.add(codeType);
        } else {
            codeTypeSql = "type_code IN ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')\n";
        }

        return jdbcTemplate.query(
                "select id,\n" +
                        "rtrim(type_code),\n" +
                        "rtrim(n_contract),\n" +
                        "date_begin,\n" +
                        "date_end,\n" +
                        "rtrim(name),\n" +
                        "n_pol,\n" +
                        "cod_tip_tarif,\n" +  //8
                        "dobor\n" +  //9
                        " from tvk_tarif\n" +
                        "WHERE\n" +
                        datesSql +
                        codeTypeSql +
                        "ORDER BY n_contract", (rs, i) -> {
                    final TpolDocument doc = new TpolDocument();
                    doc.id = rs.getInt(1);
                    doc.type_code = rs.getString(2);
                    doc.n_contract = rs.getString(3);
                    doc.date_begin = rs.getDate(4);
                    doc.date_end = rs.getDate(5);
                    doc.name = rs.getString(6);
                    doc.n_pol = rs.getInt(7);
                    doc.codTipTar = rs.getInt(8);
                    doc.codDobor = rs.getShort(9);
                    return doc;
                }, args.toArray(new Object[0]));
    }

    /**
     *
     * @return
     */
    public List<String[]> getBaseTarifList() {
        return getBaseTarifList(0);
    }

    /**
     * TPRow.tipTTar
     *
     * @param idTPol
     * @return
     */
    public List<String[]> getBaseTarifList(int idTPol) {
        return jdbcTemplate.query(idTPol == 0 ?
                        "select kod, name\n" +
                                "from tvk_tip_tar\n" +
                                "order by 1" :
                        "select k.kod, k.name\n" +
                                "from tvk_tip_tar k, tvk_t_pol t\n" +
                                "where k.kod = t.tip_t_tar and t.id = ?\n" +
                                "order by 1",
                idTPol == 0 ? null : new Object[]{idTPol},
                (rs, i) -> {
                    String[] result = new String[2];
                    result[0] = Func.iif(rs.getString(1));
                    result[1] = Func.iif(rs.getString(2));
                    return result;
                });
    }

    public List<String[]> getGroups() {
        return jdbcTemplate.query("select code, name from type_document where code IN ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')",
                (rs, i) -> {
                    String[] result = new String[2];
                    result[0] = Func.iif(rs.getString(1));
                    result[1] = Func.iif(rs.getString(2));
                    return result;
                });
    }

}
