package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    List<Currency> findAllByShortName(String shortName);

    void deleteAllByShortName(String shortName);
}