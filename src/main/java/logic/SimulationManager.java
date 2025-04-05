package logic;

import model.Client;
import model.Server;

import java.util.ArrayList;

public class SimulationManager implements Runnable {
    private ArrayList<Client> clients;
    private ArrayList<Server> servers;

    private int simulationInterval;

    private Generator generator;
    private Scheduler scheduler;
    public SelectionPolicy selectionPolicy;

    public SimulationManager(){
        this.generator = new Generator();
        this.scheduler = new Scheduler();
    }

    public void setInputData(int numberOfClients, int numberOfQueues, int simulationInterval,
                             int minimumArrivalTime, int maximumArrivalTime, int minimumServiceTime,
                             int maximumServiceTime) {
        this.simulationInterval = simulationInterval;
        generator.setInputData(numberOfClients, numberOfQueues, minimumArrivalTime, maximumArrivalTime,
                                                                minimumServiceTime, maximumServiceTime);
    }

    public void generateData() {
        this.clients = generator.generateRandomClients();
        this.servers = generator.generateServers();
    }

    @Override
    public void run() {
        int currentTime = 0;
        while(currentTime < simulationInterval) {
            // - iterate generated clients and pick clients that have the arrivalTime
            // equal with the currentTime
            // - sent client to queue by calling the dispatchClient method from Scheduler
            // - delete client from List
            // update UI => controller
            currentTime++;
            //wait an interval of 1 second
        }
    }

    public ArrayList<Client> getGeneratedClients() {
        return clients;
    }

    public ArrayList<Server> getGeneratedServers() {
        return servers;
    }

    public int getNumberOfClients() {
        return generator.getNumberOfClients();
    }

    public int getNumberOfQueues() {
        return generator.getNumberOfQueues();
    }

    public int getSimulationInterval() {
        return simulationInterval;
    }

    public int getMinimumArrivalTime() {
        return generator.getMinimumArrivalTime();
    }

    public int getMaximumArrivalTime() {
        return generator.getMaximumArrivalTime();
    }

    public int getMinimumServiceTime() {
        return generator.getMinimumServiceTime();
    }

    public int getMaximumServiceTime() {
        return generator.getMaximumServiceTime();
    }

}
