package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.matherials.Measure;
import gbas.tvk.nsi.cash.Func;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class MeasureRepository {
    public static final Logger logger = LoggerFactory.getLogger(MeasureRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MeasureRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

     /**
     * get list of {@link Measure}
     * @return
     */
    public List<Measure> getMeasures() {
        return jdbcTemplate.query("select measure as id, name, shortname, type from measure order by name",
                (rs, i) -> {
                    Measure measure = new Measure();
                    measure.setId(rs.getInt("id"));
                    measure.setName(Func.iif(rs.getString("name")));
                    measure.setShortName(Func.iif(rs.getString("shortname")));
                    measure.setType(rs.getShort("type"));
                    return measure;
                });
    }

}