package gbas.gtbch.sapod.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import gbas.gtbch.util.JpaTruncator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "calculation_log")
public class CalculationLog {

    /**
     * source type (max size - 8 characters)
     */
    public enum Source {
        MQ("MQ"),
        REST("HTTP")
        ;

        private String name;

        Source(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Source getSource(String name) {
            if (name != null) {
                try {
                    return valueOf(name);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return null;
        }
    }
    /**
     * document type (max size - 8 chars)
     */
    public enum Type {
        GU46("Ведомость ГУ-46"),
        CARD("Накопительная карточка ФДУ-92"),
        NAKL("Накладная"),
        GU23("Акт ГУ-23"),
        VOT("Накладная САПОД"),
        UNKNOWN("Неизвестный документ"),
        ;

        /**
         * type name
         */
        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Type getType(String name) {
            if (name != null) {
                try {
                    return valueOf(name);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return null;
        }
    }

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * @see Source
     */
    @Column(name = "sourcetype")
    @Enumerated(EnumType.STRING)
    private Source source;

    /**
     * @see Type
     */
    @Column(name = "doctype")
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * document number
     */
    @Column(name = "docnumber", length = 20)
    private String number;

    /**
     * station code
     */
    @Column(name = "station", length = 6)
    private String station;

    /**
     * inbound time
     */
    @Column(name = "inbound_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date inboundTime;

    /**
     * inbound xml object
     */
    @Column(name = "inbound_xml")
    @Lob
    private String inboundXml;

    /**
     * outbound time
     */
    @Column(name = "outbound_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    private Date outboundTime;

    /**
     * outbound xml object
     */
    @Column(name = "outbound_xml")
    @Lob
    private String outboundXml;

    /**
     * outbound text object
     */
    @Column(name = "outbound_text")
    @Lob
    private String outboundText;

    /**
     * error
     */
    @Column(name = "error_code")
    private int errorCode;

    @Column(name = "jms_correlation_id", length = 36)
    private String jmsCorrelationId;

    /**
     * string for calc duration
     */
    @Transient
    private String durationText;

    /**
     * filename
     */
    @Transient
    private String fileName;

    public CalculationLog() {
    }

    /**
     * create calculation log
     * @param source
     */
    public CalculationLog(Source source) {
        this.source = source;
    }

    /**
     * create calculation log with {@link Source#MQ} as source type
     * @param jmsCorrelationId
     */
    public CalculationLog(String jmsCorrelationId) {
        this.source = Source.MQ;
        setJmsCorrelationId(jmsCorrelationId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = JpaTruncator.truncate(number);
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = JpaTruncator.truncate(station);
    }

    public Date getInboundTime() {
        return inboundTime;
    }

    public void setInboundTime(Date inboundTime) {
        this.inboundTime = inboundTime;
    }

    public String getInboundXml() {
        return inboundXml;
    }

    public void setInboundXml(String inboundXml) {
        this.inboundXml = inboundXml;
    }

    public Date getOutboundTime() {
        return outboundTime;
    }

    public void setOutboundTime(Date outboundTime) {
        this.outboundTime = outboundTime;
    }

    public String getOutboundXml() {
        return outboundXml;
    }

    public void setOutboundXml(String outboundXml) {
        this.outboundXml = outboundXml;
    }

    public String getOutboundText() {
        return outboundText;
    }

    public void setOutboundText(String outboundText) {
        this.outboundText = outboundText;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int error) {
        this.errorCode = error;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public void setJmsCorrelationId(String jmsCorrelationId) {
        this.jmsCorrelationId = JpaTruncator.truncate(jmsCorrelationId);
    }

    public String getJmsCorrelationId() {
        return jmsCorrelationId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
