package logic;

import model.Client;
import model.Server;

import java.util.*;

public class Generator { // generates queues and clients
    private ArrayList<Client> generatedClients;
    private ArrayList<Server> generatedServers;

    private int numberOfClients;
    private int numberOfQueues;
    private int simulationInterval;

    private int minimumArrivalTime;
    private int maximumArrivalTime;

    private int minimumServiceTime;
    private int maximumServiceTime;

    public Generator() {
        this.generatedClients = new ArrayList<Client>();
        this.generatedServers = new ArrayList<Server>();

        this.numberOfClients = 0;
        this.numberOfQueues = 0;
        this.simulationInterval = 0;

        this.minimumArrivalTime = 0;
        this.maximumArrivalTime = 0;

        this.minimumServiceTime = 0;
        this.maximumServiceTime = 0;
    }

    //datele primite vor fi validate in controller(GUI)
    public void setInputData(int numberOfClients, int numberOfQueues, int simulationInterval,
                             int minimumArrivalTime, int maximumArrivalTime, int minimumServiceTime,
                             int maximumServiceTime) {
        this.numberOfClients = numberOfClients;
        this.numberOfQueues = numberOfQueues;
        this.simulationInterval = simulationInterval;
        this.minimumArrivalTime = minimumArrivalTime;
        this.maximumArrivalTime = maximumArrivalTime;
        this.minimumServiceTime = minimumServiceTime;
        this.maximumServiceTime = maximumServiceTime;
    }

    //helper sorting function
    private void sortList(List<Client> list) {
        list.sort(new Comparator<Client> () {
            public int compare(Client c1, Client c2) {
                return Integer.compare(c1.getArrivalTime(), c2.getArrivalTime());
            }
        });
    }

    //method that generates random clients based on the input data
    private void generateRandomClients() {
        Random random = new Random();
        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = random.nextInt(maximumArrivalTime - minimumArrivalTime + 1) + minimumArrivalTime;
            int serviceTime = random.nextInt(maximumServiceTime - minimumServiceTime + 1) + minimumServiceTime;
            Client client = new Client(i, arrivalTime, serviceTime);
            generatedClients.add(client);
        }

        //sort list with respect to arrivalTime
        sortList(generatedClients);
    }

    //method that generates queues
    private void generateServers() {
        for (int i = 1; i <= numberOfQueues; i++) {
            generatedServers.add(new Server(i));
        }
    }

    public void generateData() {
        generateRandomClients();
        generateServers();
    }

    public ArrayList<Client> getGeneratedClients() {
        return generatedClients;
    }

    public ArrayList<Server> getGeneratedServers() {
        return generatedServers;
    }

    // În clasa Generator
    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;

    }
    public int getSimulationInterval() {
        return simulationInterval;
    }

    public int getMinimumArrivalTime() {
        return minimumArrivalTime;
    }

    public int getMaximumArrivalTime() {
        return maximumArrivalTime;
    }

    public int getMinimumServiceTime() {
        return minimumServiceTime;
    }

    public int getMaximumServiceTime() {
        return maximumServiceTime;
    }


}
