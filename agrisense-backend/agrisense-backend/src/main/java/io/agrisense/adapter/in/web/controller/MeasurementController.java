package io.agrisense.adapter.in.web.controller;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.agrisense.adapter.in.web.dto.CreateMeasurementRequest;
import io.agrisense.adapter.in.web.dto.MeasurementResponse;
import io.agrisense.adapter.in.web.dto.PagedResponse;
import io.agrisense.domain.model.Measurement;
import io.agrisense.domain.model.PagedResult;
import io.agrisense.ports.in.IProcessMeasurementUseCase;
import io.agrisense.ports.in.IQueryMeasurementUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/measurements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MeasurementController {

    private final IProcessMeasurementUseCase processMeasurementUseCase;
    private final IQueryMeasurementUseCase queryMeasurementUseCase;

    @Inject
    public MeasurementController(
            IProcessMeasurementUseCase processMeasurementUseCase,
            IQueryMeasurementUseCase queryMeasurementUseCase) {
        this.processMeasurementUseCase = processMeasurementUseCase;
        this.queryMeasurementUseCase = queryMeasurementUseCase;
    }

    @POST
    public Response postMeasurement(@Valid CreateMeasurementRequest req) {
        processMeasurementUseCase.processMeasurement(
                req.getSensorId(),
                req.getValue(),
                req.getUnit() == null ? "" : req.getUnit());
        return Response.accepted()
                .entity("{\"status\": \"Measurement processed successfully\"}")
                .build();
    }

    @GET
    public Response getMeasurements(
            @QueryParam("fieldId") Long fieldId,
            @QueryParam("sensorId") Long sensorId,
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("50") int size) {

        Instant from = fromStr != null ? Instant.parse(fromStr) : null;
        Instant to = toStr != null ? Instant.parse(toStr) : null;

        PagedResult<Measurement> result = queryMeasurementUseCase.queryMeasurements(
                fieldId, sensorId, from, to, page, size);

        List<MeasurementResponse> responseList = result.getContent().stream()
                .map(m -> {
                    MeasurementResponse resp = new MeasurementResponse(
                            m.getId(),
                            m.getSensorId(),
                            m.getTimestamp(),
                            m.getValue(),
                            m.getUnit());
                    resp.set_links(new io.agrisense.adapter.in.web.dto.HateoasLinks()
                            .addLink("self", "/api/measurements/" + m.getId())
                            .addLink("sensor", "/api/sensors/" + m.getSensorId()));
                    return resp;
                })
                .collect(Collectors.toList());

        PagedResponse<MeasurementResponse> response = new PagedResponse<>(
                responseList,
                result.getPage(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());

        return Response.ok(response).build();
    }
}