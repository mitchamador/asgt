package gbas.gtbch;

import com.fasterxml.jackson.core.JsonProcessingException;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.repository.TpolRepositoryImpl;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.web.request.KeyValue;
import gbas.gtbch.websapod.ServicesImpl;
import gbas.sapod.bridge.controllers.Services;
import gbas.sapod.bridge.utilities.JsonBuilder;
import gbas.tvk.tpol3.TpolDocument;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
		List<gbas.sapod.bridge.constants.json.KeyValue> m = new ArrayList<>();

		m.add(new gbas.sapod.bridge.constants.json.KeyValue("test1", "test2_value"));
		m.add(new gbas.sapod.bridge.constants.json.KeyValue("test2", "test2_value"));

		List<List<gbas.sapod.bridge.constants.json.KeyValue>> mapList = new ArrayList<>();

		List<gbas.sapod.bridge.constants.json.KeyValue> m2 = new ArrayList<>();
		m2.add(new gbas.sapod.bridge.constants.json.KeyValue("inner_map_test_param1", "value1"));
		m2.add(new gbas.sapod.bridge.constants.json.KeyValue("inner_map_test_param2", "value2"));

		mapList.add(m2);

		m.add(new gbas.sapod.bridge.constants.json.KeyValue("inner_map", mapList));

		logger.info(JsonBuilder.getJsonObject(m).toJSONString());

		List<gbas.sapod.bridge.constants.json.KeyValue> m3 = new ArrayList<>();
		m3.add(new gbas.sapod.bridge.constants.json.KeyValue("inner map test param1", "value1"));
		m3.add(new gbas.sapod.bridge.constants.json.KeyValue("inner map test param2", "value2"));

		logger.info(JsonBuilder.getJsonLabelValueArray(m3).toJSONString());
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

	@Autowired
	TpolRepositoryImpl tpolRepository;

	@Test
	public void tpolRepositoryTest() throws ParseException {
		List<TpolDocument> list;
		list = tpolRepository.getDocuments(null, null);
		logger.info("getDocuments(null, null): " +  (list == null ? "null" : ("list size = " + list.size())));
		list = tpolRepository.getDocuments(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2020"), null);
		logger.info("getDocuments(date, null): " +  (list == null ? "null" : ("list size = " + list.size())));
		list = tpolRepository.getDocuments(null, new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2018"));
		logger.info("getDocuments(null, date): " +  (list == null ? "null" : ("list size = " + list.size())));
		list = tpolRepository.getDocuments(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2018"), new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2020"));
		logger.info("getDocuments(date, date): " +  (list == null ? "null" : ("list size = " + list.size())));
	}

}
