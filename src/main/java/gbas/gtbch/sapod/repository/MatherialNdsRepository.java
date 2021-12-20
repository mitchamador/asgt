package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.matherials.Matherial;
import gbas.gtbch.sapod.model.matherials.MatherialNds;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Component
public class MatherialNdsRepository {
    public static final Logger logger = LoggerFactory.getLogger(MatherialNdsRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MatherialNdsRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private MatherialNds getMatherialNds(ResultSet rs) throws SQLException {
        MatherialNds matherialNds = new MatherialNds();
        matherialNds.setId(rs.getInt("id"));
        matherialNds.setIdMatherial(rs.getInt("id_matherial"));
        matherialNds.setCodeMatherial(Func.iif(rs.getString("code_object")));
        matherialNds.setDate(rs.getDate("date_begin"));
        matherialNds.setValue(rs.getInt("value"));
        return matherialNds;
    }

    /**
     * get all {@link MatherialNds} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    public List<MatherialNds> getNdsList(int idMatherial) {
        return jdbcTemplate.query(
                "select * from tvk_nds where id_matherial = ? order by date_begin desc",
                (rs, i) -> getMatherialNds(rs),
                idMatherial);
    }

    /**
     * get {@link MatherialNds}
     * @param idItem
     * @return
     */
    public MatherialNds getNds(int idItem) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from tvk_nds where id = ? order by date_begin desc",
                    (rs, i) -> getMatherialNds(rs),
                    idItem);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * save all {@link MatherialNds} for {@link Matherial}
     * @param matherial
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public Matherial saveNds(Matherial matherial) {

        deleteNds(matherial.getId());

        if (matherial.getNdsList() != null) {
            matherial.getNdsList().forEach(matherialNds -> {
                matherialNds.setId(0);
                saveNds(matherial, matherialNds);
            });
        }

        return matherial;
    }

    /**
     * save {@link MatherialNds}
     * @param matherialNds
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveNds(MatherialNds matherialNds) {
        return saveNds(null, matherialNds);
    }

    /**
     * @param matherialNds
     * @return
     */
    private int saveNds(Matherial matherial, MatherialNds matherialNds) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;

            if (matherialNds.getId() == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_nds (id_matherial, date_begin, value, code_object) " +
                                " values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update tvk_nds set id_matherial=?, date_begin=?, value=?, code_object=? " +
                        " where id = ?");
            }

            preparedStatement.setInt(1,  matherial == null ? matherialNds.getIdMatherial() : matherial.getId());
            preparedStatement.setTimestamp(2, new Timestamp(UtilDate.clearHHmmssSSS(matherialNds.getDate()).getTime()));
            preparedStatement.setInt(3, matherialNds.getValue());
            preparedStatement.setString(4, matherial == null ? matherialNds.getCodeMatherial() : matherial.getCode());

            if (matherialNds.getId() != 0) {
                preparedStatement.setInt(5, matherialNds.getId());
            }

            return preparedStatement;
        }, keyHolder);

        if (matherialNds.getId() == 0) {
            matherialNds.setId((int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0));
        }

        return matherialNds.getId();
    }

    /**
     * delete {@link MatherialNds}
     * @param id tvk_nds.id
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteNdsItem(int id) {
        return jdbcTemplate.update(
                "delete from tvk_nds where id = ?",
                ps -> ps.setInt(1, id)
        ) != 0;
    }

    /**
     * delete all {@link MatherialNds} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteNds(int idMatherial) {
        return jdbcTemplate.update("delete from tvk_nds where id_matherial = ?",
                ps -> ps.setInt(1, idMatherial)
        ) != 0;
    }

}