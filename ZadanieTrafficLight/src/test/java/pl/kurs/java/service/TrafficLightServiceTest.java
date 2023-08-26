package pl.kurs.java.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.kurs.java.interfaces.*;


public class TrafficLightServiceTest {
    private DistanceSensor distanceSensor;
    private SpeedSensor speedSensor;
    private LightController lightController;
    private PedestrianSensor pedestrianSensor;
    private EmergencyModeController emergencyModeController;
    private PrivilegedVehicleSensor privilegedVehicleSensor;
    private WeatherSensor weatherSensor;
    private PedestrianButton pedestrianButton;
    private TemperatureSensor temperatureSensor;
    private TrafficLightService service;

    @Before
    public void init() {
        distanceSensor = Mockito.mock(DistanceSensor.class);
        speedSensor = Mockito.mock(SpeedSensor.class);
        lightController = Mockito.mock(LightController.class);
        pedestrianSensor = Mockito.mock(PedestrianSensor.class);
        emergencyModeController = Mockito.mock(EmergencyModeController.class);
        privilegedVehicleSensor = Mockito.mock(PrivilegedVehicleSensor.class);
        weatherSensor = Mockito.mock(WeatherSensor.class);
        pedestrianButton = Mockito.mock(PedestrianButton.class);
        temperatureSensor = Mockito.mock(TemperatureSensor.class);
        service = new TrafficLightService(distanceSensor, speedSensor, lightController, pedestrianSensor,
                emergencyModeController, privilegedVehicleSensor, weatherSensor, pedestrianButton, temperatureSensor);
    }

    @Test
    public void shouldTurnOnRedLightWhenSpeedAbove40AndDistanceLessThan50() {
        Mockito.when(distanceSensor.getDistance()).thenReturn(40.0);
        Mockito.when(speedSensor.getSpeed()).thenReturn(45.0);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).turnOnRed();
    }

    @Test
    public void shouldNotTurnOnRedLightWhenSpeedBelow40() {
        Mockito.when(distanceSensor.getDistance()).thenReturn(40.0);
        Mockito.when(speedSensor.getSpeed()).thenReturn(35.0);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController, Mockito.never()).turnOnRed();
    }

    @Test
    public void shouldTurnOnRedLightWhenCrowdDetected() {
        Mockito.when(pedestrianSensor.isCrowdDetected()).thenReturn(true);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).turnOnRed();
    }

    @Test
    public void shouldTurnOnRedLightInEmergencyMode() {
        Mockito.when(emergencyModeController.isInEmergencyMode()).thenReturn(true);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).turnOnRed();
    }

    @Test
    public void shouldTurnOnFlashYellowBetween23And5() {
        service.currentHour = 24;

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).turnOnFlashYellow();
    }

    @Test
    public void shouldTurnOnFlashYellowIfMotionIsNotDetected() {
        Mockito.when(distanceSensor.getDistance()).thenReturn(1100.0);
        Mockito.when(pedestrianSensor.isCrowdDetected()).thenReturn(false);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).turnOnFlashYellow();
    }

    @Test
    public void shouldNotTurnOnFlashYellowIfMotionIsNotDetected() {
        Mockito.when(distanceSensor.getDistance()).thenReturn(50.0);
        Mockito.when(pedestrianSensor.isCrowdDetected()).thenReturn(false);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController, Mockito.never()).turnOnFlashYellow();
    }

    @Test
    public void shouldIncreaseBrightnessIfRainyOrFoggy() {
        Mockito.when(weatherSensor.isFogDetected()).thenReturn(true);
        Mockito.when(weatherSensor.isRainDetected()).thenReturn(true);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).increaseBrightness();
    }

    @Test
    public void shouldIncreaseBrightnessIfIsOnlyRainy() {
        Mockito.when(weatherSensor.isRainDetected()).thenReturn(true);
        Mockito.when(weatherSensor.isFogDetected()).thenReturn(false);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).increaseBrightness();
    }

    @Test
    public void shouldShortenWaitingTimeForGreenLight() {
        Mockito.when(pedestrianButton.isPressed()).thenReturn(true);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).shortenRedForPedestrian();
    }

    @Test
    public void waitingTimeForGreenLightShouldBeNormal() {
        Mockito.when(pedestrianButton.isPressed()).thenReturn(false);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController, Mockito.never()).shortenRedForPedestrian();
    }

    @Test
    public void shouldExtendingTheYellowLightIfTempIsBelowZero() {
        Mockito.when(temperatureSensor.isBelowZero()).thenReturn(-2);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController).extendingTheYellowLight();
    }

    @Test
    public void shouldNotExtendingTheYellowLightIfTempIsUnderZero() {
        Mockito.when(temperatureSensor.isBelowZero()).thenReturn(5);

        service.checkAndControlTrafficLight();

        Mockito.verify(lightController, Mockito.never()).extendingTheYellowLight();
    }
}