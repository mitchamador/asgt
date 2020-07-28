package gbas.gtbch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.web.request.KeyValue;
import gbas.gtbch.websapod.ServicesImpl;
import gbas.sapod.bridge.controllers.Services;
import gbas.sapod.bridge.utilities.JsonBuilder;
import gbas.tvk.util.UtilDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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

	@Test
	public void jsonBuilderTest() throws JsonProcessingException {
		Map<String, Object> m = new LinkedHashMap<>();

		m.put("test1", "test2_value");
		m.put("test2", "test2_value");

		List<Map<String, String>> mapList = new ArrayList<>();

		Map<String, String> m2 = new LinkedHashMap<>();
		m2.put("inner_map_test_param1", "value1");
		m2.put("inner_map_test_param2", "value2");

		mapList.add(m2);

		m.put("inner_map", mapList);

		logger.info(new ObjectMapper().writer().writeValueAsString(JsonBuilder.getJsonObject(m)));

		Map<String, String> m3 = new LinkedHashMap<>();
		m3.put("inner map test param1", "value1");
		m3.put("inner map test param2", "value2");

		logger.info(new ObjectMapper().writer().writeValueAsString(JsonBuilder.getJsonLabelValueArray(m3)));
	}

	@Autowired
	@Qualifier("sapodDataSource")
	private DataSource sapodDataSource;

	@Test
	public void servicesTest() {
		try (Connection c = sapodDataSource.getConnection()) {
			Services services = new ServicesImpl(c);

			Vector v = services.getStikSng();
			logger.info(String.valueOf(v.get(0)));

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
