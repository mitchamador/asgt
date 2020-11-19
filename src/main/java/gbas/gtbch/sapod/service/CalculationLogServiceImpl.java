package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.repository.CalculationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("calculationLogService")
public class CalculationLogServiceImpl implements CalculationLogService {

    private final CalculationLogRepository calculationLogRepository;

    public CalculationLogServiceImpl(CalculationLogRepository calculationLogRepository) {
        this.calculationLogRepository = calculationLogRepository;
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
}
