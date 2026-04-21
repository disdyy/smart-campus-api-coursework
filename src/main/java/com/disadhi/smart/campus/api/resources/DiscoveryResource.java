package com.disadhi.smart.campus.api.resources;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getApiInfo() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("name", "Disadhi's Smart Campus Sensor & Room Management API");
        response.put("version", "v1");
        response.put("contact", "w2119673@westminster.ac.uk");

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("self", "/api/v1");
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        response.put("resources", resources);
        response.put("description", "REST API for managing rooms, sensors and sensor readings in the Smart Campus system.");

        return Response.ok(response).build();
    }
}