package gbas.gtbch.sapod.repository;

import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

@Component
public class TPolRowRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TPolRowRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolRowRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<TPRow> getRows(int id_tarif) {
        return jdbcTemplate.query("select a.id, a.n_str, a.prim, a.tip_t_tar, b.n_tab, c.n_tab,\n" +
                        "d.n_tab, a.kof, a.id_tab_ves, a.id_ves_norm, a.id_tab_kof, a.kof_sob, a.skid, a.id_tab_kofbs, e.n_tab, a.koleya\n" +
                        "from tvk_t_pol a\n" +
                        "left outer join tvk_group_t_ves b on a.id_tab_ves = b.id\n" +
                        "left outer join tvk_group_t_kont c on a.id_ves_norm = c.id\n" +
                        "left outer join tvk_group_t_kof d on a.id_tab_kof = d.id\n" +
                        "left outer join tvk_group_t_kof e on a.id_tab_kofbs = e.id\n" +
                        "where a.id_tarif=? order by 2",
                new Object[]{id_tarif},
                (rs, i) -> {
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
                    return row;
                });
    }

    /**
     * @param idRow
     * @return
     */
    public TPRow getRow(int idRow) {
        List<TPRow> rows = jdbcTemplate.query("select a.id, a.t_pol, a.n_str, a.prim, a.tip_t_tar, b.n_tab, klas,\n" +
                        "c.n_tab, d.n_tab, a.kof, a.skid,\n" +
                        "a.id_tab_ves, a.id_ves_norm, a.id_tab_kof, a.kof_sob, a.id_tab_kofbs, e.n_tab, a.koleya\n" +
                        "from tvk_t_pol a\n" +
                        "left outer join tvk_group_t_ves  b on a.id_tab_ves = b.id\n" +
                        "left outer join tvk_group_t_kont c on a.id_ves_norm = c.id\n" +
                        "left outer join tvk_group_t_kof  d on a.id_tab_kof  = d.id\n" +
                        "left outer join tvk_group_t_kof  e on a.id_tab_kofbs  = e.id\n" +
                        "where a.id = ?",
                new Object[]{idRow},
                (rs, i) -> {
                    TPRow row = new TPRow();
                    row.id = rs.getInt(1);
                    row.tPol = rs.getInt(2);
                    row.nStr = rs.getInt(3);
                    row.prim = rs.getString(4);
                    row.tipTTar = rs.getDouble(5);
                    row.tVes = rs.getDouble(6);             // tvk_group_t_ves.n_tab (tvk_group_t_ves.id = tvk_t_pol.id_tab_ves)
                    row.klas = rs.getDouble(7);
                    row.vesNorm = rs.getDouble(8);
                    row.nTab = rs.getInt(9);                // tvk_group_t_kont.n_tab (tvk_group_t_kont.id = tvk_t_pol.id_ves_norm)
                    row.kof = rs.getDouble(10);             // tvk_group_t_kof.n_tab (tvk_group_t_kof.id = tvk_t_pol.id_tab_kof)
                    row.skid = rs.getInt(11);
                    row.id_tab_ves = rs.getInt(12);
                    row.id_ves_norm = rs.getInt(13);
                    row.id_tab_kof = rs.getInt(14);
                    row.kof_sobst = rs.getDouble(15);
                    row.id_tab_kofbs = rs.getInt(16);
                    row.bs_tab = rs.getInt(17);             // tvk_group_t_kof.n_tab (tvk_group_t_kof.id = tvk_t_pol.id_tab_kofbs)
                    row.koleya = rs.getInt(18);
                    return row;
                });
        return rows != null && !rows.isEmpty() ? rows.get(0) : null;
    }

    /**
     * delete {@link TPRow}
     *
     * @param id
     * @return
     */
    public boolean deleteRow(int id) {
        return jdbcTemplate.update("delete from tvk_t_pol where id = " + id) != 0;
    }

    /**
     * create new or update existing {@link TPRow}
     *
     * @param row
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveRow(TPRow row) {

        if (row.id == 0) {
            // insert new row
            KeyHolder keyHolder = new GeneratedKeyHolder();

            Integer maxNumber = jdbcTemplate.query(
                    "select max(n_str) from tvk_t_pol where id_tarif = ?",
                    new Object[]{row.id_tarif},
                    rs -> {
                        return rs.getInt(1);
                    });

            row.nStr = maxNumber != null ? (maxNumber + 1) : 1;

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into tvk_t_pol (id_tarif, t_pol, n_str) values (?, ?, ?)");

                preparedStatement.setInt(1, row.id_tarif);
                preparedStatement.setInt(2, row.tPol);
                preparedStatement.setInt(3, row.nStr);

                return preparedStatement;
            }, keyHolder);

            row.id = (int) keyHolder.getKey();
        }

        if (row.id != 0) {
            // update existing row
            if (row.nTab != 0) {
                Integer id = jdbcTemplate.query(
                        "select id from tvk_group_t_kof where n_tab = ?",
                        new Object[]{row.nTab}, rs -> {
                            return rs.getInt(1);
                        });
                if (id != null) {
                    row.id_tab_kof = id;
                }
            } else {
                row.id_tab_kof = 0;
            }

            if (row.bs_tab != 0) {
                Integer id = jdbcTemplate.query(
                        "select id from tvk_group_t_kof where n_tab = ?",
                        new Object[]{row.bs_tab}, rs -> {
                            return rs.getInt(1);
                        });
                if (id != null) {
                    row.id_tab_kofbs = id;
                }
            } else {
                row.id_tab_kofbs = 0;
            }

            jdbcTemplate.update(connection -> {
                DecimalFormat df = new DecimalFormat("####.##########");
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(dfs);

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "update tvk_t_pol set n_str = ?, prim = ?, tip_t_tar = ?, klas = ?, id_tab_kof = ?, id_tab_ves = ?, " +
                                "id_ves_norm = ?, skid = ?, kof = ?, kof_sob = ?, id_tab_kofbs = ?, koleya = ?, t_pol = ? where id = ?"
                );

                preparedStatement.setInt(1, row.nStr);
                preparedStatement.setString(2, row.prim);
                preparedStatement.setDouble(3, row.tipTTar);
                preparedStatement.setDouble(4, row.klas);

                if (row.id_tab_kof != 0) {
                    preparedStatement.setInt(5, row.id_tab_kof);
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }

                if (row.id_tab_ves != 0) {
                    preparedStatement.setInt(6, row.id_tab_ves);
                } else {
                    preparedStatement.setNull(6, Types.INTEGER);
                }

                if (row.id_ves_norm != 0) {
                    preparedStatement.setInt(7, row.id_ves_norm);
                } else {
                    preparedStatement.setNull(7, Types.INTEGER);
                }
                preparedStatement.setInt(8, row.skid);
                preparedStatement.setString(9, df.format(row.kof));
                preparedStatement.setString(10, df.format(row.kof_sobst));
                if (row.id_tab_kofbs != 0)
                    preparedStatement.setInt(11, row.id_tab_kofbs);
                else
                    preparedStatement.setNull(11, Types.INTEGER);

                preparedStatement.setInt(12, row.koleya);

                preparedStatement.setInt(13, row.tPol);

                preparedStatement.setInt(14, row.id);

                return preparedStatement;
            });
        }

        return row.id;
    }

}
