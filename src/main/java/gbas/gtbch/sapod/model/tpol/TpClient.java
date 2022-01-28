package gbas.gtbch.sapod.model.tpol;

import gbas.gtbch.util.JpaTruncator;

import javax.persistence.Column;

public class TpClient {

    /**
     * client's code
     */
    @Column(length = 20)
    private String code;

    /**
     * client's name
     */
    @Column(length = 255)
    private String name;

    /**
     * client's div
     */
    private int numNod;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = JpaTruncator.truncate(code);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = JpaTruncator.truncate(name);
    }

    public int getNumNod() {
        return numNod;
    }

    public void setNumNod(int numNod) {
        this.numNod = numNod;
    }

    @Override
    public String toString() {
        return "TpClient{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", numNod=" + numNod +
                '}';
    }
}
