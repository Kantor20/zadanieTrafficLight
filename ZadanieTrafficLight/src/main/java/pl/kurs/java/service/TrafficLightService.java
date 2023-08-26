package pl.kurs.java.service;

import pl.kurs.java.interfaces.*;

import java.time.LocalTime;

public class TrafficLightService {
    private final DistanceSensor distanceSensor;
    private final SpeedSensor speedSensor;
    private final LightController lightController;
    private final PedestrianSensor pedestrianSensor;
    private final EmergencyModeController emergencyModeController;
    private final PrivilegedVehicleSensor privilegedVehicleSensor;
    private final WeatherSensor weatherSensor;
    private final PedestrianButton pedestrianButton;
    private final TemperatureSensor temperatureSensor;
    public int currentHour = LocalTime.now().getHour();

    public TrafficLightService(DistanceSensor distanceSensor, SpeedSensor speedSensor, LightController lightController,
                               PedestrianSensor pedestrianSensor, EmergencyModeController emergencyModeController,
                               PrivilegedVehicleSensor privilegedVehicleSensor, WeatherSensor weatherSensor,
                               PedestrianButton pedestrianButton, TemperatureSensor temperatureSensor) {
        this.distanceSensor = distanceSensor;
        this.speedSensor = speedSensor;
        this.lightController = lightController;
        this.pedestrianSensor = pedestrianSensor;
        this.emergencyModeController = emergencyModeController;
        this.privilegedVehicleSensor = privilegedVehicleSensor;
        this.weatherSensor = weatherSensor;
        this.pedestrianButton = pedestrianButton;
        this.temperatureSensor = temperatureSensor;
    }

    public void checkAndControlTrafficLight() {
        if (isHardWeather()) {
            lightController.increaseBrightness();
        }
        if (isButtonPressed()) {
            lightController.shortenRedForPedestrian();
        }
        if (isTemperatureBelowZero()) {
            lightController.extendingTheYellowLight();
        }
        if (isNightTime() || isEnergySavingMode()) {
            lightController.turnOnFlashYellow();
        } else if (isEmergencyMode() || isPrivilegedVehicleApproaching() || isVehicleSpeeding(distanceSensor.getDistance(),
                speedSensor.getSpeed()) || pedestrianSensor.isCrowdDetected()) {
            lightController.turnOnRed();
        } else {
            lightController.turnOffRed();
        }

    }

    private boolean isNightTime() {
        return currentHour >= 23 || currentHour < 5;
    }

    private boolean isEmergencyMode() {
        return emergencyModeController.isInEmergencyMode();
    }

    private boolean isPrivilegedVehicleApproaching() {
        return privilegedVehicleSensor.isPrivilegedVehicleApproaching();
    }

    private boolean isVehicleSpeeding(double distance, double speed) {
        return speed > 40 && distance <= 50;
    }

    private boolean isEnergySavingMode() {
        double noVehicle = 1000.0;
        boolean noPedestrians = false;

        return distanceSensor.getDistance() > noVehicle && pedestrianSensor.isCrowdDetected() == noPedestrians;
    }

    private boolean isHardWeather() {
        return weatherSensor.isRainDetected() || weatherSensor.isFogDetected();
    }

    private boolean isButtonPressed() {
        return pedestrianButton.isPressed();
    }

    private boolean isTemperatureBelowZero() {
        return temperatureSensor.isBelowZero() < 0;
    }

}