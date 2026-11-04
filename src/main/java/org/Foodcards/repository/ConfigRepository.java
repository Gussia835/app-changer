package org.Foodcards.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerConfig;

import java.util.List;

@ApplicationScoped
public class ConfigRepository implements PanacheRepository<AppChangerConfig> {

    public List<AppChangerConfig> findActiveConfigs() {
        return find("statusIn", 1).list();
    }

    public List<AppChangerConfig> findBySystemId(String systemId) {
        return find("systemId", systemId).list();
    }

    @Transactional
    public void saveOrUpdate(AppChangerConfig config) {
        persist(config);
    }
}
