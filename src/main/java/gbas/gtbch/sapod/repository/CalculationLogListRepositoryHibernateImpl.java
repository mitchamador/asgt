package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CalculationLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Repository
public class CalculationLogListRepositoryHibernateImpl implements CalculationLogListRepository {

    private final EntityManager em;

    public CalculationLogListRepositoryHibernateImpl(@Qualifier("sapodEntityManager") EntityManager em) {
        this.em = em;
    }

    @Override
    public List<CalculationLog> getList(Map<String, String> params, Date dateBegin, Date dateEnd) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CalculationLog> cq = cb.createQuery(CalculationLog.class);

        Root<CalculationLog> log = cq.from(CalculationLog.class);
        List<Predicate> predicates = new ArrayList<>();

        if (dateBegin != null) {
            predicates.add(cb.greaterThanOrEqualTo(log.get("inboundTime"), dateBegin));
        }

        if (dateEnd != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateEnd);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);
            predicates.add(cb.lessThanOrEqualTo(log.get("inboundTime"), c.getTime()));
        }

        if (params != null) {
            if (params.get("source") != null) {
                try {
                    predicates.add(cb.equal(log.get("source"), CalculationLog.Source.valueOf(params.get("source"))));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            if (params.get("type") != null) {
                try {
                    predicates.add(cb.equal(log.get("type"), CalculationLog.Type.valueOf(params.get("type"))));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            if (params.get("number") != null) {
                predicates.add(cb.equal(log.get("number"), params.get("number")));
            }
            if (params.get("station") != null) {
                predicates.add(cb.equal(log.get("station"), params.get("station")));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}
