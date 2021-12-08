package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.tpol.TpItem;
import gbas.gtbch.sapod.model.tpol.TpRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TPolItemsRepository {
    public static final Logger logger = LoggerFactory.getLogger(TPolItemsRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public TPolItemsRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public static List<String[]> extractStringArrayList(ResultSet rs) throws SQLException {
        List<String[]> list = new ArrayList<String[]>();

        final ResultSetMetaData data = rs.getMetaData();
        final int col = data.getColumnCount();
        while (rs.next()) {
            final String[] s = new String[col];
            for (int i = 0; i < col; i++) {
                s[i] = rs.getString(i + 1);
                if (s[i] != null) {
                    s[i] = s[i].trim();
                }
            }
            list.add(s);
        }

        return list;
    }

    public List<String[]> getNsi(TpItem item) {
        String sql = item.getItem().getSqlFromNSI(item.getSet());
        if (sql != null) {
            return jdbcTemplate.query(sql, TPolItemsRepository::extractStringArrayList);
        } else {
            return new ArrayList<String[]>();
        }
    }

    public List<String[]> getData(TpItem item, int id_tpol) {
        return jdbcTemplate.query(item.getItem().getSqlSelected(id_tpol), TPolItemsRepository::extractStringArrayList);
    }

    /**
     * check data for existing
     *
     * @param item {@link TpItem}
     * @param id   {@link TpRow} id
     * @param data data
     * @return true if data exists
     */
    public Boolean checkData(TpItem item, int id, String[] data) {
        String sql = item.getItem().getSqlCheckExist(id, data, item.getSet());
        if (sql != null) {
            return jdbcTemplate.query(sql, ResultSet::next);
        } else {
            return false;
        }
    }

    /**
     * add data
     *
     * @param item {@link TpItem}
     * @param id   {@link TpRow} id
     * @param data data
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean addData(TpItem item, int id, String[] data) {
        return jdbcTemplate.update(connection -> connection.prepareStatement(
                item.getItem().getSqlAddSelected(id, data, item.getSet()))) != 0;
    }

    /**
     * delete data
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public Boolean deleteData(TpItem item, int id, String data) {
        return jdbcTemplate.update(item.getItem().getSqlDelSelected(id, data, item.getSet())) != 0;
    }
}