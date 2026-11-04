package org.Foodcards.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerTrans;

import java.time.LocalDateTime;
import java.util.List;

import static org.Foodcards.utils.Constants.*;

@ApplicationScoped
public class TransRepository implements PanacheRepository<AppChangerTrans> {

    @Transactional
    public AppChangerTrans save(AppChangerTrans trans) {
        persist(trans);
        return trans;
    }

    public List<AppChangerTrans> findByStatus(String status) {
        return find("status", status).list();
    }

    public List<AppChangerTrans> findNewTransactions(int limit) {
        return find("status", STATUS_NEW)
                .page(0, limit)
                .list();
    }

    public List<AppChangerTrans> findDoneTransactions(int limit) {
        return find("status", STATUS_DONE)
                .page(0, limit)
                .list();
    }

    public List<AppChangerTrans> findStuckTransactions(int minutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutes);
        return getEntityManager()
                .createQuery(
                        "SELECT t FROM AppChangerTrans t WHERE t.status = :status AND t.updTs < :threshold",
                        AppChangerTrans.class
                )
                .setParameter("status", STATUS_PROCESSING)
                .setParameter("threshold", threshold)
                .getResultList();
    }
}
