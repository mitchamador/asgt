package gbas.gtbch.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static gbas.gtbch.util.JdbcTemplateUtils.SqlServerType.DB2;
import static gbas.gtbch.util.JdbcTemplateUtils.SqlServerType.SQL_SERVER;

@Component
public class JdbcTemplateUtils {

    public enum SqlServerType {
        DB2("DB2"),
        SQL_SERVER("SqlServer"),
        ;

        private String type;

        SqlServerType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private static SqlServerType dbType = null;

    private final DataSource sapodDataSource;

    public JdbcTemplateUtils(@Qualifier("sapodDataSource") DataSource sapodDataSource) {
        this.sapodDataSource = sapodDataSource;
    }

    public SqlServerType getDbType() {
        if (dbType == null) {
            try (Connection cSapod = sapodDataSource.getConnection()) {
                dbType = cSapod.getMetaData().getURL().startsWith("jdbc:db2") ? DB2 : SQL_SERVER;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return dbType;
    }

    /**
     * Add WHERE/AND to sqlString based on args size
     * @param args
     * @param sqlString
     * @return
     */
    public static String getSqlString(List<Object> args, String sqlString) {
        String tClauseOperator;
        if (args.isEmpty()) {
            tClauseOperator = "WHERE\n";
        } else {
            tClauseOperator = " AND ";
        }
        return tClauseOperator + sqlString + "\n";
    }
}
