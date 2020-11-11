package gbas.gtbch.sapod.repository;

import gbas.tvk.tpol3.TvkTOsr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class TPolTOsrRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TPolTOsrRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolTOsrRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static TvkTOsr mapContRow(ResultSet rs, int i) throws SQLException {
        TvkTOsr tvkTOsr = new TvkTOsr();
        tvkTOsr.id = rs.getInt("id");
        tvkTOsr.nTab = rs.getInt("n_tab");
        tvkTOsr.grpk = rs.getInt("grpk");
        tvkTOsr.nSt = rs.getDouble("n_str");
        tvkTOsr.kof = rs.getDouble("kod");
        tvkTOsr.id_group_kont = rs.getInt("id_group_kont");
        tvkTOsr.id_group_ts = rs.getInt("id_group_ts");
        return tvkTOsr;
    }

    /**
     * @param id
     * @return
     */
    public TvkTOsr getCont(int id) {
        List<TvkTOsr> list = jdbcTemplate.query("select b.id, a.n_tab, b.grpk, c.n_str, b.kof, b.id_group_kont, c.id as id_group_ts\n" +
                        "from tvk_t_osr b\n" +
                        "left outer join tvk_group_t_kont a\n" +
                        "on b.id_group_kont = a.id\n" +
                        "left outer join tvk_group_ts c\n" +
                        "on b.id_group_ts = c.id\n" +
                        "where b.id = ?" +
                        "order by 1",
                new Object[]{id},
                TPolTOsrRepository::mapContRow);

        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     *
     * @return
     */
    public List<TvkTOsr> getContList() {
        return getContList(0);
    }

    /**
     *
     * @param idTPol
     * @return
     */
    public List<TvkTOsr> getContList(int idTPol) {
        return jdbcTemplate.query("select b.id, a.n_tab, b.grpk, c.n_str, b.kof, b.id_group_kont, c.id as id_group_ts\n" +
                        "from tvk_t_osr b\n" +
                        "left outer join tvk_group_t_kont a\n" +
                        "on b.id_group_kont = a.id\n" +
                        "left outer join tvk_group_ts c\n" +
                        "on b.id_group_ts = c.id\n" +
                        (idTPol == 0 ? "" : "where b.id_group_kont in (select id_ves_norm from tvk_t_pol where id = ?)\n") +
                        "order by 1 ",
                idTPol == 0 ? null : new Object[]{idTPol},
                TPolTOsrRepository::mapContRow);
    }

    /**
     *
     * @param osr
     * @return
     */
    @Transactional
    public int saveCont(TvkTOsr osr) {
        Integer id;

        {
            id = jdbcTemplate.query("select id from tvk_group_t_kont where n_tab = ?",
                    new Object[]{osr.nTab},
                    resultSet -> {
                        return resultSet.getInt("id");
                    });

            if (id == null) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement("insert into tvk_group_t_kont (n_tab) values (?)");
                    preparedStatement.setInt(1, osr.nTab);
                    return preparedStatement;
                }, keyHolder);
                id = (Integer) keyHolder.getKey();
            }

            if (id == null) return 0;

            osr.id_group_kont = id;
        }

        {
            id = jdbcTemplate.query("select id from tvk_group_ts where n_str = ?",
                    new Object[]{osr.nSt},
                    resultSet -> {
                        return resultSet.getInt("id");
                    });

            if (id == null) return 0;

            osr.id_group_ts = id;
        }

        {
            id = jdbcTemplate.query("select id from tvk_s_grp_kon where grpk = ?",
                    new Object[]{osr.grpk},
                    resultSet -> {
                        return resultSet.getInt("id");
                    });

            if (id == null) return 0;

            osr.id_grpk = id;
        }

        {
            id = jdbcTemplate.query("select id from tvk_t_osr where id_group_kont = ? and grpk = ?",
                    new Object[]{osr.id_group_kont, osr.grpk},
                    resultSet -> {
                        return resultSet.getInt("id");
                    });

            if (id != null) {
                osr.id = id;
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;
            if (osr.id == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_t_osr (id_group_kont, id_grpk, id_group_ts, grpk, kof) " +
                        " values (?, ?, ?, ?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("update tvk_t_osr set id_group_kont = ?, id_grpk = ?, id_group_ts = ?, kof = ?, grpk = ? " +
                        " where id = ?");
            }

            preparedStatement.setInt(1, osr.id_group_kont);
            preparedStatement.setInt(2, osr.id_group_ts);
            preparedStatement.setInt(3, osr.id_grpk);
            preparedStatement.setInt(4, (int) osr.grpk);
            preparedStatement.setDouble(5, osr.kof);

            if (osr.id != 0) {
                preparedStatement.setInt(6, osr.id);
            }

            return preparedStatement;
        }, keyHolder);

        if (osr.id == 0) {
            osr.id = (int) keyHolder.getKey();
        }

        return osr.id;
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean deleteCont(int id) {
        return jdbcTemplate.update("delete from tvk_t_osr where id = " + id) != 0;
    }

}
