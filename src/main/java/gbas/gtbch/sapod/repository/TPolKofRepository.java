package gbas.gtbch.sapod.repository;

import gbas.tvk.tpol3.TvkKof;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class TPolKofRepository {
    public static final Logger LOGGER = LoggerFactory.getLogger(TPolKofRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolKofRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static TvkKof mapRasstRow(ResultSet rs, int i) throws SQLException {
        TvkKof tvkKof = new TvkKof();
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
    public TvkKof getKof(int id) {
        List<TvkKof> list = jdbcTemplate.query("select a.id, b.n_tab, a.min_rast, a.max_rast, a.min_ves, a.max_ves, a.kof, a.id_tab, a.nz\n" +
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
    public List<TvkKof> getKofList() {
        return getKofList(0);
    }

    /**
     * @param idTPol
     * @return
     */
    public List<TvkKof> getKofList(int idTPol) {
        return jdbcTemplate.query("select a.id, b.n_tab, a.min_rast, a.max_rast, a.min_ves, a.max_ves, a.kof, a.id_tab, a.nz\n" +
                        "from tvk_kof a\n" +
                        "left outer join tvk_group_t_kof b\n" +
                        "on a.id_tab = b.id\n" +
                        (idTPol == 0 ? "" : "where a.id_tab in (select id_tab_kof from tvk_t_pol where id = ?)\n") +
                        "order by 1",
                idTPol == 0 ? null : new Object[]{idTPol},
                TPolKofRepository::mapRasstRow);
    }

    /**
     * @param kof
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveKof(TvkKof kof) {

        Integer id = jdbcTemplate.query("select id from tvk_group_t_kof where n_tab = ?",
                new Object[]{kof.nTab},
                resultSet -> resultSet.next() ? resultSet.getInt("id") : null);

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

/*
        {
            id = jdbcTemplate.query("select id from tvk_kof where id_tab = ? and min_rast = ?",
                    new Object[]{kof.id_group_t_kof, kof.minRast},
                    resultSet -> {
                        return resultSet.getInt("id");
                    });

            if (id != null) {
                kof.id = id;
            }
        }
*/

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
            ;
        }

        return kof.id;
    }

    /**
     * @param id
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteKof(int id) {
        return jdbcTemplate.update("delete from tvk_kof where id = " + id) != 0;
    }
}