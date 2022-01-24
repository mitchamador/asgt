package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.tpol.TpTvkKof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TPolKofRepository {
    public static final Logger LOGGER = LoggerFactory.getLogger(TPolKofRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolKofRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static TpTvkKof mapRasstRow(ResultSet rs, int i) throws SQLException {
        TpTvkKof tvkKof = new TpTvkKof();
        tvkKof.id = rs.getInt("id");
        tvkKof.nTab = rs.getInt("n_tab");
        tvkKof.minRast = rs.getDouble("min_rast");
        tvkKof.maxRast = rs.getDouble("max_rast");
        tvkKof.minVes = rs.getDouble("min_ves");
        tvkKof.maxVes = rs.getDouble("max_ves");
        tvkKof.kof = rs.getDouble("kof");
        tvkKof.id_group_t_kof = rs.getInt("id_tab");
        tvkKof.nz = rs.getDouble("nz");
        return tvkKof;

    }

    /**
     * @param id
     * @return
     */
    public TpTvkKof getKof(int id) {
        List<TpTvkKof> list = jdbcTemplate.query("select a.id, b.n_tab, a.min_rast, a.max_rast, a.min_ves, a.max_ves, a.kof, a.id_tab, a.nz\n" +
                        "from tvk_kof a\n" +
                        "left outer join tvk_group_t_kof b\n" +
                        "on a.id_tab = b.id\n" +
                        "where a.id = ?\n" +
                        "order by 1",
                new Object[]{id},
                TPolKofRepository::mapRasstRow);

        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     * @return
     */
    public List<TpTvkKof> getKofList() {
        return _getKofList(0, null);
    }

    /**
     * @param idTPol
     * @return
     */
    public List<TpTvkKof> getKofList(int idTPol) {
        return _getKofList(idTPol, "id_tab_kof");
    }

    /**
     * @param idTPol
     * @return
     */
    public List<TpTvkKof> getKofBsList(int idTPol) {
        return _getKofList(idTPol, "id_tab_kofbs");
    }

    private List<TpTvkKof> _getKofList(int idTPol, String columnName) {
        return jdbcTemplate.query("select a.id, b.n_tab, a.min_rast, a.max_rast, a.min_ves, a.max_ves, a.kof, a.id_tab, a.nz\n" +
                        "from tvk_kof a\n" +
                        "left outer join tvk_group_t_kof b\n" +
                        "on a.id_tab = b.id\n" +
                        (idTPol == 0 ? "" : "where a.id_tab in (select " + columnName + " from tvk_t_pol where id = ?)\n") +
                        "order by 1",
                idTPol == 0 ? null : new Object[]{idTPol},
                TPolKofRepository::mapRasstRow);
    }

    /**
     * @param kof
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveKof(TpTvkKof kof) {

        Integer id = jdbcTemplate.query("select id from tvk_group_t_kof where n_tab = ?",
                resultSet -> resultSet.next() ? resultSet.getInt("id") : null,
                kof.nTab
        );

        if (id == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into tvk_group_t_kof (n_tab) values (?)",
                        Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, kof.nTab);
                return preparedStatement;
            }, keyHolder);
            id = (Integer) keyHolder.getKey();
        }

        if (id == null) return 0;

        kof.id_group_t_kof = id;

        {
            id = jdbcTemplate.query("select id from tvk_kof where id_tab = ? and min_rast = ?",
                    resultSet -> resultSet.next() ? resultSet.getInt("id") : null,
                    kof.id_group_t_kof, kof.minRast);

            if (id != null) {
                kof.id = id;
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;

            if (kof.id == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_kof (id_tab, min_rast, max_rast, min_ves, max_ves, kof, nz) " +
                                " values (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update tvk_kof set id_tab=?, min_rast=?, max_rast=?, min_ves=?, max_ves=?, kof=?, nz=? " +
                        " where id = ?");
            }

            preparedStatement.setInt(1, kof.id_group_t_kof);
            preparedStatement.setDouble(2, kof.minRast);
            preparedStatement.setDouble(3, kof.maxRast);
            preparedStatement.setDouble(4, kof.minVes);
            preparedStatement.setDouble(5, kof.maxVes);
            preparedStatement.setDouble(6, kof.kof);
            preparedStatement.setDouble(7, kof.nz);

            if (kof.id != 0) {
                preparedStatement.setInt(8, kof.id);
            }

            return preparedStatement;
        }, keyHolder);

        if (kof.id == 0) {
            kof.id = (int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0);
        }

        return kof.id;
    }

    /**
     * @param id
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteKof(int id) {
        return jdbcTemplate.update(
                "delete from tvk_kof where id = ?",
                id
        ) != 0;
    }

    private Object getResultSetObject(ResultSet rs, int i) {
        try {
            return rs.getObject(i);
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * copy table
     * @param tab
     * @param bsTab
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int copyKofTab(int tab, boolean bsTab) {

        List<List<Object>> sourceTab = jdbcTemplate.query(
                "SELECT k.min_rast, k.max_rast, k.min_ves, k.max_ves, k.kof, k.nz\n" +
                        "FROM tvk_kof k\n" +
                        "JOIN tvk_group_t_kof g ON g.id = k.id_tab\n" +
                        "WHERE g.n_tab = ?",
                (rs, rowNum) -> IntStream.rangeClosed(1, bsTab ? 5 : 6).mapToObj(i -> getResultSetObject(rs, i)).collect(Collectors.toList()),
                tab);

        if (sourceTab == null || sourceTab.isEmpty()) {
            return 0;
        }

        // get max table's number
        Integer maxNumber = jdbcTemplate.query(
                "select max(n_tab) as max_num from tvk_group_t_kof",
                rs -> rs.next() ? rs.getInt("max_num") : null
        );

        if (maxNumber == null) {
            return 0;
        }
        int finalMaxNumber = maxNumber + 1;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into tvk_group_t_kof (n_tab) values (?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, finalMaxNumber);
            return preparedStatement;
        }, keyHolder);

        Integer id = (Integer) keyHolder.getKey();

        if (id == null) {
            return 0;
        }

        List<Object[]> insertList = sourceTab.stream().peek(list -> {
            list.add(0, id);
            if (bsTab) {
                list.add(0);
            }
        }).map(list -> list.toArray(new Object[0])).collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "insert into tvk_kof (id_tab, min_rast, max_rast, min_ves, max_ves, kof, nz)\n" +
                        "values (?, ?, ?, ?, ?, ?, ?)",
                insertList
        );

        return finalMaxNumber;
    }

    /**
     *
     * @param tab
     * @return
     */
    public boolean deleteKofTab(int tab) {

        Integer id = jdbcTemplate.query(
                "select id from tvk_group_t_kof where n_tab = ?",
                rs -> rs.next() ? rs.getInt("id") : null,
                tab
        );

        if (id == null) return false;

        try {
            jdbcTemplate.update("delete from tvk_kof where id_tab = ?", id);

            jdbcTemplate.update("delete from tvk_group_t_kof where id = ?", id);

            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}