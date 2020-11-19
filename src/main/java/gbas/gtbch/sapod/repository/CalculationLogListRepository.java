package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CalculationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Component
public class CalculationLogListRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationLogListRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CalculationLogListRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private String addToSql(String sql, String command) {
        return (sql.isEmpty() ? "WHERE " : " AND ") + command + "\n";
    }

    /**
     * get list
     * @param filter
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public List<CalculationLog> getList(CalculationLog filter, Date dateBegin, Date dateEnd) {
        List<Object> args = new ArrayList<>();

        String sql = "";
        if (dateBegin != null) {
            sql += addToSql(sql, "inbound_time >= ?");
            args.add(dateBegin);
        }

        if (dateEnd != null) {
            sql += addToSql(sql, "inbound_time <= ?");
            args.add(dateEnd);
        }

        if (filter != null) {
            if (filter.getSource() != null) {
                sql += addToSql(sql, "sourcetype = ?");
                args.add(filter.getSource().toString());
            }
            if (filter.getType() != null) {
                sql += addToSql(sql, "doctype = ?");
                args.add(filter.getType().toString());
            }
            if (filter.getNumber() != null) {
                sql += addToSql(sql, "docnumber = ?");
                args.add(filter.getNumber());
            }
            if (filter.getStation() != null) {
                sql += addToSql(sql, "station = ?");
                args.add(filter.getStation());
            }
        }

        sql = "select id, sourcetype, doctype, docnumber, station, inbound_time\n" +
                "from calculation_log\n"
                + sql;

        sql += "ORDER BY inbound_time";

        return jdbcTemplate.query(sql, args.toArray(), (rs, i) -> {
            CalculationLog calculationLog = new CalculationLog();

            calculationLog.setId(rs.getInt("id"));
            calculationLog.setSource(CalculationLog.Source.getSource(rs.getString("sourcetype")));
            calculationLog.setType(CalculationLog.Type.getType(rs.getString("doctype")));
            calculationLog.setNumber(rs.getString("docnumber"));
            calculationLog.setStation(rs.getString("station"));
            calculationLog.setInboundTime(rs.getTimestamp("inbound_time"));

            return calculationLog;
        });
    }

}
