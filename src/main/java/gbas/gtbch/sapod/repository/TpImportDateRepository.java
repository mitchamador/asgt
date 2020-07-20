package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.TpImportDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TpImportDateRepository extends JpaRepository<TpImportDate, Integer> {
    TpImportDate findFirstByOrderByDateImportDesc();
}
