package org.Foodcards.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerIoMsgs;

@ApplicationScoped
public class IoMsgsRepository implements PanacheRepository<AppChangerIoMsgs> {

    @Transactional
    public void save(AppChangerIoMsgs ioMsgs) {
        persist(ioMsgs);
    }

}
