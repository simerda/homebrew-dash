package cz.jansimerda.homebrewdash.business;

import cz.jansimerda.homebrewdash.exception.internal.meross.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MerossService {

    private final String COMMAND_ON = "ON";
    private final String COMMAND_OFF = "OFF";
    private final String SCRIPT_PATH = "src/main/python/meross.py";

    /**
     * Tries to connect to a given meross and turn a device ON
     *
     * @param email      email of the meross account
     * @param password   password of the meross account
     * @param deviceName name of the device
     * @throws GeneralMerossException               if a general error occurred when calling the internal script
     * @throws AuthenticationMissingMerossException if email or password was not provided to the internal script
     * @throws DeviceNameMissingMerossException     if the device name was not provided to the internal script
     * @throws InvalidCommandMerossException        if the requested command to be sent is not valid
     * @throws InvalidCredentialsMerossException    if the provided meross credentials are incorrect
     * @throws DeviceNotFoundMerossException        if the meross device cannot be found in given account
     * @throws DeviceOfflineMerossException         if the meross device is offline
     */
    public void turnOn(String email, String password, String deviceName) throws MerossException {
        sendCommand(email, password, deviceName, COMMAND_ON);
    }

    /**
     * Tries to connect to a given meross and turn a device OFF
     *
     * @param email      email of the meross account
     * @param password   password of the meross account
     * @param deviceName name of the device
     * @throws GeneralMerossException               if a general error occurred when calling the internal script
     * @throws AuthenticationMissingMerossException if email or password was not provided to the internal script
     * @throws DeviceNameMissingMerossException     if the device name was not provided to the internal script
     * @throws InvalidCommandMerossException        if the requested command to be sent is not valid
     * @throws InvalidCredentialsMerossException    if the provided meross credentials are incorrect
     * @throws DeviceNotFoundMerossException        if the meross device cannot be found in given account
     * @throws DeviceOfflineMerossException         if the meross device is offline
     */
    public void turnOff(String email, String password, String deviceName) throws MerossException {
        sendCommand(email, password, deviceName, COMMAND_OFF);
    }

    /**
     * Tries to connect to a given meross account and execute a command for given device
     *
     * @param email      email of the meross account
     * @param password   password of the meross account
     * @param deviceName name of the device
     * @param command    command to be executed; either ON to switch the device on or OFF to switch the device off
     * @throws GeneralMerossException               if a general error occurred when calling the internal script
     * @throws AuthenticationMissingMerossException if email or password was not provided to the internal script
     * @throws DeviceNameMissingMerossException     if the device name was not provided to the internal script
     * @throws InvalidCommandMerossException        if the requested command to be sent is not valid
     * @throws InvalidCredentialsMerossException    if the provided meross credentials are incorrect
     * @throws DeviceNotFoundMerossException        if the meross device cannot be found in given account
     * @throws DeviceOfflineMerossException         if the meross device is offline
     */
    protected void sendCommand(String email, String password, String deviceName, String command) throws MerossException {

        String path = new FileSystemResource(SCRIPT_PATH).getPath();
        ProcessBuilder processBuilder = new ProcessBuilder("python3", path, command);
        processBuilder.redirectErrorStream(true);

        Map<String, String> env = processBuilder.environment();
        env.clear();
        env.put("MSS_EMAIL", email);
        env.put("MSS_PASS", password);
        env.put("MSS_DEVICE_NAME", deviceName);

        String generalErrorMessage = "An unspecified error occurred while using the Meross service";
        Process process = null;
        try {
            process = processBuilder.start();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                throw new GeneralMerossException("Timed-out when using the Meross service");
            }
        } catch (IOException | InterruptedException e) {
            throw new GeneralMerossException(generalErrorMessage);
        }

        int exitCode = process.exitValue();
        String output;
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\t")).trim();
        } catch (IOException e) {
            output = generalErrorMessage;
        }

        switch (exitCode) {
            case 0:
                return;
            case 100:
                throw new AuthenticationMissingMerossException(output);
            case 101:
                throw new DeviceNameMissingMerossException(output);
            case 102, 103:
                throw new InvalidCommandMerossException(output);
            case 104:
                throw new InvalidCredentialsMerossException(output);
            case 105:
                throw new DeviceNotFoundMerossException(output);
            case 106:
                throw new DeviceOfflineMerossException(output);
            default:
                throw new GeneralMerossException(output);
        }
    }

}
