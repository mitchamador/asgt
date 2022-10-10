package gbas.gtbch.util;

import java.util.List;

public class JdbcTemplateUtils {

    /**
     * Add WHERE/AND to sqlString based on args size
     * @param args
     * @param sqlString
     * @return
     */
    public static String getSqlString(List<Object> args, String sqlString) {
        if (args.isEmpty()) {
            return "WHERE\n " + sqlString + "\n";
        } else {
            return " AND " + sqlString + "\n";
        }
    }
}
