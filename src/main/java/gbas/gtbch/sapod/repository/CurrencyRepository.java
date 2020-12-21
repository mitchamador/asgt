package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    /**
     *
     * @param shortName
     * @return
     */
    List<Currency> findAllByShortName(String shortName);

    /**
     *
     * @param shortName
     */
    void deleteAllByShortName(String shortName);

    /**
     *
     * @param emptyString
     * @return
     */
    List<Currency> findAllByShortNameIsNotNullAndShortNameNot(String emptyString);
}