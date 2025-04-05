package logic;

import model.Client;
import model.Server;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class SimulationManager implements Runnable {
    private ArrayList<Client> clients;
    private ArrayList<Server> servers;
    private int simulationInterval;
    private SimulationClock clock;

    private Generator generator;
    private Scheduler scheduler;
    public SelectionPolicy selectionPolicy;

    private CyclicBarrier barrier; // Bariera pentru sincronizarea tick-urilor

    public SimulationManager() {
        this.generator = new Generator();
        this.scheduler = new Scheduler();
    }

    public void setInputData(int numberOfClients, int numberOfQueues, int simulationInterval,
                             int minimumArrivalTime, int maximumArrivalTime, int minimumServiceTime,
                             int maximumServiceTime) {
        this.simulationInterval = simulationInterval;
        generator.setInputData(numberOfClients, numberOfQueues, minimumArrivalTime, maximumArrivalTime,
                minimumServiceTime, maximumServiceTime);
        // Creează o singură instanță de clock
        this.clock = new SimulationClock(simulationInterval);
        // Creează bariera: numărul de părți = numărul de servere + 1 (pentru SimulationManager)
        this.barrier = new CyclicBarrier(numberOfQueues + 1);
    }

    public void generateData() {
        // Generează clienții
        this.clients = generator.generateRandomClients();
        // Generează serverele folosind același clock, transmitând și bariera
        this.servers = generator.generateServers(clock, barrier);
        // Setează serverele în Scheduler
        this.scheduler.setServers(servers);
        // Pornește thread-urile pentru fiecare server:
        for (Server server : servers) {
            new Thread(server).start();
        }
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("simulation_log.txt", false))) {
            while (clock.hasNextTick()) {
                // 1. Incrementează ceasul și notifică serverele
                synchronized (clock.getLock()) {
                    clock.tick();
                }

                // 2. Distribuie clienții care au sosit, pe baza noului currentTime
                ArrayList<Client> readyClients = new ArrayList<>();
                for (Client client : new ArrayList<>(clients)) {
                    if (client.getArrivalTime() <= clock.getCurrentTime()) {
                        readyClients.add(client);
                    }
                }
                clients.removeAll(readyClients);
                for (Client client : readyClients) {
                    scheduler.dispatchClient(client);
                }

                // 3. Așteaptă ca toate serverele să-și fi actualizat starea pentru acest tick
                try {
                    barrier.await();
                } catch (InterruptedException | java.util.concurrent.BrokenBarrierException ex) {
                    ex.printStackTrace();
                    break;
                }

                // 4. Generează log-ul stării curente
                StringBuilder logBuilder = new StringBuilder();
                logBuilder.append("Time ").append(clock.getCurrentTime()).append("\n");

                logBuilder.append("Waiting clients: ");
                if (clients.isEmpty()) {
                    logBuilder.append("none");
                } else {
                    for (Client client : clients) {
                        logBuilder.append(client).append("; ");
                    }
                }
                logBuilder.append("\n");

                for (Server server : servers) {
                    logBuilder.append("Queue ").append(server.getId()).append(": ");
                    Client processing = server.getCurrentClient();
                    if (processing != null) {
                        logBuilder.append("Processing: ").append(processing).append(" | ");
                    }
                    if (server.getClients().isEmpty() && processing == null) {
                        logBuilder.append("closed");
                    } else {
                        for (Client client : server.getClients()) {
                            logBuilder.append(client).append("; ");
                        }
                    }
                    logBuilder.append("\n");
                }

                writer.println(logBuilder.toString());
                writer.flush();

                // 5. Așteaptă 1 secundă înainte de următorul tick
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // La terminarea simulării, semnalăm serverelor să se oprească
        for (Server server : servers) {
            server.stopServer();
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
