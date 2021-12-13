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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
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

    /**
     * get all {@link MatherialPrice} for {@link Matherial}
     * @param idMatherial
     * @return
     */
    public List<MatherialPrice> getPriceList(int idMatherial) {
        Map<Integer, Currency> currencyMap = new HashMap<>();

        return jdbcTemplate.query("select codp as id, matherial as id_matherial, code_object, dat_home, cena_1, code as id_currency from price2 where matherial = ? order by dat_home desc", (rs, i) -> {
            MatherialPrice matherialPrice = new MatherialPrice();
            matherialPrice.setId(rs.getInt("id"));
            matherialPrice.setIdMatherial(rs.getInt("id_matherial"));
            matherialPrice.setCodeMatherial(Func.iif(rs.getString("code_object")));
            matherialPrice.setDate(rs.getDate("dat_home"));
            matherialPrice.setRate(rs.getDouble("cena_1"));
            matherialPrice.setCurrency(getCurrency(currencyMap, rs.getInt("id_currency")));
            return matherialPrice;
        }, idMatherial);
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
                preparedStatement = connection.prepareStatement("insert into price2 (matherial, dat_home, cena_1, code, code_object, tarif) " +
                                " values (?, ?, ?, ?, ?, 0)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update price2 set matherial=?, dat_home=?, cena_1=?, code=?, code_object=?, tarif=0 " +
                        " where codp = ?");
            }

            preparedStatement.setInt(1, matherial == null ? matherialPrice.getIdMatherial() : matherial.getId());
            preparedStatement.setTimestamp(2, new Timestamp(UtilDate.clearHHmmssSSS(matherialPrice.getDate()).getTime()));
            preparedStatement.setDouble(3, matherialPrice.getRate());
            if (matherialPrice.getCurrency() != null) {
                preparedStatement.setInt(4, matherialPrice.getCurrency().getId());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            preparedStatement.setString(5, matherial == null ? matherialPrice.getCodeMatherial() : matherial.getCode());

            if (matherialPrice.getId() != 0) {
                preparedStatement.setInt(6, matherialPrice.getId());
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