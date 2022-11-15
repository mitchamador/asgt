package gbas.gtbch.sapod.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import gbas.tvk.util.GZipUtils;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;

@Entity
@Table(name = "calculation_log_data")
public class CalculationLogData {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_calculation_log", nullable = false)
    @JsonBackReference
    private CalculationLog calculationLog;

    /**
     * gzipped inbound xml object
     */
    @Column(name = "inbound_xml_gz")
    private byte[] inboundXml;

    /**
     * gzipped outbound xml object
     */
    @Column(name = "outbound_xml_gz")
    private byte[] outboundXml;

    /**
     * gzipped outbound text object
     */
    @Column(name = "outbound_text_gz")
    private byte[] outboundText;

    public CalculationLogData() {
    }

    public CalculationLogData(CalculationLog calculationLog) {
        this.calculationLog = calculationLog;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInboundXml() {
        try {
            return GZipUtils.gZippedBytes2String(inboundXml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setInboundXml(String inboundXml) {
        try {
            this.inboundXml = GZipUtils.string2GZippedBytes(inboundXml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getOutboundXml() {
        try {
            return GZipUtils.gZippedBytes2String(outboundXml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOutboundXml(String outboundXml) {
        try {
            this.outboundXml = GZipUtils.string2GZippedBytes(outboundXml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getOutboundText() {
        try {
            return GZipUtils.gZippedBytes2String(outboundText);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOutboundText(String outboundText) {
        try {
            this.outboundText = GZipUtils.string2GZippedBytes(outboundText);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public CalculationLog getCalculationLog() {
        return calculationLog;
    }

    public void setCalculationLog(CalculationLog calculationLog) {
        this.calculationLog = calculationLog;
    }
}
