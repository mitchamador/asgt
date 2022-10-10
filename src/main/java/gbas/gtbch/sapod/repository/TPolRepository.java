package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CodeName;
import gbas.gtbch.sapod.model.tpol.*;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.tpol3.service.TPItems;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gbas.gtbch.util.JdbcTemplateUtils.getSqlString;

@Component
public class TPolRepository {
    public static final Logger logger = LoggerFactory.getLogger(TPolRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate jdbcTemplatePensi;
    private final TPolRowRepository tPolRowRepository;

    @Autowired
    public TPolRepository(@Qualifier("sapodDataSource") DataSource dataSource, @Qualifier("pensiDataSource") DataSource dataSourcePensi, TPolRowRepository tPolRowRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplatePensi = new JdbcTemplate(dataSourcePensi);
        this.tPolRowRepository = tPolRowRepository;
    }

    /**
     * get {@link TpDocument}
     *
     * @param id
     * @return
     */
    public TpDocument getDocument(int id, boolean editorMode) {
        List<TpDocument> list = getDocuments(id, null, null, null, null);
        if (list != null && !list.isEmpty()) {
            TpDocument document = list.get(0);
            document.sobstList = getSobstList(id, !editorMode);
            return document;
        } else {
            return null;
        }
    }

    public List<TpDocument> getDocuments(String typeCode, Date dateBegin, Date dateEnd, Map<String, String> filterMap) {
        return getDocuments(0, typeCode, dateBegin, dateEnd, filterMap);
    }

    /**
     * @param id
     * @param typeCode
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    private List<TpDocument> getDocuments(int id, String typeCode, Date dateBegin, Date dateEnd, Map<String, String> filterMap) {
        List<Object> args = new ArrayList<Object>();

        String sql = "SELECT DISTINCT\n" +
                " tvk_tarif.id,\n" +
                " rtrim(type_code) as type_code,\n" +
                " rtrim(n_contract) as n_contract,\n" +
                " date_begin,\n" +
                " date_end,\n" +
                " rtrim(name) as name,\n" +
                " n_pol,\n" +
                " cod_tip_tarif,\n" +
                " dobor,\n" +
                " pr_calc\n" +
                "FROM tvk_tarif\n";

        if (id == 0) {

            String sqlFilterString = TpItemFilter.getFilterSqlString(args, filterMap);

            if (!sqlFilterString.isEmpty()) {
                sql += "LEFT OUTER JOIN tvk_t_pol ON tvk_t_pol.id_tarif = tvk_tarif.id\n" + sqlFilterString;
            }

            if (dateBegin != null && dateEnd != null) {
                sql += getSqlString(args, "(date_end >= ? AND date_begin <= ?)");
                args.add(dateBegin);
                args.add(dateEnd);
            } else if (dateBegin != null) {
                sql += getSqlString(args, "date_begin >= ?");
                args.add(dateBegin);
            } else if (dateEnd != null) {
                sql += getSqlString(args, "date_end <= ?");
                args.add(dateEnd);
            }

            if (typeCode != null) {
                sql += getSqlString(args, "type_code = ?");
                args.add(typeCode);
            } else {
                sql += getSqlString(args, "type_code IN ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')");
            }

        } else {
            sql += getSqlString(args, "id = ?");
            args.add(id);
        }

        sql += "ORDER BY n_contract";

        return jdbcTemplate.query(sql, (rs, i) -> {
            final TpDocument doc = new TpDocument();
            doc.id = rs.getInt("id");
            doc.type_code = rs.getString("type_code");
            doc.n_contract = rs.getString("n_contract");
            doc.date_begin = rs.getDate("date_begin");
            doc.date_end = rs.getDate("date_end");
            doc.name = rs.getString("name");
            doc.n_pol = rs.getInt("n_pol");
            doc.codTipTar = rs.getInt("cod_tip_tarif");
            doc.codDobor = rs.getShort("dobor");
            doc.pr_calc = rs.getShort("pr_calc");
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
                                (!Func.isEmpty(TPItems.getTarifListSql()) ? ("where kod in (" + TPItems.getTarifListSql() + ")\n") : "") +
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
    public List<TpSobst> getSobstList(int idTarif, boolean checked) {
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
                    TpSobst tpSobst = new TpSobst();
                    tpSobst.setkAdm(rs.getInt("kadm"));
                    tpSobst.setsName(Func.iif(rs.getString("sname")));
                    tpSobst.setName(Func.iif(rs.getString("name")));
                    tpSobst.setChecked(rs.getBoolean("checked"));
                    return tpSobst;
                });
    }

    /**
     * save {@link TpSobst} list
     *
     * @param idTarif {@link TpDocument#id}
     * @param list    {@link TpSobst} list
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean saveSobstList(int idTarif, List<TpSobst> list) {
        if (list != null) {
            jdbcTemplate.update("delete from tvk_tpol_nssobst where id_tarif = ?", idTarif);

            jdbcTemplate.execute("insert into tvk_tpol_nssobst (id_tarif, code_adm) values (?, ?)", (PreparedStatementCallback<Object>) preparedStatement -> {
                for (TpSobst tpSobst : list) {
                    if (tpSobst.isChecked()) {
                        preparedStatement.setInt(1, idTarif);
                        preparedStatement.setInt(2, tpSobst.getkAdm());
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
    public List<TpGroup> getGroups() {
        return jdbcTemplate.query("select code, name from type_document where code IN ('base_tarif', 'down_tarif', 'polnom', 'russia_tarif', 'iskl_tarif', 'tr1_bch')",
                (rs, i) -> {
                    TpGroup group = new TpGroup();
                    group.setCode(Func.iif(rs.getString("code")));
                    group.setName(Func.iif(rs.getString("name")));
                    return group;
                });
    }

    /**
     * save {@link TpDocument}
     *
     * @param tpDocument {@link TpDocument}
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveDocument(TpDocument tpDocument) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement;
            if (tpDocument.id == 0) {
                preparedStatement = connection.prepareStatement("insert into tvk_tarif (type_code, n_contract, date_begin, date_end, name, n_pol, cod_tip_tarif, dobor, pr_calc)" +
                                " values (?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement("update tvk_tarif set type_code = ?, n_contract = ?, date_begin = ?, date_end = ?, name = ?, n_pol = ?, cod_tip_tarif = ?, dobor = ?, pr_calc = ? where id = ?");
            }

            preparedStatement.setString(1, tpDocument.type_code);
            preparedStatement.setString(2, tpDocument.n_contract);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.ofInstant(tpDocument.date_begin.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay()));
            //preparedStatement.setTimestamp(3, new Timestamp(tpDocument.date_begin.getTime()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.ofInstant(tpDocument.date_end.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay()));
            //preparedStatement.setTimestamp(4, new Timestamp(tpDocument.date_end.getTime()));
            preparedStatement.setString(5, tpDocument.name);
            preparedStatement.setInt(6, getTPNumber(tpDocument.type_code));
            preparedStatement.setInt(7, tpDocument.codTipTar);
            preparedStatement.setShort(8, tpDocument.codDobor);
            preparedStatement.setShort(9, tpDocument.pr_calc);

            if (tpDocument.id != 0) {
                preparedStatement.setInt(10, tpDocument.id);
            }

            return preparedStatement;
        }, keyHolder);

        if (tpDocument.id == 0) {
            tpDocument.id = (int) (keyHolder.getKey() != null ? keyHolder.getKey() : 0);
            ;
        }

        saveSobstList(tpDocument.id, tpDocument.sobstList);

        return tpDocument.id;
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
     * delete {@link TpDocument}
     *
     * @param id {@link TpDocument#id}
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


    /**
     *
     * @param sourceId
     * @param destinationId
     * @return
     */
    public Integer copyDocument(int sourceId, int destinationId) {

        List<TpRow> sourceRows = tPolRowRepository.getRows(sourceId, null);

        if (sourceRows == null) return null;

        for (TpRow tpRow : sourceRows) {
            tPolRowRepository.copyRow(tpRow.id, destinationId);
        }

        return destinationId;
    }

    /**
     *
     * @return
     */
    public List<TpClient> getTpClients() {
        List<TpClient> list = new ArrayList<>();

        list.addAll(jdbcTemplatePensi.query("select kod, name1\n" +
                        "from forward_bch\n" +
                        "order by kod",
                (rs, rowNum) -> {
                    TpClient tpClient = new TpClient();
                    tpClient.setCode(Func.iif(rs.getString("kod")));
                    tpClient.setName(Func.iif(rs.getString("name1")));
                    return tpClient;
                }
        ));

//        list.addAll(jdbcTemplate.query("SELECT consi_bch_no, consi_bch_name, div_no\n" +
//                        "FROM s_klient_consign_bch\n" +
//                        "ORDER by consi_bch_no",
        list.addAll(jdbcTemplatePensi.query("select consi_bch_no, consi_bch_name, rail_div.div_no\n" +
                        "from consign_bch\n" +
                        "left outer join rail_div on rail_div.div#un = consign_bch.div#un",
                (rs, rowNum) -> {
                    TpClient tpClient = new TpClient();
                    tpClient.setCode(Func.iif(rs.getString("consi_bch_no")));
                    tpClient.setName(Func.iif(rs.getString("consi_bch_name")));
                    tpClient.setNumNod(rs.getInt("div_no"));
                    return tpClient;
                }
        ));

        return list;
    }

    /**
     *
     * @param idTarif
     * @return
     */
    public List<TpLinkedClient> getLinkedTpClients(int idTarif) {
        return jdbcTemplate.query("select id_tarif, code_client, name, sector from tvk_tarif_client where id_tarif = ?",
                (rs, rowNum) -> {
                    TpLinkedClient tpLinkedClient = new TpLinkedClient();
                    tpLinkedClient.setIdTarif(rs.getInt("id_tarif"));
                    tpLinkedClient.setCode(Func.iif(rs.getString("code_client")));
                    tpLinkedClient.setName(Func.iif(rs.getString("name")));
                    tpLinkedClient.setNumNod(rs.getInt("sector"));
                    return tpLinkedClient;
                },
                idTarif);
    }

    /**
     *
     * @param idTarif
     * @param clientList
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean saveLinkedTpClients(int idTarif, List<TpLinkedClient> clientList) {

        deleteLinkedTpClients(idTarif);

        List<Object[]> paramsList = clientList.stream()
                .map(client -> {
                    List<Object> objectList = new ArrayList<>();
                    objectList.add(idTarif);
                    objectList.add(client.getCode());
                    objectList.add(client.getName());
                    objectList.add(client.getNumNod());
                    return objectList.toArray(new Object[0]);
                })
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "insert into tvk_tarif_client (id_tarif, code_client, name, sector)\n" +
                        "values (?, ?, ?, ?)",
                paramsList
        );

        return true;
    }

    /**
     *
     * @param idTarif
     * @return
     */
    @Transactional(transactionManager = "sapodTransactionManager")
    public boolean deleteLinkedTpClients(int idTarif) {
        return jdbcTemplate.update("delete from tvk_tarif_client where id_tarif = ?", idTarif) > 0;
    }
}