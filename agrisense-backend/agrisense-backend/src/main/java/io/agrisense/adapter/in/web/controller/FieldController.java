package io.agrisense.adapter.in.web.controller;

import java.util.List;

import io.agrisense.adapter.out.persistence.entity.FieldEntity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/fields")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FieldController {

    @Inject
    EntityManager entityManager;

    @GET
    public Response getAllFields() {
        List<FieldEntity> fields = entityManager
                .createQuery("SELECT f FROM FieldEntity f", FieldEntity.class)
                .getResultList();

        var result = fields.stream().map(f -> {
            var map = new java.util.LinkedHashMap<String, Object>();
            map.put("id", f.getId());
            map.put("name", f.getName());
            map.put("location", f.getLocation());
            return map;
        }).toList();

        return Response.ok(result).build();
    }

    @POST
    @Transactional
    public Response createField(java.util.Map<String, String> body) {
        String name = body.get("name");
        String location = body.get("location");

        if (name == null || name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Field name is required\"}")
                    .build();
        }

        FieldEntity field = new FieldEntity();
        field.setName(name);
        field.setLocation(location != null ? location : "");
        entityManager.persist(field);

        var result = new java.util.LinkedHashMap<String, Object>();
        result.put("id", field.getId());
        result.put("name", field.getName());
        result.put("location", field.getLocation());

        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}
