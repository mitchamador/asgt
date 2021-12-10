package gbas.gtbch.sapod.model.tpol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.tvk.tpol3.TvkTVes;

@JsonIgnoreProperties(value = {"id_group_t_ves"})
public class TpTvkTVes extends TvkTVes {
}
