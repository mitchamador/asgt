package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.matherials.*;
import gbas.gtbch.sapod.repository.MatherialKoefRepository;
import gbas.gtbch.sapod.repository.MatherialNdsRepository;
import gbas.gtbch.sapod.repository.MatherialPriceRepository;
import gbas.gtbch.sapod.repository.MatherialRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("matherialService")
public class MatherialServiceImpl implements MatherialService {

    /**
     *
     */
    private final MatherialRepository matherialRepository;

    private final MatherialKoefRepository matherialKoefRepository;

    private final MatherialPriceRepository matherialPriceRepository;

    private final MatherialNdsRepository matherialNdsRepository;

    public MatherialServiceImpl(MatherialRepository matherialRepository, MatherialKoefRepository matherialKoefRepository, MatherialPriceRepository matherialPriceRepository, MatherialNdsRepository matherialNdsRepository) {
        this.matherialRepository = matherialRepository;
        this.matherialKoefRepository = matherialKoefRepository;
        this.matherialPriceRepository = matherialPriceRepository;
        this.matherialNdsRepository = matherialNdsRepository;
    }

    @Override
    public List<MatherialListItem> getMatherials() {
        return matherialRepository.getMatherialListItems();
    }

    @Override
    public List<MatherialListItem> getMatherials(Date periodBegin, Date periodEnd, boolean all) {
        return matherialRepository.getMatherialListItems(periodBegin, periodEnd, all);
    }

    @Override
    public String getCodeMatherial(int id) {
        return matherialRepository.getCodeMatherial(id);
    }

    @Override
    public Matherial getMatherial(int id) {
        Matherial matherial = matherialRepository.getMatherial(id);
        if (matherial != null) {
            matherial.setPriceList(matherialPriceRepository.getPriceList(id));
            matherial.setKoefList(matherialKoefRepository.getKoefList(id));
            matherial.setNdsList(matherialNdsRepository.getNdsList(id));
        }
        return matherial;
    }

    @Override
    public int saveMatherial(Matherial matherial, boolean full) {
        matherial.setId(matherialRepository.saveMatherial(matherial));
        if (full) {
            matherialPriceRepository.savePrice(matherial);
            matherialKoefRepository.saveKoef(matherial);
            matherialNdsRepository.saveNds(matherial);
        }
        return matherial.getId();
    }

    @Override
    public boolean deleteMatherial(int id) {
        return matherialRepository.deleteMatherial(id);
    }

    @Override
    public List<MatherialKoef> getMatherialKoefList(int idMatherial) {
        return matherialKoefRepository.getKoefList(idMatherial);
    }

    @Override
    public List<MatherialPrice> getMatherialPriceList(int idMatherial) {
        return matherialPriceRepository.getPriceList(idMatherial);
    }

    @Override
    public List<MatherialNds> getMatherialNdsList(int idMatherial) {
        return matherialNdsRepository.getNdsList(idMatherial);
    }

    @Override
    public int saveMatherialPrice(MatherialPrice matherialPrice) {
        matherialPrice.setCodeMatherial(getCodeMatherial(matherialPrice.getIdMatherial()));
        return matherialPriceRepository.savePrice(matherialPrice);
    }

    public int saveMatherialKoef(MatherialKoef matherialKoef) {
        matherialKoef.setCodeMatherial(getCodeMatherial(matherialKoef.getIdMatherial()));
        return matherialKoefRepository.saveKoef(matherialKoef);
    }

    @Override
    public int saveMatherialNds(MatherialNds matherialNds) {
        matherialNds.setCodeMatherial(getCodeMatherial(matherialNds.getIdMatherial()));
        return matherialNdsRepository.saveNds(matherialNds);
    }

    @Override
    public boolean deleteMatherialPrice(int idMatherial) {
        return matherialPriceRepository.deletePrice(idMatherial);
    }

    @Override
    public boolean deleteMatherialKoef(int idMatherial) {
        return matherialKoefRepository.deleteKoef(idMatherial);
    }

    @Override
    public boolean deleteMatherialNds(int idMatherial) {
        return matherialNdsRepository.deleteNds(idMatherial);
    }

    @Override
    public boolean deleteMatherialPriceItem(int idMatherialPrice) {
        return matherialPriceRepository.deletePriceItem(idMatherialPrice);
    }

    @Override
    public boolean deleteMatherialKoefItem(int idMatherialKoef) {
        return matherialKoefRepository.deleteKoefItem(idMatherialKoef);
    }

    @Override
    public boolean deleteMatherialNdsItem(int idMatherialNds) {
        return matherialNdsRepository.deleteNdsItem(idMatherialNds);
    }
}
