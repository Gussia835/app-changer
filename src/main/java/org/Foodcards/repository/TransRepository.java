package org.Foodcards.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerTransEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.Foodcards.utils.Constants.*;

@ApplicationScoped
public class TransRepository implements PanacheRepository<AppChangerTransEntity> {

    @Transactional
    public AppChangerTransEntity save(AppChangerTransEntity trans) {
        persist(trans);
        return trans;
    }

    public List<AppChangerTransEntity> findByStatus(String status) {
        return find("status", status).list();
    }

    public List<AppChangerTransEntity> findNewTransactions(int limit) {
        return find("status", STATUS_NEW)
                .page(0, limit)
                .list();
    }

    public List<AppChangerTransEntity> findDoneTransactions(int limit) {
        return find("status", STATUS_DONE)
                .page(0, limit)
                .list();
    }

    public List<AppChangerTransEntity> findStuckTransactions(int minutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutes);
        return getEntityManager()
                .createQuery(
                        "SELECT t FROM AppChangerTrans t WHERE t.status = :status AND t.updTs < :threshold",
                        AppChangerTransEntity.class
                )
                .setParameter("status", STATUS_PROCESSING)
                .setParameter("threshold", threshold)
                .getResultList();
    }
}
