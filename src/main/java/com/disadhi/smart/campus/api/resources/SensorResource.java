/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.disadhi.smart.campus.api.resources;

/**
 *
 * @author disadhiranasinghe
 */
import com.disadhi.smart.campus.api.model.Sensor;
import com.disadhi.smart.campus.api.store.DataStore;
import com.disadhi.smart.campus.api.exception.LinkedResourceNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = DataStore.getAllSensors();

        if (type == null || type.trim().isEmpty()) {
            return Response.ok(sensors).build();
        }

        List<Sensor> filteredSensors = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)) {
                filteredSensors.add(sensor);
            }
        }

        return Response.ok(filteredSensors).build();
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null
                || sensor.getId() == null || sensor.getId().trim().isEmpty()
                || sensor.getType() == null || sensor.getType().trim().isEmpty()
                || sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()
                || sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {

            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Sensor id, type, status, and roomId are required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (DataStore.sensorExists(sensor.getId())) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "A sensor with this id already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (!DataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("The specified roomId does not exist.");
        }

        DataStore.addSensor(sensor);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(sensor.getId())
                .build();

        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensor(sensorId);

        if (sensor == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Sensor not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
