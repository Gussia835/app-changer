package org.Foodcards.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerConfigEntity;

import java.util.List;

@ApplicationScoped
public class ConfigRepository implements PanacheRepository<AppChangerConfigEntity> {

    public List<AppChangerConfigEntity> findActiveConfigs() {
        return find("statusIn", 1).list();
    }

    public List<AppChangerConfigEntity> findBySystemId(String systemId) {
        return find("systemId", systemId).list();
    }

    @Transactional
    public void saveOrUpdate(AppChangerConfigEntity config) {
        persist(config);
    }
}
