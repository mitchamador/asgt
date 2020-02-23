package gbas.gtbch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDatasources {

    @Autowired
    @Qualifier("sapodDataSource")
    private DataSource sapodDataSource;

    @Autowired
    @Qualifier("pensiDataSource")
    private DataSource pensiDataSource;

    @Test
    public void givenTomcatSapodConnectionPoolInstance__whenCheckedPoolClassName__thenCorrect() {
        assertThat(sapodDataSource.getClass().getName()).isEqualTo("org.apache.tomcat.jdbc.pool.DataSource");
    }

    @Test
    public void givenTomcatPensiConnectionPoolInstance__whenCheckedPoolClassName__thenCorrect() {
        assertThat(pensiDataSource.getClass().getName()).isEqualTo("org.apache.tomcat.jdbc.pool.DataSource");
    }
}
