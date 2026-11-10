package org.Foodcards.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerIoMsgsEntity;

@ApplicationScoped
public class IoMsgsRepository implements PanacheRepository<AppChangerIoMsgsEntity> {

    @Transactional
    public void save(AppChangerIoMsgsEntity ioMsgs) {
        persist(ioMsgs);
    }

}
