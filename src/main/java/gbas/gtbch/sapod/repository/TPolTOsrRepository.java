package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.tpol.TpTvkTOsr;
import gbas.gtbch.web.request.KeyValue;
import gbas.tvk.nsi.cash.Func;
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
public class TPolTOsrRepository {
    public static final Logger LOGGER = LoggerFactory.getLogger(TPolTOsrRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolTOsrRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public static TpTvkTOsr mapContRow(ResultSet rs, int i) throws SQLException {
        TpTvkTOsr tvkTOsr = new TpTvkTOsr();
        tvkTOsr.id = rs.getInt("id");
        tvkTOsr.nTab = rs.getInt("n_tab");
        tvkTOsr.grpk = rs.getInt("grpk");
        tvkTOsr.nSt = rs.getDouble("n_str");
        tvkTOsr.kof = rs.getDouble("kof");
        tvkTOsr.id_group_kont = rs.getInt("id_group_kont");
        tvkTOsr.id_group_ts = rs.getInt("id_group_ts");
        return tvkTOsr;
    }

    /**
     * @param id
     * @return
     */
    public TpTvkTOsr getCont(int id) {
        List<TpTvkTOsr> list = jdbcTemplate.query("select b.id, a.n_tab, b.grpk, c.n_str, b.kof, b.id_group_kont, c.id as id_group_ts\n" +
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
     * @return
     */
    public List<TpTvkTOsr> getContList() {
        return getContList(0);
    }

    /**
     * @param idTPol
     * @return
     */
    public List<TpTvkTOsr> getContList(int idTPol) {
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
     * @param osr
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveCont(TpTvkTOsr osr) {
        Integer id;

        {
            id = jdbcTemplate.query("select id from tvk_group_t_kont where n_tab = ?",
                    new Object[]{osr.nTab},
                    resultSet -> resultSet.next() ? resultSet.getInt("id") : null
            );

            if (id == null) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "insert into tvk_group_t_kont (n_tab) values (?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
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
                    resultSet -> resultSet.next() ? resultSet.getInt("id") : null
            );

            if (id == null) return 0;

            osr.id_group_ts = id;
        }

        {
            id = jdbcTemplate.query("select id from tvk_s_grp_kon where grpk = ?",
                    new Object[]{osr.grpk},
                    resultSet -> resultSet.next() ? resultSet.getInt("id") : null
            );

            if (id == null) return 0;

            osr.id_grpk = id;
        }

        {
            id = jdbcTemplate.query("select id from tvk_t_osr where id_group_kont = ? and grpk = ?",
                    new Object[]{osr.id_group_kont, osr.grpk},
                    resultSet -> resultSet.next() ? resultSet.getInt("id") : null
            );

            if (id != null) {
                osr.id = id;
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;
            if (osr.id == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_t_osr (id_group_kont, id_grpk, id_group_ts, grpk, kof)" +
                                " values (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update tvk_t_osr set id_group_kont = ?, id_grpk = ?, id_group_ts = ?, grpk = ?, kof = ?" +
                        " where id = ?");
            }

            preparedStatement.setInt(1, osr.id_group_kont);
            preparedStatement.setInt(2, osr.id_grpk);
            preparedStatement.setInt(3, osr.id_group_ts);
            preparedStatement.setInt(4, (int) osr.grpk);
            preparedStatement.setDouble(5, osr.kof);

            if (osr.id != 0) {
                preparedStatement.setInt(6, osr.id);
            }

            return preparedStatement;
        }, keyHolder);

        if (osr.id == 0) {
            osr.id = (int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0);
        }

        return osr.id;
    }

    /**
     * @param id
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteCont(int id) {
        return jdbcTemplate.update(
                "delete from tvk_t_osr where id = ?",
                ps -> ps.setInt(1, id)
        ) != 0;
    }

    public List<KeyValue> getNstrValues() {
        return jdbcTemplate.query("SELECT n_str, name FROM tvk_group_ts\n" +
                        "ORDER BY 1\n ",
                (rs, rowNum) -> new KeyValue(rs.getString("n_str"), Func.iif(rs.getString("name")))
        );
    }

    public List<KeyValue> getGrpkValues() {
        return jdbcTemplate.query("SELECT grpk, rem FROM tvk_s_grp_kon\n" +
                        "ORDER BY 1\n ",
                (rs, rowNum) -> new KeyValue(rs.getString("grpk"), Func.iif(rs.getString("rem")))
        );
    }
}