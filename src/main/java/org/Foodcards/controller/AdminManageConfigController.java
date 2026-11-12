package org.Foodcards.controller;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.Foodcards.models.AppChangerConfigEntity;
import org.Foodcards.repository.ConfigRepository;
import org.Foodcards.service.StreamManager;

import java.util.List;

@Path("/admin/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminManageConfigController {
    @Inject
    ConfigRepository configRepository;

    @Inject
    StreamManager streamManager;

    @GET
    public Response getAllConfigs() {
        List<AppChangerConfigEntity> configs = configRepository.findAll().list();
        return Response.ok(configs).build();
    }

    @PUT
    @Path("/{id}/status-in/{newStatus}")
    @Transactional
    public Response toggleStatusIn(@PathParam("id") Long id, @PathParam("newStatus") Integer newStatus) {
        AppChangerConfigEntity config = configRepository.findById(id);
        if (config == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        config.setStatusIn(newStatus);
        configRepository.persist(config);

        Log.infof("admin: STATUS_IN для %s изменен на %d", config.getStreamId(), newStatus);

        streamManager.syncStreams();

        return Response
                .ok()
                .entity(config)
                .build();
    }

    @PUT
    @Path("/{id}/toggle-out/{newStatus}")
    @Transactional
    public Response toggleStatusOut(@PathParam("id") Long id, @PathParam("newStatus")Integer newStatus) {
        AppChangerConfigEntity config = configRepository.findById(id);

        if (config == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        config.setStatusOut(newStatus);
        configRepository.persist(config);

        Log.infof("admin: STATUS_OUT для %s изменен на %d", config.getStreamId(), newStatus);

        streamManager.syncStreams();

        return Response.
                ok()
                .entity(config)
                .build();
    }

    @POST
    @Transactional
    public Response createConfig(AppChangerConfigEntity newConfig) {
        if (newConfig.getSystemId() == null || newConfig.getEventType() == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST).entity("systemId и eventType обязательны")
                    .build();
        }

        configRepository.persist(newConfig);

        Log.infof("admin: Добавлена новая конфигурация для %s", newConfig.getStreamId());

        if (newConfig.getStatusIn() != null && newConfig.getStatusIn() == 1) {
            streamManager.syncStreams();
        }

        return Response
                .status(Response.Status.CREATED)
                .entity(newConfig)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteConfig(@PathParam("id") Long id) {
        boolean deleted = configRepository.deleteById(id);

        if (!deleted) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        Log.infof("ADMIN API: Конфигурация с ID %d удалена", id);

        streamManager.syncStreams();

        return Response
                .noContent()
                .build();
    }
}
