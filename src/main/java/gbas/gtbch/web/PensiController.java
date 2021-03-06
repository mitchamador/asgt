package gbas.gtbch.web;

import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.schedule.PensiDownloaderJob;
import gbas.gtbch.schedule.PensiSyncronizerJob;
import gbas.gtbch.util.ServerJob;
import gbas.gtbch.web.request.KeyValue;
import gbas.gtbch.web.request.PensiUpdate;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.PensiQueue;
import gbas.tvk.interaction.pensi.description.PensiSpr;
import gbas.tvk.interaction.pensi.description.PensiSprDescription;
import gbas.tvk.interaction.pensi.description.PensiTableDescription;
import gbas.tvk.interaction.pensi.jobs.PensiMailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RestController
public class PensiController {

    private static Logger logger = LoggerFactory.getLogger(PensiController.class.getName());

    @Autowired
    private PensiDownloaderJob pensiDownloaderJob;

    @Autowired
    private PensiSyncronizerJob pensiSyncronizerJob;

    @Autowired
    private PensiManager pensiManager;

    /**
     *
     * @return
     */
    @GetMapping("/admin/pensi")
    public ModelAndView adminPensi() {
        ModelAndView pensi = new ModelAndView("admin/pensi");
        try {
            pensi.addObject("pensiSprs", pensiManager.getPensiSprList());

            Properties p = pensiManager.readConfig();
            pensi.addObject(PensiMailer.POP3_HOSTNAME, p.getProperty(PensiMailer.POP3_HOSTNAME));
            pensi.addObject(PensiMailer.POP3_USERNAME, p.getProperty(PensiMailer.POP3_USERNAME));
            pensi.addObject(PensiMailer.POP3_PASSWORD, p.getProperty(PensiMailer.POP3_PASSWORD));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pensi;
    }

    /**
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/api/pensi/update", method = RequestMethod.POST)
    public ServerResponse pensiUpdate(@RequestBody PensiUpdate body) {

        ServerResponse response = new ServerResponse();

        if (body != null && body.getList() != null && !body.getList().isEmpty()) {
            ServerJob serverJob = null;
            List<PensiQueue> queueList = new ArrayList<>();

            switch (body.getType()) {
                case "tables":
                    for (String name : body.getList()) {
                        PensiSprDescription desc = PensiSprDescription.getSprDescription(name);
                        if (desc != null) {
                            for (PensiTableDescription table : desc.getTables()) {
                                queueList.add(new PensiQueue.Builder().type(PensiQueue.QUEUE_TYPE_TABLE).name(table.getName()).build());
                            }
                        }
                    }
                    serverJob = pensiDownloaderJob;
                    break;
                case "sprs":
                    for (String name : body.getList()) {
                        queueList.add(new PensiQueue.Builder().type(PensiQueue.QUEUE_TYPE_SPR).name(name).build());
                    }
                    serverJob = pensiSyncronizerJob;
                    break;
                default:
            }

            if (!queueList.isEmpty() && serverJob != null) {

                try {
                    pensiManager.writeQueue(queueList);
                    serverJob.run();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

        response.setMessage("OK");

        return response;
    }

    /**
     * ???????????? ???????????????????????? ???????????????????????? ?? ????????????
     * @return
     */
    @RequestMapping(value = "/api/pensi/config", method = RequestMethod.GET)
    public ResponseEntity<List<PensiSpr>> pensiConfigGet() {

        List<PensiSpr> config = null;
        try {
            config = pensiManager.getPensiSprList();
        } catch (SQLException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(config, HttpStatus.OK);
    }

    /**
     * ???????????????????? ???????????????????????? ???????????????????????? ?? ????????????
     * @param config
     * @return
     */
    @RequestMapping(value = "/api/pensi/config", method = RequestMethod.POST)
    public ResponseEntity pensiConfigSet(@RequestBody List<KeyValue> config) {

        if (config != null) {
            Properties p = new Properties();
            for (KeyValue k : config) {
                p.put(k.getKey(), k.getValue());
            }
            try {
                pensiManager.writeConfig(p);
            } catch (SQLException e) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/api/pensi/mailerconfig", method = RequestMethod.POST)
    public ResponseEntity pensiMailerConfigSet(@RequestParam(name = PensiMailer.POP3_HOSTNAME) String hostname,
                                               @RequestParam(name = PensiMailer.POP3_USERNAME) String username,
                                               @RequestParam(name = PensiMailer.POP3_PASSWORD) String password) {

        try {
            Properties p = new Properties();
            p.setProperty(PensiMailer.POP3_HOSTNAME, hostname);
            p.setProperty(PensiMailer.POP3_USERNAME, username);
            p.setProperty(PensiMailer.POP3_PASSWORD, password);
            pensiManager.writeConfig(p);
        } catch (SQLException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }


}