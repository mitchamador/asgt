package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.matherials.Measure;
import gbas.gtbch.sapod.repository.MeasureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("measureService")
public class MeasureServiceImpl implements MeasureService {

    private final MeasureRepository measureRepository;

    public MeasureServiceImpl(MeasureRepository measureRepository) {
        this.measureRepository = measureRepository;
    }

    @Override
    public List<Measure> getMeasureList() {
        return measureRepository.getMeasures();
    }
}
