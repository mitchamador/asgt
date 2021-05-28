package gbas.gtbch.util;

import gbas.gtbch.util.calc.CalcData;
import gbas.gtbch.util.calc.CalcHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class CalcHandlerTest {

    private final static Logger logger = LoggerFactory.getLogger(CalcHandlerTest.class.getName());

    @Autowired
    CalcHandler calcHandler;

    @Test
    public void calc() {
        CalcData calcData;

        calcData = new CalcData(null, null);
        calcData = calcHandler.calc(calcData);
        logger.info("output xml: \"{}\", output text: \"{}\"", calcData.getOutputXml(), calcData.getTextResult());

        calcData = new CalcData("test", null);
        calcData = calcHandler.calc(calcData);
        logger.info("output xml: \"{}\", output text: \"{}\"", calcData.getOutputXml(), calcData.getTextResult());
    }
}