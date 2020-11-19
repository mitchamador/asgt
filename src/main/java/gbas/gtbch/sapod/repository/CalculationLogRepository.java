package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CalculationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CalculationLogRepository extends JpaRepository<CalculationLog, Integer> {

    /**
     *
     * @param type
     * @param station
     * @param startTime
     * @param endTime
     * @return
     */
    List<CalculationLog> findByTypeAndStationAndInboundTimeBetween(CalculationLog.Type type, String station, Date startTime, Date endTime);

    /**
     *
     * @param type
     * @param startTime
     * @param endTime
     * @return
     */
    List<CalculationLog> findByTypeAndInboundTimeBetween(CalculationLog.Type type, Date startTime, Date endTime);

    /**
     *
     * @param station
     * @param startTime
     * @param endTime
     * @return
     */
    List<CalculationLog> findByStationAndInboundTimeBetween(String station, Date startTime, Date endTime);

    /**
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<CalculationLog> findByInboundTimeBetween(Date startTime, Date endTime);

}
