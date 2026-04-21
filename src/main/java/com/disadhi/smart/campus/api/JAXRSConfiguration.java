package com.disadhi.smart.campus.api;

import com.disadhi.smart.campus.api.exception.GlobalExceptionMapper;
import com.disadhi.smart.campus.api.exception.LinkedResourceNotFoundExceptionMapper;
import com.disadhi.smart.campus.api.exception.RoomNotEmptyExceptionMapper;
import com.disadhi.smart.campus.api.exception.SensorUnavailableExceptionMapper;
import com.disadhi.smart.campus.api.filter.ApiLoggingFilter;
import com.disadhi.smart.campus.api.resources.DebugResource;
import com.disadhi.smart.campus.api.resources.DiscoveryResource;
import com.disadhi.smart.campus.api.resources.RoomResource;
import com.disadhi.smart.campus.api.resources.SensorReadingResource;
import com.disadhi.smart.campus.api.resources.SensorResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class JAXRSConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        classes.add(DebugResource.class);

        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        classes.add(ApiLoggingFilter.class);

        return classes;
    }
}