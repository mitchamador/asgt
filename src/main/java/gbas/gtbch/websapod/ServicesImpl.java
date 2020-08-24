package gbas.gtbch.websapod;

import gbas.sapod.bridge.constants.json.KeyValue;
import gbas.sapod.bridge.controllers.CountPlataResult;
import gbas.sapod.bridge.controllers.NsiMethod;
import gbas.sapod.bridge.controllers.Services;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.db.DbPayment;
import gbas.tvk.rwmap.dist.objects.DistanceData;
import gbas.tvk.rwmap.rasst.Station;
import gbas.tvk.service.SQLUtils;
import gbas.tvk.tuner.service.RailwayMetadata;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class ServicesImpl implements Services {

    RailwayMetadata railwayMetadata;

    private DbPayment getPayServer() throws Exception {
        if (payServer == null) {
            payServer = new DbPayment(con);
        }
        return payServer;
    }

    private DbPayment payServer;

    private Connection con;

    public ServicesImpl(Connection con) {
        this.con = con;
    }

    public int getIdGng(String kodGng) throws Exception {
        return getPayServer().getIdGng(kodGng);
    }

    public int getClassGrETT(String kodEtsng) throws Exception {
        return getPayServer().getClassGrETT(kodEtsng);
    }

    public RailwayMetadata getRailwayMetadata() {
        if (railwayMetadata == null) {
            try {
                railwayMetadata = RailwayMetadata.getDefault(con);
            } catch (RemoteException e) {
                railwayMetadata = new RailwayMetadata();
            }
        }
        return railwayMetadata;
    }

    public String[] getId_IsklTarif(Date date) throws Exception {
        return getPayServer().getId_IsklTarif(date);
    }

    public String[] getId_PolnomSkid(Date date) throws Exception {
        return getPayServer().getId_PolnomSkid(date);
    }

    public Vector getStringIsklTar(String id_tarif, VagonOtprTransit otpr, int i, int pr) throws Exception {
        return getPayServer().getStringIsklTar(id_tarif, otpr, i, pr);
    }

    public String getNContractTvkTarif(int id_tarif) throws Exception {
        return getPayServer().getNContractTvkTarif(id_tarif);
    }

    public String[] getInfKl(String id_tarif) throws Exception {
        return getPayServer().getInfKl(id_tarif);
    }

    public Vector analyseV(Vector vct) throws Exception {
        return getPayServer().analyseV(vct);
    }

    public float getKofRast(int rast, int n_tab) throws Exception {
        return getPayServer().getKofRast(rast, n_tab);
    }

    public Vector SchetR_SMGS(String sOtpr, String sNazn) throws Exception {
        return getPayServer().SchetR_SMGS(sOtpr, sNazn);
    }

    public Vector SchetR_SMGS(String sOtpr, String sNazn, int kVOtpr, int iskl) throws Exception {
        return getPayServer().SchetR_SMGS(sOtpr, sNazn, kVOtpr, iskl);
    }

    public Vector SchetR_SMGS(String sOtpr, String sJoint, String sNazn, int kVOtpr, int iskl) throws Exception {
        return getPayServer().SchetR_SMGS(sOtpr, sJoint, sNazn, kVOtpr, iskl);
    }

    public Vector SchetR_SMGS(String sOtpr, String sNazn, int kVOtpr, int iskl, int type) throws Exception {
        return getPayServer().SchetR_SMGS(sOtpr, sNazn, kVOtpr, iskl, type);
    }

    public Vector SchetR_SMGS(String sOtpr, String sJoint, String sNazn, int kVOtpr, int iskl, int type) throws Exception {
        return getPayServer().SchetR_SMGS(sOtpr, sJoint, sNazn, kVOtpr, iskl, type);
    }

    public Vector SchetR_SMGS(String[] route) throws Exception {
        return getPayServer().SchetR_SMGS(route);
    }

    public String getJointCode(String code) throws Exception {
        return getPayServer().getJointCode(code);
    }

    public String[] dor_kname(String kod_stan) throws Exception {
        return getPayServer().dor_kname(kod_stan);
    }

    public short getAdminCode(String station) {
        if (station == null) return 0;
        try {
            return Short.parseShort(getPayServer().dor_kname(station)[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Vector getBorderStationList(String country_code) throws Exception {
        return getPayServer().getBorderStationList(country_code);
    }

    public int getParagraf(String station_code) throws Exception {
        return getPayServer().getParagraf(station_code);
    }

    public Vector getStikSng() throws Exception {
        Vector result = new Vector<Vector>();
        Statement st = null;
        try {
            st = con.createStatement();
            ResultSet rs = st.executeQuery("select id, kod, name from tvk_stan where type in (1, 2) and is_delete <> 1 order by kod");
            while (rs.next()) {
                Vector v = new Vector();
                v.add(Func.iif(rs.getString("kod")));
                v.add(Func.iif(rs.getString("name")));
                v.add(Func.iif(rs.getString("id")));

                result.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtils.close(st);
        }
        return result;
    }

    public CountPlataResult countPlata(VagonOtprTransit vo, Vector msh) throws Exception {
        CountPlata cp = new CountPlata(con, vo, msh);
        CountPlataResult res = new CountPlataResult();
        res.setInfoPay(cp.getInfoPay());
        res.setResultText(cp.getResultText());
        return res;
    }

    @Override
    public List<List<Object>> getNsi(String method) throws Exception {

        Statement st = null;
        List<List<Object>> result = new ArrayList<>();

        try {
            NsiMethod nsiMethod = NsiMethod.getNsiMethod(method);

            if (nsiMethod != null) {
                st = con.createStatement();

                ResultSet rs = st.executeQuery(nsiMethod.getSql());
                int columnCount = rs.getMetaData().getColumnCount();
                {
                    List<Object> data = new ArrayList<>();
                    for (int i = 0; i < columnCount; i++) {
                        data.add(rs.getMetaData().getColumnLabel(i + 1).toLowerCase());
                    }
                    result.add(data);
                }

                while (rs.next()) {
                    List<Object> data = new ArrayList<>();
                    for (int i = 0; i < columnCount; i++) {
                        data.add(rs.getObject(i + 1));
                    }
                    result.add(data);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtils.close(st);
        }

        return result;
    }

    @Override
    public List<KeyValue> getImportTpDate() throws Exception {
        return null;
    }

    @Override
    public List<Station> calculateRoute(DistanceData distanceData) throws Exception {
        return getPayServer().calculateRoute(distanceData);
    }
}