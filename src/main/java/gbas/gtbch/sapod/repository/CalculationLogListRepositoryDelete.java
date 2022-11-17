package gbas.gtbch.sapod.repository;

import gbas.gtbch.util.JdbcTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gbas.gtbch.util.JdbcTemplateUtils.SqlServerType.DB2;

@Component
public class CalculationLogListRepositoryDelete {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationLogListRepositoryDelete.class);

    private final JdbcTemplate jdbcTemplate;

    private final JdbcTemplateUtils jdbcTemplateUtils;

    public CalculationLogListRepositoryDelete(JdbcTemplate jdbcTemplate, JdbcTemplateUtils jdbcTemplateUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcTemplateUtils = jdbcTemplateUtils;
    }

    /**
     * delete rows from calculation_log with inbound_time until dateToNakl (for NAKL doctype) and dateToOther (for other doctypes)
     * @param dateToNakl
     * @param dateToOther
     * @param maxRows
     * @return
     */
    public int deleteRows(LocalDate dateToNakl, LocalDate dateToOther, int maxRows) {
        String cmd = null;

        if (jdbcTemplateUtils.getDbType() == DB2) {
            cmd = "DELETE FROM \n" +
                    "(\n" +
                    " SELECT *" +
                    " FROM GTMAIN.CALCULATION_LOG\n" +
                    " WHERE" +
                    "  (INBOUND_TIME < ? AND DOCTYPE = 'NAKL')\n" +
                    " OR\n" +
                    "  (INBOUND_TIME < ? AND DOCTYPE != 'NAKL')\n" +
                    (maxRows > 0 ? "FETCH FIRST " + maxRows + " ROW ONLY \n" : "") +
                    ")\n";
        }

        if (cmd != null) {
            List<Object> args = new ArrayList<>();
            args.add(Date.from(dateToNakl.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            args.add(Date.from(dateToOther.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            return jdbcTemplate.update(cmd, args.toArray());
        }

        return 0;
    }
}
