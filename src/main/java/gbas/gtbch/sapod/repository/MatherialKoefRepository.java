package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.matherials.Matherial;
import gbas.gtbch.sapod.model.matherials.MatherialKoef;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.util.UtilDate;
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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Component
public class MatherialKoefRepository {
    public static final Logger logger = LoggerFactory.getLogger(MatherialKoefRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MatherialKoefRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * get all {@link MatherialKoef} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    public List<MatherialKoef> getKoefList(int idMatherial) {
        return jdbcTemplate.query("select * from tvk_kof_sbor where id_object = ? order by date_begin desc", (rs, i) -> {
            MatherialKoef matherialKoef = new MatherialKoef();
            matherialKoef.setId(rs.getInt("id"));
            matherialKoef.setIdMatherial(rs.getInt("id_object"));
            matherialKoef.setCodeMatherial(Func.iif(rs.getString("code_object")));
            matherialKoef.setDate(rs.getDate("date_begin"));
            matherialKoef.setKoef(rs.getDouble("koef"));
            return matherialKoef;
        }, idMatherial);
    }

    /**
     * save all {@link MatherialKoef} for {@link Matherial}
     * @param matherial
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public Matherial saveKoef(Matherial matherial) {

        deleteKoef(matherial.getId());

        if (matherial.getKoefList() != null) {
            matherial.getKoefList().forEach(matherialKoef -> {
                matherialKoef.setId(0);
                saveKoef(matherial, matherialKoef);
            });
        }

        return matherial;
    }

    /**
     * save {@link MatherialKoef}
     * @param matherialKoef
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveKoef(MatherialKoef matherialKoef) {
        return saveKoef(null, matherialKoef);
    }

    /**
     * @param matherialKoef
     * @return
     */
    private int saveKoef(Matherial matherial, MatherialKoef matherialKoef) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;

            if (matherialKoef.getId() == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_kof_sbor (id_object, date_begin, koef, code_object) " +
                                " values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update tvk_kof_sbor set id_object=?, date_begin=?, koef=?, code_object=? " +
                        " where id = ?");
            }

            preparedStatement.setInt(1,  matherial == null ? matherialKoef.getIdMatherial() : matherial.getId());
            preparedStatement.setTimestamp(2, new Timestamp(UtilDate.clearHHmmssSSS(matherialKoef.getDate()).getTime()));
            preparedStatement.setDouble(3, matherialKoef.getKoef());
            preparedStatement.setString(4, matherial == null ? matherialKoef.getCodeMatherial() : matherial.getCode());

            if (matherialKoef.getId() != 0) {
                preparedStatement.setInt(5, matherialKoef.getId());
            }

            return preparedStatement;
        }, keyHolder);

        if (matherialKoef.getId() == 0) {
            matherialKoef.setId((int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0));
        }

        return matherialKoef.getId();
    }

    /**
     * delete {@link MatherialKoef}
     * @param id tvk_kof_sbor.id
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteKoefItem(int id) {
        return jdbcTemplate.update(
                "delete from tvk_kof_sbor where id = ?",
                ps -> ps.setInt(1, id)
        ) != 0;
    }

    /**
     * delete all {@link MatherialKoef} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteKoef(int idMatherial) {
        return jdbcTemplate.update(
                "delete from tvk_kof_sbor where id_object = ?",
                ps -> ps.setInt(1, idMatherial)
        ) != 0;
    }

}