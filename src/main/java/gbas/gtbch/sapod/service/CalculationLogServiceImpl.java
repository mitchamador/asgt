package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.repository.CalculationLogListRepository;
import gbas.gtbch.sapod.repository.CalculationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("calculationLogService")
@Transactional(transactionManager = "sapodTransactionManager")
public class CalculationLogServiceImpl implements CalculationLogService {

    private final CalculationLogRepository calculationLogRepository;
    private final CalculationLogListRepository calculationLogListRepository;

    public CalculationLogServiceImpl(CalculationLogRepository calculationLogRepository, CalculationLogListRepository calculationLogListRepository) {
        this.calculationLogRepository = calculationLogRepository;
        this.calculationLogListRepository = calculationLogListRepository;
    }

    @Override
    public List<CalculationLog> find(CalculationLog.Type type, String station, Date startTime, Date endTime) {
        if (startTime != null && endTime != null) {
            if (type == null && station == null) {
                return calculationLogRepository.findByInboundTimeBetween(startTime, endTime);
            } else if (type != null && station == null) {
                return calculationLogRepository.findByTypeAndInboundTimeBetween(type, startTime, endTime);
            } else if (type == null && station != null) {
                return calculationLogRepository.findByStationAndInboundTimeBetween(station, startTime, endTime);
            } else {
                return calculationLogRepository.findByTypeAndStationAndInboundTimeBetween(type, station, startTime, endTime);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public CalculationLog save(CalculationLog log) {
        return calculationLogRepository.save(log);
    }

    @Override
    public CalculationLog findById(int id) {
        return calculationLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<CalculationLog> getList(Map<String, String> params, Date dateBegin, Date dateEnd) {
        return calculationLogListRepository.getList(params, dateBegin, dateEnd);
    }
}
