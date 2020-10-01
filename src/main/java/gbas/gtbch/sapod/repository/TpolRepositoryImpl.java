package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.TpolItem;
import gbas.tvk.tpol3.TpolDocument;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TpolRepositoryImpl implements TpolRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TpolRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TpolRepositoryImpl(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static List<String[]> extractStringArrayList(ResultSet rs) throws SQLException {
        List<String[]> list = new ArrayList<>();

        final ResultSetMetaData data = rs.getMetaData();
        final int col = data.getColumnCount();
        while (rs.next()) {
            final String[] s = new String[col];
            for (int i = 0; i < col; i++) {
                s[i] = rs.getString(i + 1);
                if (s[i] != null) {
                    s[i] = s[i].trim();
                }
            }
            list.add(s);
        }

        return list;
    }

    @Override
    public List<String[]> getNsi(TpolItem item) {
        return jdbcTemplate.query(item.getItem().getSqlFromNSI(item.getSet()), TpolRepositoryImpl::extractStringArrayList);
    }

    @Override
    public List<String[]> getData(TpolItem item, int id_tpol) {
        return jdbcTemplate.query(item.getItem().getSqlSelected(id_tpol), TpolRepositoryImpl::extractStringArrayList);
    }

    public List<TpolDocument> getDocuments() {
        return jdbcTemplate.query(
                "select id, " +
                        "rtrim(type_code), " +
                        "rtrim(n_contract), " +
                        "date_begin, " +
                        "date_end, " +
                        "rtrim(name), " +
                        "n_pol, " +
                        "cod_tip_tarif, " +  //8
                        "dobor" +  //9
                        " from tvk_tarif \n" +
                        "WHERE /*(date_begin >= ? \n" +
                        "  AND date_end <= ?) \n" +
                        "  AND*/(type_code = 'base_tarif' \n" +
                        "  OR type_code = 'down_tarif' \n" +
                        "  OR type_code = 'polnom' \n" +
                        "  OR type_code = 'russia_tarif' \n" +
                        "  OR type_code = 'iskl_tarif' \n" +
                        "  OR type_code = 'tr1_bch') \n" +
                        "ORDER BY n_contract", rs -> {
                    List<TpolDocument> list = new ArrayList<>();

                    while (rs.next()) {
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
                        list.add(doc);
                    }

                    return list;
                });
    }

    public List<TPRow> getRows(int id_tarif) {
        return jdbcTemplate.query("select a.id, a.n_str, a.prim, a.tip_t_tar, b.n_tab, c.n_tab, " +
                "d.n_tab, a.kof, a.id_tab_ves, a.id_ves_norm, a.id_tab_kof, a.kof_sob, a.skid, a.id_tab_kofbs, e.n_tab, a.koleya " +
                "from tvk_t_pol a " +
                "left outer join tvk_group_t_ves b on a.id_tab_ves = b.id " +
                "left outer join tvk_group_t_kont c on a.id_ves_norm = c.id " +
                "left outer join tvk_group_t_kof d on a.id_tab_kof = d.id " +
                "left outer join tvk_group_t_kof e on a.id_tab_kofbs = e.id " +
                "where a.id_tarif= " + id_tarif + " order by 2 ", rs -> {
            List<TPRow> list = new ArrayList<>();
            while (rs.next()) {
                TPRow row = new TPRow();
                row.id = rs.getInt(1);
                row.nStr = rs.getInt(2);
                row.prim = rs.getString(3);
                row.tipTTar = rs.getDouble(4);
                row.tVes = rs.getDouble(5);
                row.vesNorm = rs.getDouble(6);
                row.nTab = rs.getInt(7);
                row.kof = rs.getDouble(8);
                row.id_tab_ves = rs.getInt(9);
                row.id_ves_norm = rs.getInt(10);
                row.id_tab_kof = rs.getInt(11);
                row.kof_sobst = rs.getDouble(12);
                row.skid = rs.getInt(13);
                row.id_tab_kofbs = rs.getInt(14);
                row.bs_tab = rs.getInt(15);
                row.koleya = rs.getInt(16);
                list.add(row);
            }

            return list;
        });
    }
}
