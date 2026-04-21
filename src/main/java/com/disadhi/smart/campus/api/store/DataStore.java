/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.disadhi.smart.campus.api.store;

/**
 *
 * @author disadhiranasinghe
 */
import com.disadhi.smart.campus.api.model.Room;
import com.disadhi.smart.campus.api.model.Sensor;
import com.disadhi.smart.campus.api.model.SensorReading;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    private static final Map<String, Room> rooms = new LinkedHashMap<>();
    private static final Map<String, Sensor> sensors = new LinkedHashMap<>();
    private static final Map<String, List<SensorReading>> sensorReadings = new LinkedHashMap<>();

    private DataStore() {
    }

    public static List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public static Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public static void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public static boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    public static Room removeRoom(String roomId) {
        return rooms.remove(roomId);
    }

    public static List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    public static Sensor getSensor(String sensorId) {
        return sensors.get(sensorId);
    }

    public static boolean sensorExists(String sensorId) {
        return sensors.containsKey(sensorId);
    }

    public static void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);

        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            List<String> sensorIds = room.getSensorIds();
            if (sensorIds == null) {
                sensorIds = new ArrayList<>();
                room.setSensorIds(sensorIds);
            }

            if (!sensorIds.contains(sensor.getId())) {
                sensorIds.add(sensor.getId());
            }
        }

        if (!sensorReadings.containsKey(sensor.getId())) {
            sensorReadings.put(sensor.getId(), new ArrayList<>());
        }
    }

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        List<SensorReading> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
            sensorReadings.put(sensorId, readings);
        }
        return readings;
    }

    public static void addReading(String sensorId, SensorReading reading) {
        List<SensorReading> readings = getReadingsForSensor(sensorId);
        readings.add(reading);

        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(reading.getValue());
        }
    }
}