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
    private String[] nsiData;

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

    public String[] getNsiData() {
        return nsiData;
    }

    public void setNsiData(String[] nsiData) {
        this.nsiData = nsiData;
    }

    /**
     * todo move to sapod side
     * @param itemClassName
     */
    private static String getSqlFilter(String itemClassName, int set) throws IllegalArgumentException {
        String sql = "";
        if (itemClassName != null) {
            switch (itemClassName) {
                case "VidSoob":
                    sql = "EXISTS (SELECT * FROM tvk_t_vs WHERE tvk_t_vs.id_t_pol = tvk_t_pol.id AND tvk_t_vs.code_vid_s = ?)";
                    break;
                case "CountryCalculationItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_sr WHERE tvk_t_sr.id_t_pol = tvk_t_pol.id AND tvk_t_sr.code_str_r = ?)";
                    break;
                case "CountrySourceItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_so WHERE tvk_t_so.id_t_pol = tvk_t_pol.id AND tvk_t_so.code_str_o = ?)";
                    break;
                case "CountryTargetItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_sn WHERE tvk_t_sn.id_t_pol = tvk_t_pol.id AND tvk_t_sn.code_str_n = ?)";
                    break;
                case "VhStikItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_vh WHERE tvk_t_vh.id_t_pol = tvk_t_pol.id AND tvk_t_vh.code_stan = ?)";
                    break;
                case "VhStikItem1":
                    sql = "EXISTS (SELECT * FROM tvk_t_vh1 WHERE tvk_t_vh1.id_t_pol = tvk_t_pol.id AND tvk_t_vh1.code_stan = ?)";
                    break;
                case "VidOtpr":
                    sql = "EXISTS (SELECT * FROM tvk_t_vot WHERE tvk_t_vot.id_t_pol = tvk_t_pol.id AND tvk_t_vot.code_svo = ?)";
                    break;
                case "TipPodS":
                    sql = "EXISTS (SELECT * FROM tvk_t_rps WHERE tvk_t_rps.id_t_pol = tvk_t_pol.id AND tvk_t_rps.code_rod_ps = ?)";
                    break;
                case "TipCont":
                    sql = "EXISTS (SELECT * FROM tvk_t_kon WHERE tvk_t_kon.id_t_pol = tvk_t_pol.id AND tvk_t_kon.code_tip_kon = ?)";
                    break;
                case "Cargo":
                    if (set == 0) {
                        sql = "EXISTS (SELECT * FROM tvk_t_gr WHERE tvk_t_gr.id_t_pol = tvk_t_pol.id AND tvk_t_gr.code_algng = ?)";
                    } else {
                        sql = "EXISTS (SELECT * FROM tvk_t_gr WHERE tvk_t_gr.id_t_pol = tvk_t_pol.id AND tvk_t_gr.code_etsng = ?)";
                    }
                    break;
                case "ClassItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_kl WHERE tvk_t_kl.id_t_pol = tvk_t_pol.id AND tvk_t_kl.klass = ?)";
                    break;
                case "VyhStikItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_vyh WHERE tvk_t_vyh.id_t_pol = tvk_t_pol.id AND tvk_t_vyh.code_stan = ?)";
                    break;
                case "VyhStikItem1":
                    sql = "EXISTS (SELECT * FROM tvk_t_vyh1 WHERE tvk_t_vyh1.id_t_pol = tvk_t_pol.id AND tvk_t_vyh1.code_stan = ?)";
                    break;
                case "OsOtItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_oso WHERE tvk_t_oso.id_t_pol = tvk_t_pol.id AND tvk_t_oso.code_os_ot = ?)";
                    break;
                case "PrinVagItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_v WHERE tvk_t_v.id_t_pol = tvk_t_pol.id AND tvk_t_v.code_prv_k = ?)";
                    break;
                case "PrinContItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_k WHERE tvk_t_k.id_t_pol = tvk_t_pol.id AND tvk_t_k.code_prv_k = ?)";
                    break;
                case "GrpContItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_grpk WHERE tvk_t_grpk.id_t_pol = tvk_t_pol.id AND tvk_t_grpk.grpk = ?)";
                    break;
                case "StationTargetItem":
                    sql = "EXISTS (SELECT * FROM tvk_stan_n WHERE tvk_stan_n.id_t_pol = tvk_t_pol.id AND tvk_stan_n.code_stan = ?)";
                    break;
                case "StationSourceItem":
                    sql = "EXISTS (SELECT * FROM tvk_stan_o WHERE tvk_stan_o.id_t_pol = tvk_t_pol.id AND tvk_stan_o.code_stan = ?)";
                    break;
                case "RailOwnerItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_adm WHERE tvk_t_adm.id_t_pol = tvk_t_pol.id AND tvk_t_adm.code_adm = ?)";
                    break;
                case "RailTargetItem":
                    sql = "EXISTS (SELECT * FROM tvk_rail_n WHERE tvk_rail_n.id_t_pol = tvk_t_pol.id AND tvk_rail_n.code_pr = ?)";
                    break;
                case "RailSourceItem":
                    sql = "EXISTS (SELECT * FROM tvk_rail_o WHERE tvk_rail_o.id_t_pol = tvk_t_pol.id AND tvk_rail_o.code_pr = ?)";
                    break;
                case "PackingItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_upak WHERE tvk_t_upak.id_t_pol = tvk_t_pol.id AND tvk_t_upak.code_rod_u = ?)";
                    break;
                case "ContTrainItem":
                    sql = "EXISTS (SELECT * FROM tvk_t_conttrain WHERE tvk_t_conttrain.id_t_pol = tvk_t_pol.id AND tvk_t_conttrain.kod = ?)";
                    break;
                case "OsOtItemAdd":
                    sql = "EXISTS (SELECT * FROM tvk_t_oso_add WHERE tvk_t_oso_add.id_t_pol = tvk_t_pol.id AND tvk_t_oso_add.code_os_ot = ?)";
                    break;
                case "Distance":
                    sql = "EXISTS (SELECT * FROM tvk_t_km WHERE tvk_t_km.id_t_pol = tvk_t_pol.id AND ? BETWEEN tvk_t_km.km AND tvk_t_km.km2)";
                    break;
                case "CargoOLD":
                    sql = "EXISTS (SELECT * FROM tvk_t_grold WHERE tvk_t_grold.id_t_pol = tvk_t_pol.id AND tvk_t_grold.code_etsng = ?)";
                    break;
            }
        }
        if (sql.isEmpty()) {
            throw new IllegalArgumentException("Empty sql for filter item.");
        }
        return sql;
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
                            sql.append(getSqlString(args, getSqlFilter(item.getName(), set)));
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
