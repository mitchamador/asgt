package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.TpolItem;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TPolItemsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TPolItemsRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolItemsRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    static List<String[]> extractStringArrayList(ResultSet rs) throws SQLException {
        List<String[]> list = new ArrayList<>();

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

    public List<String[]> getNsi(TpolItem item) {
        return jdbcTemplate.query(item.getItem().getSqlFromNSI(item.getSet()), TPolItemsRepository::extractStringArrayList);
    }

    public List<String[]> getData(TpolItem item, int id_tpol) {
        return jdbcTemplate.query(item.getItem().getSqlSelected(id_tpol), TPolItemsRepository::extractStringArrayList);
    }

    /**
     * check data for existing
     *
     * @param item {@link TpolItem}
     * @param id   {@link TPRow} id
     * @param data data
     * @return
     */
    public Boolean checkData(TpolItem item, int id, String[] data) {
        return jdbcTemplate.query(item.getItem().getSqlCheckExist(id, data, 0), ResultSet::next);
    }

    /**
     * add data
     *
     * @param item {@link TpolItem}
     * @param id   {@link TPRow} id
     * @param data data
     * @return
     */
    public int addData(TpolItem item, int id, String[] data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> connection.prepareStatement(item.getItem().getSqlAddSelected(id, data, 0)), keyHolder);

        return (int) keyHolder.getKey();
    }

    /**
     * delete data
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    public Boolean deleteData(TpolItem item, int id, String data) {
        return jdbcTemplate.update(item.getItem().getSqlDelSelected(id, data, 0)) != 0;
    }

}
