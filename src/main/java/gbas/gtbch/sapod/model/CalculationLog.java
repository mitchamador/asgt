package gbas.gtbch.sapod.model;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    @Column(name = "docnumber")
    @Size(max = 20)
    private String number;

    /**
     * station code
     */
    @Column(name = "station")
    @Size(max = 6)
    private String station;

    /**
     * inbound time
     */
    @Column(name = "inbound_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS", timezone = "Europe/Minsk")
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS", timezone = "Europe/Minsk")
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
        this.number = number;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
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

}
