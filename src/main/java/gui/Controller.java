package gui;

import gui.view.SimulationFrame;
import gui.view.SimulationSetupFrame;
import logic.SimulationManager;
import logic.SimulationUpdate;
import utils.*;

import javax.swing.*;

public class Controller implements SimulationUpdate {
    private SimulationSetupFrame setupFrame;
    private SimulationFrame simulationFrame;
    private SimulationManager manager;

    public Controller(SimulationSetupFrame frame, SimulationManager manager) {
        this.setupFrame = frame;
        this.manager = manager;
    }

    private void validateNumberOfClients(String numberOfClientsStr)
                                    throws InvalidNumberOfClientsException, InvalidDataException {
        if(numberOfClientsStr.isEmpty()){
            throw new InvalidDataException("All fields must be filled!");
        }

        int numberOfClients;
        try {
            numberOfClients = Integer.parseInt(numberOfClientsStr);
        } catch (NumberFormatException e) {
            throw new InvalidNumberOfClientsException("The number of clients must be an integer!");
        }

        if (numberOfClients <= 0) {
            throw new InvalidNumberOfClientsException("The number of clients must be greater than zero!");
        }
    }

    private void validateNumberOfQueues(String numberOfQueuesStr)
                            throws InvalidNumberOfQueuesException, InvalidDataException {
        if(numberOfQueuesStr.isEmpty()){
            throw new InvalidDataException("All fields must be filled!");
        }

        int numberOfQueues;
        try {
            numberOfQueues = Integer.parseInt(numberOfQueuesStr);
        } catch (NumberFormatException e) {
            throw new InvalidNumberOfQueuesException("The number of queues must be an integer!");
        }

        if (numberOfQueues <= 0) {
            throw new InvalidNumberOfQueuesException("The number of queues must be greater than zero!");
        }
    }

    private void validateArrivalTime(String minimumArrivalTimeStr, String maximumArrivalTimeStr,
               int simulationInterval) throws InvalidArrivalTimeException, InvalidDataException {
        if(minimumArrivalTimeStr.isEmpty() || maximumArrivalTimeStr.isEmpty()){
            throw new InvalidDataException("All fields must be filled!");
        }

        int minimumArrivalTime, maximumArrivalTime;
        try {
            minimumArrivalTime = Integer.parseInt(minimumArrivalTimeStr);
            maximumArrivalTime = Integer.parseInt(maximumArrivalTimeStr);
        } catch (NumberFormatException e) {
            throw new InvalidArrivalTimeException("The minimum/maximum arrival time must be an integer!");
        }

        if (minimumArrivalTime > maximumArrivalTime) {
            throw new InvalidArrivalTimeException("The minimum arrival time must be less or " +
                    "equal to maximum arrival time!");
        }

        if (minimumArrivalTime <= 0) {
            throw new InvalidArrivalTimeException("The minimum arrival time must be greater than zero!");
        }

        if (maximumArrivalTime >= simulationInterval) {
            throw new InvalidArrivalTimeException("The maximum arrival time has to be less than the " +
                    "simulation interval!");
        }
    }

    private void validateServiceTime(String minimumServiceTimeStr, String maximumServiceTimeStr,
            int simulationInterval) throws InvalidServiceTimeException, InvalidDataException {
        if(minimumServiceTimeStr.isEmpty() || maximumServiceTimeStr.isEmpty()){
            throw new InvalidDataException("All fields must be filled!");
        }

        int minimumServiceTime, maximumServiceTime;
        try {
            minimumServiceTime = Integer.parseInt(minimumServiceTimeStr);
            maximumServiceTime = Integer.parseInt(maximumServiceTimeStr);
        } catch (NumberFormatException e) {
            throw new InvalidServiceTimeException("The minimum/maximum service time must be an integer!");
        }

        if (minimumServiceTime > maximumServiceTime) {
            throw new InvalidServiceTimeException("The minimum service time has to be less than or equal " +
                    "to maximum service time!");
        }

        if (minimumServiceTime <= 0) {
            throw new InvalidServiceTimeException("The minimum service time must be greater than zero!");
        }

        if (maximumServiceTime >= simulationInterval) {
            throw new InvalidServiceTimeException("The maximum service time has to be less than the " +
                    "simulation interval!");
        }
    }

