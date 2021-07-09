package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.RecalcRate;
import gbas.gtbch.sapod.repository.RecalcRateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("rateRecalcService")
public class RecalcRateServiceImpl implements RecalcRateService {

    private final RecalcRateRepository recalcRateRepository;

    public RecalcRateServiceImpl(RecalcRateRepository recalcRateRepository) {
        this.recalcRateRepository = recalcRateRepository;
    }

    @Override
    public List<RecalcRate> getRecalcRates() {
        return recalcRateRepository.findAll(Sort.by(Sort.Direction.DESC, "dateBegin"));
    }

    @Override
    public RecalcRate findRecalcRateById(int id) {
        return recalcRateRepository.findById(id).orElse(null);
    }

    @Override
    public RecalcRate save(RecalcRate recalcRate) {
        return recalcRateRepository.save(recalcRate);
    }

    @Override
    public void delete(int id) {
        recalcRateRepository.deleteById(id);
    }
}
