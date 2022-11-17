package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CalculationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

/**
 *
 */
@Component
public class CalculationLogListRepositoryJdbcTemplateImpl implements CalculationLogListRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationLogListRepositoryJdbcTemplateImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final CalculationLogListRepositoryDelete calculationLogListRepositoryDelete;

    @Autowired
    public CalculationLogListRepositoryJdbcTemplateImpl(@Qualifier("sapodDataSource") DataSource dataSource, CalculationLogListRepositoryDelete calculationLogListRepositoryDelete) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.calculationLogListRepositoryDelete = calculationLogListRepositoryDelete;
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

        sql = "select id, sourcetype, user as u, doctype, docnumber, station, inbound_time, outbound_time, error_code, jms_correlation_id\n" +
                "from calculation_log\n"
                + sql;

        sql += "ORDER BY inbound_time";

        return jdbcTemplate.query(sql, args.toArray(), (rs, i) -> {
            CalculationLog calculationLog = new CalculationLog();

            calculationLog.setId(rs.getInt("id"));
            calculationLog.setSource(CalculationLog.Source.getSource(rs.getString("sourcetype")));
            calculationLog.setUser(rs.getString("u"));
            calculationLog.setType(CalculationLog.Type.getType(rs.getString("doctype")));
            calculationLog.setNumber(rs.getString("docnumber"));
            calculationLog.setStation(rs.getString("station"));
            calculationLog.setInboundTime(rs.getTimestamp("inbound_time"));
            calculationLog.setOutboundTime(rs.getTimestamp("outbound_time"));
            calculationLog.setErrorCode(rs.getInt("error_code"));
            calculationLog.setJmsCorrelationId(rs.getString("jms_correlation_id"));

            return calculationLog;
        });
    }

    @Override
    public int deleteRows(LocalDate keepDateNakl, LocalDate keepDateOther, int batchSize) {
        return calculationLogListRepositoryDelete.deleteRows(keepDateNakl, keepDateOther, batchSize);
    }

}
