package gbas.gtbch.sapod.repository;

import gbas.tvk.tpol3.TvkTVes;
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
public class TPolTVesRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TPolTVesRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolTVesRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static TvkTVes mapVORow(ResultSet rs, int i) throws SQLException {
        TvkTVes tvkTVes = new TvkTVes();
        tvkTVes.id = rs.getInt("id");
        tvkTVes.id_group_t_ves = rs.getInt("id_tab");
        tvkTVes.minV = rs.getDouble("min_v");
        tvkTVes.maxV = rs.getDouble("max_v");
        tvkTVes.vKat = rs.getDouble("v_kat");
        tvkTVes.vKatR = rs.getDouble("v_kat_r");
        tvkTVes.kof = rs.getDouble("kof");
        tvkTVes.nTab = rs.getInt("n_tab");
        return tvkTVes;
    }

    /**
     *
     * @param id
     * @return
     */
    public TvkTVes getVO(int id) {
        List<TvkTVes> list = jdbcTemplate.query("select v.*, t.n_tab\n" +
                        "from tvk_t_ves v\n" +
                        "left outer join tvk_group_t_ves t\n" +
                        "on v.id_tab = t.id\n" +
                        "where v.id = ?" +
                        "order by 1,2,3",
                new Object[]{id},
                TPolTVesRepository::mapVORow);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     *
     * @return
     */
    public List<TvkTVes> getVOList() {
        return getVOList(0);
    }

    /**
     *
     * @param idTPol
     * @return
     */
    public List<TvkTVes> getVOList(int idTPol) {
        return jdbcTemplate.query("select v.*, t.n_tab\n" +
                        "from tvk_t_ves v\n" +
                        "left outer join tvk_group_t_ves t\n" +
                        "on v.id_tab = t.id\n" +
                        (idTPol == 0 ? "" : "where v.id_tab in (select id_tab_ves from tvk_t_pol where id = ?)\n") +
                        "order by 1,2,3",
                idTPol == 0 ? null : new Object[]{idTPol},
                TPolTVesRepository::mapVORow);
    }

    /**
     *
     * @param tVes
     * @return
     */
    @Transactional
    public int saveVO(TvkTVes tVes) {
        Integer id = jdbcTemplate.query("select id from tvk_group_t_ves where n_tab = ?",
                new Object[]{tVes.nTab},
                resultSet -> {
                    return resultSet.getInt("id");
                });

        if (id == null) {
            // todo (check if need to insert)
            return 0;
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            jdbcTemplate.update(connection -> {
//                PreparedStatement preparedStatement = connection.prepareStatement("insert into tvk_group_t_ves (n_tab) values (?)");
//                preparedStatement.setInt(1, tVes.nTab);
//                return preparedStatement;
//            }, keyHolder);
//            id = (Integer) keyHolder.getKey();
        }

        tVes.id_group_t_ves = id;

        {
            id = jdbcTemplate.query("select id_tab, min_v from tvk_t_ves where id_tab = ? and min_v = ?",
                    new Object[]{tVes.id_group_t_ves, tVes.minV},
                    resultSet -> {
                        return resultSet.getInt("id");
                    });

            if (id != null) {
                tVes.id = id;
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;
            if (tVes.id == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_t_ves (id_tab, min_v, max_v, v_kat, v_kat_r, kof) " +
                        "values (?, ?, ?, ?, ?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("update tvk_t_ves set id_tab = ?, min_v = ?, max_v = ?, v_kat = ?, v_kat_r = ?, kof = ? " +
                        "where id = ?");
            }

            preparedStatement.setInt(1, tVes.id_group_t_ves);
            preparedStatement.setDouble(2, tVes.minV);
            preparedStatement.setDouble(3, tVes.maxV);
            preparedStatement.setDouble(4, tVes.vKat);
            preparedStatement.setDouble(5, tVes.vKatR);
            preparedStatement.setDouble(6, tVes.kof);

            if (tVes.id != 0) {
                preparedStatement.setInt(7, tVes.id);
            }

            return preparedStatement;
        }, keyHolder);

        if (tVes.id == 0) {
            tVes.id = (int) keyHolder.getKey();
        }

        return tVes.id;
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean deleteVO(int id) {
        return jdbcTemplate.update("delete from tvk_t_ves where id = " + id) != 0;
    }


}
