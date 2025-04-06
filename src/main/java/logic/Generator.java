package logic;

import model.Client;
import model.Server;

import java.util.*;
import java.util.concurrent.CyclicBarrier;

//genereaza random clienti si cozi
public class Generator {

    private int numberOfClients;
    private int numberOfQueues;

    private int minimumArrivalTime;
    private int maximumArrivalTime;

    private int minimumServiceTime;
    private int maximumServiceTime;

    public Generator() {
        this.numberOfClients = 0;
        this.numberOfQueues = 0;

        this.minimumArrivalTime = 0;
        this.maximumArrivalTime = 0;

        this.minimumServiceTime = 0;
        this.maximumServiceTime = 0;
    }

    //datele primite in GUI vor fi validate de Controller
    public void setInputData(int numberOfClients, int numberOfQueues, int minimumArrivalTime,
                             int maximumArrivalTime, int minimumServiceTime, int maximumServiceTime) {
        this.numberOfClients = numberOfClients;
        this.numberOfQueues = numberOfQueues;
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

    //metoda care genereaza random clienti pe baza datelor de intrare
    public ArrayList<Client> generateRandomClients() {
        Random random = new Random(); //folosim un obiect de tipul Random
        ArrayList<Client> generatedClients = new ArrayList<>();
        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = random.nextInt(maximumArrivalTime - minimumArrivalTime + 1) + minimumArrivalTime;
            int serviceTime = random.nextInt(maximumServiceTime - minimumServiceTime + 1) + minimumServiceTime;
            Client client = new Client(i, arrivalTime, serviceTime);
            generatedClients.add(client);
        }

        //sorteaza lista in functie de arrival time
        sortList(generatedClients);
        return generatedClients;
    }

    //metoda care genereaza servers (queues)
    public ArrayList<Server> generateServers(SimulationClock clock, CyclicBarrier barrier, ClientCompletion listener) {
        ArrayList<Server> generatedServers = new ArrayList<>();
        for (int i = 1; i <= numberOfQueues; i++) {
            generatedServers.add(new Server(i, clock, barrier, listener));
        }
        return generatedServers;
    }


    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
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
