package io.agrisense.adapter.in.web.controller;

import java.util.List;

import io.agrisense.adapter.in.web.dto.CreateSensorRequest;
import io.agrisense.adapter.in.web.dto.SensorResponse;
import io.agrisense.adapter.in.web.mapper.SensorWebMapper;
import io.agrisense.domain.model.Sensor;
import io.agrisense.ports.in.IManageSensorUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorController {

    private final IManageSensorUseCase manageSensorUseCase;
    private final SensorWebMapper sensorMapper;

    @Inject
    public SensorController(IManageSensorUseCase manageSensorUseCase, SensorWebMapper sensorMapper) {
        this.manageSensorUseCase = manageSensorUseCase;
        this.sensorMapper = sensorMapper;
    }

    @POST
    public Response createSensor(@Valid CreateSensorRequest req) {
        Sensor sensorDomain = sensorMapper.toDomain(req);
        if (sensorDomain == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"invalid sensor payload\"}")
                    .build();
        }
        Sensor savedSensor = manageSensorUseCase.createSensor(sensorDomain);
        SensorResponse response = sensorMapper.toResponse(savedSensor);
        response.set_links(new io.agrisense.adapter.in.web.dto.HateoasLinks()
                .addLink("self", "/api/sensors/" + savedSensor.getId())
                .addLink("all", "/api/sensors"));

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public Response getAllSensors() {
        List<Sensor> sensors = manageSensorUseCase.getAllSensors();
        List<SensorResponse> responseList = sensorMapper.toResponseList(sensors);
        for (SensorResponse resp : responseList) {
            resp.set_links(new io.agrisense.adapter.in.web.dto.HateoasLinks()
                    .addLink("self", "/api/sensors/" + resp.getId())
                    .addLink("all", "/api/sensors"));
        }
        return Response.ok(responseList).build();
    }

    @GET
    @Path("/{id}")
    public Response getSensorById( @PathParam("id") Long id) {
        Sensor sensor = manageSensorUseCase.getSensorById(id);
        return Response.ok(sensorMapper.toResponse(sensor)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateSensor(@PathParam("id") Long id, @Valid CreateSensorRequest req) {
        Sensor sensorDomain = sensorMapper.toDomain(req);
        Sensor updated = manageSensorUseCase.updateSensor(id, sensorDomain);
        SensorResponse response = sensorMapper.toResponse(updated);
        response.set_links(new io.agrisense.adapter.in.web.dto.HateoasLinks()
                .addLink("self", "/api/sensors/" + updated.getId())
                .addLink("all", "/api/sensors"));
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSensor(@PathParam("id") Long id) {
        manageSensorUseCase.deleteSensor(id);
        return Response.noContent().build();
    }
}