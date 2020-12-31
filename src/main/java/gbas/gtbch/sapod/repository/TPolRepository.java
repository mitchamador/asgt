package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.TPolDocument;
import gbas.gtbch.sapod.model.TPolSobst;
import gbas.gtbch.sapod.model.TpolGroup;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TPolRepository {
    public static final Logger logger = LoggerFactory.getLogger(TPolRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TPolRepository(@Qualifier("sapodDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * get {@link TPolDocument}
     *
     * @param id
     * @return
     */
    public TPolDocument getDocument(int id, boolean editorMode) {
        List<TPolDocument> list = getDocuments(id, null, null, null);
        if (list != null && !list.isEmpty()) {
            TPolDocument document = list.get(0);
            document.sobstList = getSobstList(id, !editorMode);
            return document;
        } else {
            return null;
        }
    }

    /**
     * get list of {@link TPolDocument}
     *
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public List<TPolDocument> getDocuments(Date dateBegin, Date dateEnd) {
        return getDocuments(0, null, dateBegin, dateEnd);
    }

    /**
     * get list of {@link TPolDocument}
     *
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public List<TPolDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd) {
        return getDocuments(0, typeCode, dateBegin, dateEnd);
    }

    /**
     * @param id
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public List<TPolDocument> getDocuments(int id, String typeCode, Date dateBegin, Date dateEnd) {
        List<Object> args = new ArrayList<Object>();

        String sql = "select id,\n" +
                "rtrim(type_code),\n" +
                "rtrim(n_contract),\n" +
                "date_begin,\n" +
                "date_end,\n" +
                "rtrim(name),\n" +
                "n_pol,\n" +
                "cod_tip_tarif,\n" +  //8
                "dobor\n" +  //9
                " from tvk_tarif\n" +
                "WHERE\n";

        if (id == 0) {
            if (dateBegin != null && dateEnd != null) {
                sql += "(date_end >= ? AND date_begin <= ?) AND\n";
                args.add(dateBegin);
                args.add(dateEnd);
            } else if (dateBegin != null) {
                sql += "date_begin >= ? AND\n";
                args.add(dateBegin);
            } else if (dateEnd != null) {
                sql += "date_end <= ? AND\n";
                args.add(dateEnd);
            }

            if (typeCode != null) {
                sql += "type_code = ?\n";
                args.add(typeCode);
            } else {
                sql += "type_code IN ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')\n";
            }

            sql += "ORDER BY n_contract";
        } else {
            sql += "id = ?";
            args.add(id);
        }

        return jdbcTemplate.query(sql, (rs, i) -> {
            final TPolDocument doc = new TPolDocument();
            doc.id = rs.getInt(1);
            doc.type_code = rs.getString(2);
            doc.n_contract = rs.getString(3);
            doc.date_begin = rs.getDate(4);
            doc.date_end = rs.getDate(5);
            doc.name = rs.getString(6);
            doc.n_pol = rs.getInt(7);
            doc.codTipTar = rs.getInt(8);
            doc.codDobor = rs.getShort(9);
            return doc;
        }, args.toArray(new Object[0]));
    }

    /**
     * @return
     */
    public List<CodeName> getBaseTarifList() {
        return getBaseTarifList(0);
    }

    /**
     * TPRow.tipTTar
     *
     * @param idTPol
     * @return
     */
    public List<CodeName> getBaseTarifList(int idTPol) {
        return jdbcTemplate.query(idTPol == 0 ?
                        "select kod, name\n" +
                                "from tvk_tip_tar\n" +
                                "order by 1" :
                        "select k.kod, k.name\n" +
                                "from tvk_tip_tar k, tvk_t_pol t\n" +
                                "where k.kod = t.tip_t_tar and t.id = ?\n" +
                                "order by 1",
                idTPol == 0 ? null : new Object[]{idTPol},
                (rs, i) -> {
                    return new CodeName(Func.iif(rs.getString("kod")), Func.iif(rs.getString("name")));
                });
    }

    /**
     *
     * @param idTarif
     * @param checked
     * @return
     */
    public List<TPolSobst> getSobstList(int idTarif, boolean checked) {
        return jdbcTemplate.query(
                idTarif == 0 ?
                        "select a.kadm, rtrim(a.sname) as sname, rtrim(a.name) as name, 0 as checked\n" +
                                "from tvk_nssobst a\n" +
                                "order by name" :
                        "select a.kadm, rtrim(a.sname) as sname, rtrim(a.name) as name, (case when b.code_adm is null then 0 else 1 end) as checked\n" +
                                "from tvk_nssobst a\n" +
                                "left outer join tvk_tpol_nssobst b on a.kadm = b.code_adm and b.id_tarif = ?\n" +
                                (checked ? "where b.code_adm is not null\n" : "") +
                                "order by name",
                idTarif == 0 ? null : new Object[]{idTarif},
                (rs, i) -> {
                    TPolSobst tPolSobst = new TPolSobst();
                    tPolSobst.setkAdm(rs.getInt("kadm"));
                    tPolSobst.setsName(Func.iif(rs.getString("sname")));
                    tPolSobst.setName(Func.iif(rs.getString("name")));
                    tPolSobst.setChecked(rs.getBoolean("checked"));
                    return tPolSobst;
                });
    }

    /**
     * save {@link TPolSobst} list
     *
     * @param idTarif {@link TPolDocument#id}
     * @param list    {@link TPolSobst} list
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean saveSobstList(int idTarif, List<TPolSobst> list) {
        if (list != null) {
            jdbcTemplate.update("delete from tvk_tpol_nssobst where id_tarif = ?", idTarif);

            jdbcTemplate.execute("insert into tvk_tpol_nssobst (id_tarif, code_adm) values (?, ?)", (PreparedStatementCallback<Object>) preparedStatement -> {
                for (TPolSobst tPolSobst : list) {
                    if (tPolSobst.isChecked()) {
                        preparedStatement.setInt(1, idTarif);
                        preparedStatement.setInt(2, tPolSobst.getkAdm());
                        preparedStatement.executeUpdate();
                    }
                }
                return null;
            });
        }

        return true;
    }

    /**
     * get tpol groups
     *
     * @return
     */
    public List<TpolGroup> getGroups() {
        return jdbcTemplate.query("select code, name from type_document where code IN ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')",
                (rs, i) -> {
                    TpolGroup group = new TpolGroup();
                    group.setCode(Func.iif(rs.getString("code")));
                    group.setName(Func.iif(rs.getString("name")));
                    return group;
                });
    }

    /**
     * save {@link TPolDocument}
     *
     * @param tPolDocument {@link TPolDocument}
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveDocument(TPolDocument tPolDocument) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;
            if (tPolDocument.id == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_tarif (type_code, n_contract, date_begin, date_end, name, n_pol, cod_tip_tarif, dobor)" +
                                " values (?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update tvk_tarif set type_code = ?, n_contract = ?, date_begin = ?, date_end = ?, name = ?, n_pol = ?, cod_tip_tarif = ?, dobor = ? where id = ?");
            }

            preparedStatement.setString(1, tPolDocument.type_code);
            preparedStatement.setString(2, tPolDocument.n_contract);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.ofInstant(tPolDocument.date_begin.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay()));
            //preparedStatement.setTimestamp(3, new Timestamp(tPolDocument.date_begin.getTime()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.ofInstant(tPolDocument.date_end.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay()));
            //preparedStatement.setTimestamp(4, new Timestamp(tPolDocument.date_end.getTime()));
            preparedStatement.setString(5, tPolDocument.name);
            preparedStatement.setInt(6, getTPNumber(tPolDocument.type_code));
            preparedStatement.setInt(7, tPolDocument.codTipTar);
            preparedStatement.setShort(8, tPolDocument.codDobor);

            if (tPolDocument.id != 0) {
                preparedStatement.setInt(9, tPolDocument.id);
            }

            return preparedStatement;
        }, keyHolder);

        if (tPolDocument.id == 0) {
            tPolDocument.id = (int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0);
            ;
        }

        saveSobstList(tPolDocument.id, tPolDocument.sobstList);

        return tPolDocument.id;
    }

    /**
     *
     * @param type_code
     * @return
     */
    public int getTPNumber(String type_code) {
        int nPol = -1;
        if (type_code.equals("base_tarif")) {
            nPol = 0;
        }
        if (type_code.equals("down_tarif")) {
            nPol = 1;
        }
        if (type_code.equals("polnom")) {
            nPol = 2;
        }
        if (type_code.equals("iskl_tarif")) {
            nPol = 3;
        }
        if (type_code.equals("russia_tarif")) {
            nPol = 20;
        }
        if (type_code.equals("pr10_01bch")) {
            nPol = 21;
        }
        if (type_code.equals("tr1_bch")) {
            nPol = 4;
        }
        return nPol;
    }

    /**
     * delete {@link TPolDocument}
     *
     * @param id {@link TPolDocument#id}
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteDocument(int id) {

        Object[] args = new Object[]{id};

        String[] tables = new String[]{
                "tvk_t_gr", "tvk_t_kl", "tvk_t_sr", "tvk_t_so", "tvk_t_sn",
                "tvk_t_grpk", "tvk_t_oso", "tvk_t_oso_add", "tvk_t_upak",
                "tvk_t_k", "tvk_t_v", "tvk_t_adm", "tvk_rail_o", "tvk_rail_n",
                "tvk_stan_o", "tvk_stan_n", "tvk_t_kon", "tvk_t_rps", "tvk_t_vh",
                "tvk_t_vot", "tvk_t_vs", "tvk_t_vyh"
        };

        for (String table : tables) {
            jdbcTemplate.update("delete from " + table + " where id_t_pol in (select id from tvk_t_pol where id_tarif = ?)", args);
        }

        // Удаление строк тарифной политики
        jdbcTemplate.update("delete from tvk_t_pol where id_tarif = ?", args);

        // Удаление ссылок на клиенты
        jdbcTemplate.update("delete from tvk_tarif_client where id_tarif = ?", args);

        // Удаление из таблицы участников ТП
        jdbcTemplate.update("delete from tvk_tpol_nssobst where id_tarif = ?", args);

        // Удаление строки ТП
        jdbcTemplate.update("delete from tvk_tarif where id = ?", args);

        return true;
    }


    @Autowired
    private TPolRowRepository tPolRowRepository;

    /**
     *
     * @param sourceId
     * @param destinationId
     * @return
     */
    public Integer copyDocument(int sourceId, int destinationId) {

        List<TPRow> sourceRows = tPolRowRepository.getRows(sourceId);

        if (sourceRows == null) return null;

        for (TPRow tpRow : sourceRows) {
            tPolRowRepository.copyRow(tpRow.id, destinationId);
        }

        return destinationId;
    }
}