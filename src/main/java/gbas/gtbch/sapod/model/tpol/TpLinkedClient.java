package gbas.gtbch.sapod.model.tpol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * client linked to tvk_tarif document
 * tvk_tarif_client
 */
@JsonIgnoreProperties(value = {"idTarif"})
public class TpLinkedClient extends TpClient {
    /**
     * tvk_tarif.id
     */
    private int idTarif;

    public int getIdTarif() {
        return idTarif;
    }

    public void setIdTarif(int idTarif) {
        this.idTarif = idTarif;
    }

    @Override
    public String toString() {
        return "TpLinkedClient{" +
                "idTarif=" + idTarif +
                ", code='" + getCode() +
                ", name='" + getName() +
                ", numNod=" + getNumNod() +
                '}';
    }
}
