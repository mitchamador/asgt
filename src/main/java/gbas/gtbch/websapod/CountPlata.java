package gbas.gtbch.websapod;

import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.InfoPay;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.payment.Stroka;

import java.sql.Connection;
import java.util.Vector;

public class CountPlata {
    private final Vector msh;
    private final Connection con;

    private InfoPay infoPay;
    private String s;
    private VagonOtprTransit vo;
    private double kurs;
    private double plata;

    private Stroka strIsklTar;
    private String[] ssCurs;
    private boolean skidAllvag;

    public CountPlata(Connection con, VagonOtprTransit vo, Vector msh) throws Exception {
        this.con = con;

        this.vo = vo;
        this.msh = msh;

        plataTpServerMarshrut();

    }

    public InfoPay getInfoPay() {
        return infoPay;
    }

    public String getResultText() {
        return s;
    }

    private void plataTpServerMarshrut() throws Exception {
        fillFromCalcPlatData(new PayTransportation(con).countPlataMarshrut(setCalcPlatData(3)));
    }

    private CalcPlataData setCalcPlatData(int mode) {
        CalcPlataData data = new CalcPlataData(mode);

        data.vo = vo;
        data.strIsklTar = strIsklTar;
        data.ssCurs = ssCurs;
        data.skidAllvag = skidAllvag;
        data.msh = msh;

        return data;
    }

    private void fillFromCalcPlatData(CalcPlataData data) {
        infoPay = data.infopay;
        this.vo = data.vo;
        s = data.s;
        kurs = data.kurs;
        plata = data.plata;
    }


}
