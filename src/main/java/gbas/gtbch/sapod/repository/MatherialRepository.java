package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.matherials.Matherial;
import gbas.gtbch.sapod.model.matherials.MatherialListItem;
import gbas.gtbch.sapod.model.matherials.Measure;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.objects.service.SborDescriptor;
import gbas.tvk.service.db.DbHelper;
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
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatherialRepository {
    public static final Logger logger = LoggerFactory.getLogger(MatherialRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final DbHelper dbHelper;

    private final MatherialKoefRepository matherialKoefRepository;

    private final MatherialPriceRepository matherialPriceRepository;

    private final MatherialNdsRepository matherialNdsRepository;

    @Autowired
    public MatherialRepository(@Qualifier("sapodDataSource") DataSource dataSource, DbHelper dbHelper, MatherialKoefRepository matherialKoefRepository, MatherialPriceRepository matherialPriceRepository, MatherialNdsRepository matherialNdsRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dbHelper = dbHelper;
        this.matherialKoefRepository = matherialKoefRepository;
        this.matherialPriceRepository = matherialPriceRepository;
        this.matherialNdsRepository = matherialNdsRepository;
    }

    /**
     *
     * @return
     */
    public List<MatherialListItem> getMatherialListItems() {
        return getMatherialListItems(null, null, false);
    }

    /**
     *
     * @param dateBegin
     * @param dateEnd
     * @param all
     * @return
     */
    public List<MatherialListItem> getMatherialListItems(Date dateBegin, Date dateEnd, boolean all) {
        List<Object> args = new ArrayList<Object>();

        String sql = "select\n" +
                " m.matherial as id,\n" +
                " m.code,\n" +
                " m.code_ekisufr,\n" +
                " m.name,\n" +
                " m.descriptor,\n" +
                " mea1.name as mea1_name,\n" +
                " mea2.name as mea2_name,\n" +
                " m.code_group,\n" +
                " (" + dbHelper.selectTopRows("select cena_1 from price2 where price2.matherial = m.matherial order by dat_home desc ", 1) + ") as rate,\n" +
                " (select short_name from currency where code = (" + dbHelper.selectTopRows("select code from price2 where price2.matherial = m.matherial order by dat_home desc", 1) + ")) as currency,\n" +
                " (" + dbHelper.selectTopRows("select koef from tvk_kof_sbor where tvk_kof_sbor.id_object = m.matherial order by date_begin desc", 1) + ") as koef,\n" +
                " (" + dbHelper.selectTopRows("select value from tvk_nds where tvk_nds.id_matherial = m.matherial order by date_begin desc", 1) + ") as nds\n" +
                "from matherial m\n" +
                "left outer join measure mea1 on mea1.measure = m.measure\n" +
                "left outer join measure mea2 on mea2.measure = m.measure_right\n";

//        if (dateBegin != null && dateEnd != null) {
//            sql += "(date_b >= ? AND date_e <= ?) AND\n";
//            args.add(dateBegin);
//            args.add(dateEnd);
//        } else if (dateBegin != null) {
//            sql += "date_b >= ? AND\n";
//            args.add(dateBegin);
//        } else if (dateEnd != null) {
//            sql += "date_e <= ? AND\n";
//            args.add(dateEnd);
//        }

        if (!all) {
            sql += "and m.is_delete != 1\n";
        }

        sql += "order by code_ekisufr, code";

        List<MatherialListItem> rows = jdbcTemplate.query(sql, (rs, i) -> {
            final MatherialListItem matherial = new MatherialListItem();

            matherial.setId(rs.getInt("id"));
            matherial.setCode(Func.iif(rs.getString("code")));
            matherial.setCodeEkisufr(Func.iif(rs.getString("code_ekisufr")));
            matherial.setName(Func.iif(rs.getString("name")));
            SborDescriptor sborDescriptor = SborDescriptor.getDescriptor(rs.getInt("descriptor"));
            if (sborDescriptor != null) {
                matherial.setDescriptorName(sborDescriptor.getName());
            }
            matherial.setMeasureLeftName(Func.iif(rs.getString("mea1_name")));
            matherial.setMeasureRightName(Func.iif(rs.getString("mea2_name")));
            matherial.setCodeGroup(Func.iif(rs.getString("code_group")));
            matherial.setServiceRate(rs.getDouble("rate"));
            matherial.setServiceCurrency(Func.iif(rs.getString("currency")));
            matherial.setServiceKoef(rs.getDouble("koef"));
            matherial.setServiceNds(rs.getInt("nds"));

            return matherial;
        }, args.toArray(new Object[0]));

        return rows.stream()
                .filter(row -> !"300".equals(row.getCode()) && !"301".equals(row.getCode())) // remove "300" and "301" from list
                .collect(Collectors.toList());
    }

    /**
     * get {@link Matherial} code
     * @param id
     * @return
     */
    public String getCodeMatherial(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select code from matherial where matherial = ?",
                    (resultSet, i) -> Func.iif(resultSet.getString("code")),
                    id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * get {@link Matherial}
     * @param id
     * @return
     */
    public Matherial getMatherial(int id) {
        String sql = "select\n" +
                " m.matherial as id,\n" +
                " m.code,\n" +
                " m.code_ekisufr,\n" +
                " m.name,\n" +
                " m.descriptor,\n" +
                " mea1.measure as mea1_id,\n" +
                " mea1.name as mea1_name,\n" +
                " mea2.measure as mea2_id,\n" +
                " mea2.name as mea2_name,\n" +
                " m.osob_name,\n" +
                " m.osob_val,\n" +
                " m.code_group\n" +
                "from matherial m\n" +
                "left outer join measure mea1 on mea1.measure = m.measure\n" +
                "left outer join measure mea2 on mea2.measure = m.measure_right\n" +
                "where m.matherial = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, i) -> {
                final Matherial matherial = new Matherial();
                matherial.setId(rs.getInt("id"));
                matherial.setCode(Func.iif(rs.getString("code")));
                matherial.setCodeEkisufr(Func.iif(rs.getString("code_ekisufr")));
                matherial.setName(Func.iif(rs.getString("name")));
                matherial.setDescriptor(rs.getInt("descriptor"));
                matherial.setMeasureLeft(new Measure(rs.getInt("mea1_id"), Func.iif(rs.getString("mea1_name"))));
                matherial.setMeasureRight(new Measure(rs.getInt("mea2_id"), Func.iif(rs.getString("mea2_name"))));
                matherial.setOsobName(Func.iif(rs.getString("osob_name")));
                matherial.setOsobVal(rs.getInt("osob_val"));
                matherial.setCodeGroup(Func.iif(rs.getString("code_group")));
                return matherial;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * save matherial
     * @param matherial
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveMatherial(Matherial matherial) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;

            if (matherial.getId() == 0) {
                preparedStatement = connection.prepareStatement("insert into matherial (\n" +
                                " code, code_ekisufr, name, descriptor, measure,\n" +
                                " measure_name_left, measure_right, measure_name_right, code_group, osob_name,\n" +
                                " osob_val)\n" +
                                " values\n" +
                                " (?, ?, ?, ?, ?,\n" +
                                "  ?, ?, ?, ?, ?,\n" +
                                "  ?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update matherial set\n" +
                        " code=?, code_ekisufr=?, name=?, descriptor=?, measure=?,\n" +
                        " measure_name_left=?, measure_right=?, measure_name_right=?, code_group=?, osob_name=?,\n" +
                        " osob_val=?\n" +
                        " where matherial = ?");
            }

            preparedStatement.setString(1, matherial.getCode());
            preparedStatement.setString(2, matherial.getCodeEkisufr());
            preparedStatement.setString(3, matherial.getName());
            preparedStatement.setInt(4, matherial.getDescriptor());
            if (matherial.getMeasureLeft() != null) {
                preparedStatement.setInt(5, matherial.getMeasureLeft().getId());
                preparedStatement.setString(6, matherial.getMeasureLeft().getName());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            if (matherial.getMeasureRight() != null) {
                preparedStatement.setInt(7, matherial.getMeasureRight().getId());
                preparedStatement.setString(8, matherial.getMeasureRight().getName());
            } else {
                preparedStatement.setNull(7, Types.INTEGER);
                preparedStatement.setNull(8, Types.VARCHAR);
            }
            preparedStatement.setString(9, Func.isEmpty(matherial.getCodeGroup()) ? "F34" : matherial.getCodeGroup());
            preparedStatement.setString(10, matherial.getOsobName());
            preparedStatement.setInt(11, matherial.getOsobVal());

            if (matherial.getId() != 0) {
                preparedStatement.setInt(12, matherial.getId());
            }

            return preparedStatement;
        }, keyHolder);

        if (matherial.getId() == 0) {
            matherial.setId((int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0));
        }

        return matherial.getId();
    }

    /**
     * delete {@link Matherial} with dependencies
     * @param id
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteMatherial(int id) {
        matherialPriceRepository.deletePrice(id);
        matherialKoefRepository.deleteKoef(id);
        matherialNdsRepository.deleteNds(id);
        return jdbcTemplate.update(
                "delete from matherial where matherial = ?",
                ps -> ps.setInt(1, id)
        ) != 0;
    }
}