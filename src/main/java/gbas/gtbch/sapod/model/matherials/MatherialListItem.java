package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.tvk.nsi.cash.Func;

import java.text.DecimalFormat;

@JsonIgnoreProperties(value = {"priceList", "koefList", "ndsList", "measureLeft", "measureRight", "measureLeftName", "measureRightName", "serviceRate", "serviceCurrency", "descriptor", "codeGroup", "nDoc", "dateBegin", "dateEnd", "osobName", "osobVal"})
public class MatherialListItem extends Matherial {

    /**
     *
     */
    private String descriptorName;

    /**
     *
     */
    private String measureLeftName;

    /**
     *
     */
    private String measureRightName;

    /**
     *
     */
    private double serviceRate;

    /**
     *
     */
    private String serviceCurrency;

    /**
     *
     */
    private double serviceKoef;

    /**
     *
     */
    private int serviceNds;

    private DecimalFormat df = new DecimalFormat("###########0.##########");

    public String getDescriptorName() {
        return Func.iif(descriptorName);
    }

    public String getMeasureName() {
        String measureName = Func.iif(measureLeftName);
        if (!Func.isEmpty(measureRightName)) {
            measureName += (Func.isEmpty(measureName) ? "" : " * ") + Func.iif(measureRightName);
        }
        return measureName;
    }

    public String getRate() {
        String rate = serviceRate != 0 ? df.format(serviceRate) : "";
        if (!Func.isEmpty(serviceCurrency)) {
            rate += (rate.isEmpty() ? "0 " : " ") + Func.iif(serviceCurrency);
        }
        return rate;
    }

    /**
     *
     * @return
     */
    public String getMeasureLeftName() {
        return Func.iif(measureLeftName);
    }

    /**
     *
     * @return
     */
    public String getMeasureRightName() {
        return Func.iif(measureRightName);
    }

    /**
     *
     * @return
     */
    public String getServiceRate() {
        return serviceRate != 0 ? df.format(serviceRate) : "";
    }

    /**
     *
     * @return
     */
    public String getServiceCurrency() {
        return Func.iif(serviceCurrency);
    }

    /**
     *
     * @return
     */
    public String getServiceKoef() {
        return serviceKoef != 0 ? df.format(serviceKoef) : "";
    }

    /**
     *
     * @return
     */
    public String getServiceNds() {
        return serviceNds != 0 ? String.valueOf(serviceNds) : "";
    }

    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public void setMeasureRightName(String measureRightName) {
        this.measureRightName = measureRightName;
    }

    public void setMeasureLeftName(String measureLeftName) {
        this.measureLeftName = measureLeftName;
    }

    public void setServiceRate(double serviceRate) {
        this.serviceRate = serviceRate;
    }

    public void setServiceCurrency(String serviceCurrency) {
        this.serviceCurrency = serviceCurrency;
    }

    public void setServiceNds(int serviceNds) {
        this.serviceNds = serviceNds;
    }

    public void setServiceKoef(double serviceKoef) {
        this.serviceKoef = serviceKoef;
    }
}
