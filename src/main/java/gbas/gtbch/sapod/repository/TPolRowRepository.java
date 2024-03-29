package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.tpol.TpItemFilter;
import gbas.gtbch.sapod.model.tpol.TpRow;
import gbas.tvk.tpol3.service.TPItems;
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
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static gbas.gtbch.util.JdbcTemplateUtils.getSqlString;

@Component
public class TPolRowRepository {
    static final Logger logger = LoggerFactory.getLogger(TPolRowRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolRowRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<TpRow> getRows(int id_tarif, Map<String, String> filterMap) {
        return getRows(id_tarif, 0, filterMap);
    }

    private List<TpRow> getRows(int id_tarif, int id_row, Map<String, String> filterMap) {

        List<Object> args = new ArrayList<>();
        
        String sql = "select tvk_t_pol.id, tvk_t_pol.t_pol, tvk_t_pol.n_str, tvk_t_pol.prim, tvk_t_pol.tip_t_tar, tvk_t_pol.klas, b.n_tab as ves_n_tab, c.n_tab as kont_n_tab,\n" +
                "d.n_tab as kof_n_tab, tvk_t_pol.kof, tvk_t_pol.id_tab_ves, tvk_t_pol.id_ves_norm, tvk_t_pol.id_tab_kof, tvk_t_pol.kof_sob, tvk_t_pol.skid,\n" +
                "tvk_t_pol.id_tab_kofbs, e.n_tab as kofbs_n_tab, tvk_t_pol.koleya, tvk_t_pol.id_tarif\n" +
                "from tvk_t_pol\n" +
                "left outer join tvk_group_t_ves b on tvk_t_pol.id_tab_ves = b.id\n" +
                "left outer join tvk_group_t_kont c on tvk_t_pol.id_ves_norm = c.id\n" +
                "left outer join tvk_group_t_kof d on tvk_t_pol.id_tab_kof = d.id\n" +
                "left outer join tvk_group_t_kof e on tvk_t_pol.id_tab_kofbs = e.id\n";

        sql += TpItemFilter.getFilterSqlString(args, filterMap);
        
        if (id_tarif != 0) {
            sql += getSqlString(args, "tvk_t_pol.id_tarif = ?");
            args.add(id_tarif);
        }

        if (id_row != 0) {
            sql += getSqlString(args, "tvk_t_pol.id = ?");
            args.add(id_row);
        }
        
        sql += "order by 2";

        return jdbcTemplate.query(sql,
                args.toArray(),
                (rs, i) -> {
                    TpRow row = new TpRow();
                    row.id = rs.getInt("id");
                    row.tPol = rs.getInt("t_pol");
                    row.nStr = rs.getInt("n_str");
                    row.prim = rs.getString("prim");
                    row.tipTTar = rs.getDouble("tip_t_tar");
                    row.klas = rs.getDouble("klas");
                    row.tVes = rs.getDouble("ves_n_tab");
                    row.vesNorm = rs.getDouble("kont_n_tab");
                    row.nTab = rs.getInt("kof_n_tab");
                    row.kof = rs.getDouble("kof");
                    row.id_tab_ves = rs.getInt("id_tab_ves");
                    row.id_ves_norm = rs.getInt("id_ves_norm");
                    row.id_tab_kof = rs.getInt("id_tab_kof");
                    row.kof_sobst = rs.getDouble("kof_sob");
                    row.skid = rs.getInt("skid");
                    row.id_tab_kofbs = rs.getInt("id_tab_kofbs");
                    row.bs_tab = rs.getInt("kofbs_n_tab");
                    row.koleya = rs.getInt("koleya");
                    row.id_tarif = rs.getInt("id_tarif");
                    return row;
                });
    }

    /**
     * @param idRow
     * @return
     */
    public TpRow getRow(int idRow) {
        List<TpRow> rows = getRows(0, idRow, null);
        return rows != null && !rows.isEmpty() ? rows.get(0) : null;
    }

    /**
     * delete {@link TpRow}
     *
     * @param id
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteRow(int id) {
        for (TPItems item : TPItems.values()) {
            jdbcTemplate.update(
                    "delete from " + item.getItem().getTableName() + "  where id_t_pol = ?",
                    ps -> ps.setInt(1, id)
            );
        }
        return jdbcTemplate.update("delete from tvk_t_pol where id = ?",
                ps -> ps.setInt(1, id)
        ) != 0;
    }

    /**
     * create new or update existing {@link TpRow}
     *
     * @param row
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveRow(TpRow row) {

        if (row.id == 0) {
            // insert new row
            Integer maxNumber = jdbcTemplate.query(
                    "select max(n_str) as max_num from tvk_t_pol where id_tarif = ?",
                    new Object[]{row.id_tarif},
                    rs -> rs.next() ? rs.getInt("max_num") : null
            );

            row.nStr = maxNumber != null ? (maxNumber + 1) : 1;

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into tvk_t_pol (id_tarif, t_pol, n_str) values (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );

                preparedStatement.setInt(1, row.id_tarif);
                preparedStatement.setInt(2, row.tPol);
                preparedStatement.setInt(3, row.nStr);

                return preparedStatement;
            }, keyHolder);

            row.id = (int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0);
        }

        if (row.id != 0) {
            // update existing row
            if (row.nTab != 0) {
                Integer id = jdbcTemplate.query(
                        "select id from tvk_group_t_kof where n_tab = ?",
                        new Object[]{row.nTab},
                        rs -> rs.next() ? rs.getInt("id") : null
                );
                if (id != null) {
                    row.id_tab_kof = id;
                }
            } else {
                row.id_tab_kof = 0;
            }

            if (row.bs_tab != 0) {
                Integer id = jdbcTemplate.query(
                        "select id from tvk_group_t_kof where n_tab = ?",
                        new Object[]{row.bs_tab},
                        rs -> rs.next() ? rs.getInt("id") : null
                );
                if (id != null) {
                    row.id_tab_kofbs = id;
                }
            } else {
                row.id_tab_kofbs = 0;
            }

            if (row.tVes != 0) {
                Integer id = jdbcTemplate.query(
                        "select id from tvk_group_t_ves where n_tab = ?",
                        new Object[]{row.tVes},
                        rs -> rs.next() ? rs.getInt("id") : null
                );
                if (id != null) {
                    row.id_tab_ves = id;
                }
            } else {
                row.id_tab_ves = 0;
            }

            if (row.vesNorm != 0) {
                Integer id = jdbcTemplate.query(
                        "select id from tvk_group_t_kont where n_tab = ?",
                        new Object[]{row.vesNorm},
                        rs -> rs.next() ? rs.getInt("id") : null
                );
                if (id != null) {
                    row.id_ves_norm = id;
                }
            } else {
                row.id_ves_norm = 0;
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

    private Object getResultSetObject(ResultSet rs, int i) {
        try {
            return rs.getObject(i);
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Transactional(transactionManager = "sapodTransactionManager")
    public TpRow copyRow(int sourceRowId, int destinationDocumentId) {

        // read source row
        TpRow row = getRow(sourceRowId);

        if (row == null) return null;

        row.id = 0;
        if (destinationDocumentId != 0) {
            row.id_tarif = destinationDocumentId;
        }

        // save new row to destination document
        row.id = saveRow(row);

        if (row.id == 0) return null;

        // copy items
        for (TPItems item : TPItems.values()) {
            String table = item.getItem().getTableName();

            String sqlSelectCommand = "select " + String.join(", ", item.getItem().getTableFields()) + " " +
                    "from " + table + " " +
                    "where id_t_pol = ?";

            List<List<Object>> sourceList = jdbcTemplate.query(
                    sqlSelectCommand,
                    (rs, rowNum) -> IntStream.rangeClosed(1, rs.getMetaData().getColumnCount()).mapToObj(i -> getResultSetObject(rs, i)).collect(Collectors.toList()),
                    sourceRowId
            );

            if (sourceList == null || sourceList.isEmpty()) continue;

            String sqlInsertCommand = "insert into " + table + " " +
                    "(id_t_pol, " + String.join(", ", item.getItem().getTableFields()) + ") " +
                    "values (?," + String.join(",", Collections.nCopies(item.getItem().getTableFields().length, "?")) + ")";

            List<Object[]> destinationList = sourceList.stream().peek(list -> list.add(0, row.id)).map(list -> list.toArray(new Object[0])).collect(Collectors.toList());

            jdbcTemplate.batchUpdate(
                    sqlInsertCommand,
                    destinationList
            );

        }

        return row;
    }

}