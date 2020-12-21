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
import java.util.*;

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
     * @param params
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public List<CalculationLog> getList(Map<String, String> params, Date dateBegin, Date dateEnd) {
        List<Object> args = new ArrayList<>();

        String sql = "";
        if (dateBegin != null) {
            sql += addToSql(sql, "inbound_time >= ?");
            args.add(dateBegin);
        }

        if (dateEnd != null) {
            sql += addToSql(sql, "inbound_time <= ?");
            Calendar c = Calendar.getInstance();
            c.setTime(dateEnd);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);
            args.add(c.getTime());
        }

        if (params != null) {
            if (params.get("source") != null) {
                sql += addToSql(sql, "sourcetype = ?");
                args.add(params.get("source"));
            }
            if (params.get("type") != null) {
                sql += addToSql(sql, "doctype = ?");
                args.add(params.get("type"));
            }
            if (params.get("number") != null) {
                sql += addToSql(sql, "docnumber = ?");
                args.add(params.get("number"));
            }
            if (params.get("station") != null) {
                sql += addToSql(sql, "station = ?");
                args.add(params.get("station"));
            }
        }

        sql = "select id, sourcetype, doctype, docnumber, station, inbound_time, outbound_time, error_code\n" +
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
            calculationLog.setOutboundTime(rs.getTimestamp("outbound_time"));
            calculationLog.setErrorCode(rs.getInt("error_code"));

            return calculationLog;
        });
    }

}
