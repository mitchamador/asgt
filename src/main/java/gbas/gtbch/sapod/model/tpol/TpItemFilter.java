package gbas.gtbch.sapod.model.tpol;

import gbas.tvk.tpol3.service.TPItem;
import gbas.tvk.tpol3.service.TPItems;

import java.util.List;
import java.util.Map;

import static gbas.gtbch.util.JdbcTemplateUtils.getSqlString;

public class TpItemFilter {

    /**
     * filter's type
     */
    private String type;

    /**
     * filter's ui name
     */
    private String name;

    /**
     * filter's ui dropdown columns
     */
    private String[] nsiColumns;

    /**
     * filter's ui dropdown NSI
     */
    private List<String[]> nsiData;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getNsiColumns() {
        return nsiColumns;
    }

    public void setNsiColumns(String[] nsiColumns) {
        this.nsiColumns = nsiColumns;
    }

    public List<String[]> getNsiData() {
        return nsiData;
    }

    public void setNsiData(List<String[]> nsiData) {
        this.nsiData = nsiData;
    }

    /**
     * get sql string for filter map
     * @param args
     * @param filterMap
     * @return
     */
    public static String getFilterSqlString(List<Object> args, Map<String, String> filterMap) {
        StringBuilder sql = new StringBuilder();

        if (filterMap != null) {
            filterMap.keySet().forEach(key -> {
                String[] _key = key.split(":");
                TPItem item = TPItems.getTpItem(_key[0]);
                int set = 0;
                try {
                    set = _key.length > 1 ? Integer.parseInt(_key[1]) : 0;
                } catch (NumberFormatException ignored) {
                }

                if (item != null) {
                    String value = filterMap.get(key);
                    if (value != null && !value.trim().isEmpty()) {
                        try {
                            sql.append(getSqlString(args, item.getSqlFilterString(set)));
                            args.add(value.trim());
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            });
        }

        return sql.toString();
    }
}
