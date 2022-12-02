package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.matherials.Matherial;
import gbas.gtbch.sapod.model.matherials.MatherialPrice;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MatherialPriceRepository {
    public static final Logger logger = LoggerFactory.getLogger(MatherialPriceRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public MatherialPriceRepository(@Qualifier("sapodDataSource") DataSource dataSource, CurrencyRepository currencyRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currencyRepository = currencyRepository;
    }

    private MatherialPrice getMatherialPrice(Map<Integer, Currency> currencyMap, ResultSet rs) throws SQLException {
        MatherialPrice matherialPrice = new MatherialPrice();
        matherialPrice.setId(rs.getInt("id"));
        matherialPrice.setIdMatherial(rs.getInt("id_matherial"));
        matherialPrice.setCodeMatherial(Func.iif(rs.getString("code_object")));
        matherialPrice.setDate(rs.getDate("dat_home"));
        matherialPrice.setRate(rs.getDouble("cena_1"));
        matherialPrice.setCurrency(getCurrency(currencyMap, rs.getInt("id_currency")));
        matherialPrice.setNumNod(rs.getInt("num_nod"));
        return matherialPrice;
    }

    /**
     * get all {@link MatherialPrice} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    public List<MatherialPrice> getPriceList(int idMatherial) {
        Map<Integer, Currency> currencyMap = new HashMap<>();

        return jdbcTemplate.query(
                "select codp as id, matherial as id_matherial, code_object, dat_home, cena_1, code as id_currency, num_nod from price2 where matherial = ? order by dat_home desc",
                (rs, i) -> getMatherialPrice(currencyMap, rs),
                idMatherial);
    }

    /**
     * get {@link MatherialPrice}
     * @param idItem
     * @return
     */
    public MatherialPrice getPrice(int idItem) {
        Map<Integer, Currency> currencyMap = new HashMap<>();

        try {
            return jdbcTemplate.queryForObject(
                    "select codp as id, matherial as id_matherial, code_object, dat_home, cena_1, code as id_currency, num_nod from price2 where codp = ? order by dat_home desc",
                    (rs, i) -> getMatherialPrice(currencyMap, rs),
                    idItem);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * get cached {@link Currency}
     * @param currencyMap
     * @param code
     * @return
     */
    private Currency getCurrency(Map<Integer, Currency> currencyMap, Integer code) {
        Currency c;
        if (!currencyMap.containsKey(code)) {
            currencyMap.put(code, c = currencyRepository.findById(code).orElse(null));
        } else {
            c = currencyMap.get(code);
        }
        return c;
    }

    @Transactional(transactionManager = "sapodTransactionManager")
    public Matherial savePrice(Matherial matherial) {

        deletePrice(matherial.getId());

        if (matherial.getPriceList() != null) {
            matherial.getPriceList().forEach(matherialPrice -> {
                matherialPrice.setId(0);
                savePrice(matherial, matherialPrice);
            });
        }

        return matherial;
    }

    /**
     *
     * @param matherialPrice
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int savePrice(MatherialPrice matherialPrice) {
        return savePrice(null, matherialPrice);
    }

    /**
     * @param matherialPrice
     * @return
     */
    private int savePrice(Matherial matherial, MatherialPrice matherialPrice) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;

            if (matherialPrice.getId() == 0) {
                preparedStatement = connection.prepareStatement("insert into price2 (matherial, dat_home, cena_1, code, code_object, num_nod, tarif) " +
                                " values (?, ?, ?, ?, ?, ?, 0)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update price2 set matherial=?, dat_home=?, cena_1=?, code=?, code_object=?, num_nod=?, tarif=0 " +
                        " where codp = ?");
            }

            int idMatherial = matherial == null ? matherialPrice.getIdMatherial() : matherial.getId();
            if (idMatherial != 0) {
                preparedStatement.setInt(1, idMatherial);
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            preparedStatement.setTimestamp(2, new Timestamp(UtilDate.clearHHmmssSSS(matherialPrice.getDate()).getTime()));
            preparedStatement.setDouble(3, matherialPrice.getRate());
            if (matherialPrice.getCurrency() != null) {
                preparedStatement.setInt(4, matherialPrice.getCurrency().getId());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            preparedStatement.setString(5, matherial == null ? matherialPrice.getCodeMatherial() : matherial.getCode());

            preparedStatement.setInt(6, matherialPrice.getNumNod());

            if (matherialPrice.getId() != 0) {
                preparedStatement.setInt(7, matherialPrice.getId());
            }

            return preparedStatement;
        }, keyHolder);

        if (matherialPrice.getId() == 0) {
            matherialPrice.setId((int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0));
        }

        return matherialPrice.getId();
    }

    /**
     * delete {@link MatherialPrice}
     * @param id price.codp
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deletePriceItem(int id) {
        return jdbcTemplate.update(
                "delete from price2 where codp = ?",
                        ps -> ps.setInt(1, id)
        ) != 0;
    }

    /**
     * delete all {@link MatherialPrice} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deletePrice(int idMatherial) {
        return jdbcTemplate.update(
                "delete from price2 where matherial = ?",
                ps -> ps.setInt(1, idMatherial)
        ) != 0;
    }

}