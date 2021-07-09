package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.RecalcRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecalcRateRepository extends JpaRepository<RecalcRate, Integer> {
}