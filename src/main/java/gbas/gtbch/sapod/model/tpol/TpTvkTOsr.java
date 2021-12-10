package gbas.gtbch.sapod.model.tpol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.tvk.tpol3.TvkTOsr;

@JsonIgnoreProperties(value = {"id_group_kont", "id_grpk", "id_group_ts"})
public class TpTvkTOsr extends TvkTOsr {
}