    private void validateTimeIntervals(String simulationIntervalStr, String minimumArrivalTimeStr,
                                       String maximumArrivalTimeStr, String minimumServiceTimeStr,
                                       String maximumServiceTimeStr)
     throws InvalidSimulationIntervalException, InvalidArrivalTimeException, InvalidServiceTimeException,
                                                                                InvalidDataException {
        if(simulationIntervalStr.isEmpty()) {
            throw new InvalidDataException("All fields must be filled!");
        }

        int simulationInterval;
        try {
            simulationInterval = Integer.parseInt(simulationIntervalStr);
        } catch (NumberFormatException e) {
            throw new InvalidSimulationIntervalException("The simulation interval must be an integer!");
        }

        if (simulationInterval <= 0) {
            throw new InvalidSimulationIntervalException("The simulation interval must be greater than zero!");
        }
        validateArrivalTime(minimumArrivalTimeStr, maximumArrivalTimeStr, simulationInterval);
        validateServiceTime(minimumServiceTimeStr, maximumServiceTimeStr, simulationInterval);
    }

    public void validateData(String numberOfClientsStr,
                             String numberOfQueuesStr,
                             String simulationIntervalStr,
                             String minimumArrivalTimeStr,
                             String maximumArrivalTimeStr,
                             String minimumServiceTimeStr,
                             String maximumServiceTimeStr) {
        try {
            validateNumberOfClients(numberOfClientsStr);
            validateNumberOfQueues(numberOfQueuesStr);
            validateTimeIntervals(simulationIntervalStr, minimumArrivalTimeStr, maximumArrivalTimeStr,
                                                        minimumServiceTimeStr, maximumServiceTimeStr);
            JOptionPane.showMessageDialog(setupFrame,
                                "Data is valid!",
                                    "Validation Successful",
                                        JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(setupFrame, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int numberOfClients = Integer.parseInt(numberOfClientsStr);
        int numberOfQueues = Integer.parseInt(numberOfQueuesStr);
        int simulationInterval = Integer.parseInt(simulationIntervalStr);
        int minimumArrivalTime = Integer.parseInt(minimumArrivalTimeStr);
        int maximumArrivalTime = Integer.parseInt(maximumArrivalTimeStr);
        int minimumServiceTime = Integer.parseInt(minimumServiceTimeStr);
        int maximumServiceTime = Integer.parseInt(maximumServiceTimeStr);

        manager.setInputData(numberOfClients, numberOfQueues, simulationInterval, minimumArrivalTime,
                                        maximumArrivalTime, minimumServiceTime, maximumServiceTime);
        manager.generateData();
        setupFrame.showSimulationPanel(manager);
    }

    // Metoda ce pornește simularea și afișează SimulationFrame
    public void startSimulation() {
        manager.setUpdateListener(this);
        Thread simulationThread = new Thread(manager);
        simulationThread.start();

        simulationFrame = new SimulationFrame("Simulation View", manager.getGeneratedServers()); // referință stocată aici
        setupFrame.dispose();
        SwingUtilities.invokeLater(() -> simulationFrame.setVisible(true));

        // Inițial update la Time 0
        SwingUtilities.invokeLater(() ->
                simulationFrame.updateQueues(manager.getGeneratedServers(), manager.getCurrentTime())
        );
    }

    @Override
    public void onSimulationUpdated(int currentTime) {
        SwingUtilities.invokeLater(() ->
                simulationFrame.updateQueues(manager.getGeneratedServers(), currentTime)
        );
    }

    @Override
    public void onSimulationEnded() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Simulation ended!", "End", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

}
