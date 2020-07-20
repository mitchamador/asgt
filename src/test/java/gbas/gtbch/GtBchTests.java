package gbas.gtbch;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.SystemInfo;
import gbas.gtbch.web.request.KeyValue;
import gbas.tvk.util.UtilDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class GtBchTests {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void contextLoads() {
	}

	@Autowired
	TpImportDateService tpImportDateService;

	@Test
	public void testImportDate() {
		TpImportDate tpImportDate1 = new TpImportDate();
		tpImportDate1.setDateCreate(UtilDate.getDate("01.06.2020"));
		tpImportDate1.setDateImport(UtilDate.getDate("10.06.2020"));
		tpImportDateService.save(tpImportDate1);

		TpImportDate tpImportDate2 = new TpImportDate();
		tpImportDate2.setDateCreate(UtilDate.getDate("01.07.2020"));
		tpImportDate2.setDateImport(UtilDate.getDate("10.07.2020"));
		tpImportDateService.save(tpImportDate2);

		logger.info(tpImportDateService.getTpImportDate().toString());

		tpImportDateService.delete(tpImportDate2);
		tpImportDateService.delete(tpImportDate1);
	}

	@Test
	public void testImportDate2() {
		TpImportDate tpImportDate = tpImportDateService.getTpImportDate();
		if (tpImportDate == null) {
			logger.info("tpImportDate is null");
		} else {
			logger.info(tpImportDate.toString());
		}
	}

	@Autowired
	List<KeyValue> systemProperties;

	@Test
	public void systemInfoTest() {
		for (KeyValue k : systemProperties) {
			logger.info(k.getKey() + " " + k.getValue());
		}
	}

}
