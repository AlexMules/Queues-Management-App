package logic;

import model.Client;
import model.Server;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

public class SimulationManager implements Runnable, ClientCompletion {
    private ArrayList<Client> clients;
    private ArrayList<Server> servers;
    private int simulationInterval;
    private SimulationClock clock;

    private final Generator generator;
    private final Scheduler scheduler;
    private CyclicBarrier barrier; // Bariera pentru sincronizarea tick-urilor

    // Lista thread-safe pentru waiting times
    private final Queue<Integer> waitingTimes = new ConcurrentLinkedQueue<>();
    private final Queue<Integer> serviceTimes = new ConcurrentLinkedQueue<>();

    private int peakTime = 0;
    private int maxWaitingCount = 0;

    private SimulationUpdate updateListener;

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
        this.clock = new SimulationClock(simulationInterval);
        // Bariera are numărul de servere + 1 (pentru SimulationManager)
        this.barrier = new CyclicBarrier(numberOfQueues + 1);
    }

    private void setServersForScheduler(List<Server> servers) {
        scheduler.setServers(servers);
    }

    private void startAllServers(List<Server> servers) {
        for (Server server : servers) {
            new Thread(server).start();
        }
    }

    public void generateData() {
        this.clients = generator.generateRandomClients();
        // Transmiterea referinței la ClientCompletionListener (this) către servere
        this.servers = generator.generateServers(clock, barrier, (ClientCompletion) this);
        setServersForScheduler(servers);
        startAllServers(servers);
    }

    private ArrayList<Client> getReadyClients() {
        ArrayList<Client> readyClients = new ArrayList<>();
        for (Client client : new ArrayList<>(clients)) {
            if (client.getArrivalTime() <= clock.getCurrentTime()) {
                readyClients.add(client);
            }
        }
        return readyClients;
    }

    private void updateMaxWaitingCount() {
        int waitingCount = 0;
        for (Server server : servers) {
            waitingCount += server.getClients().size(); // only the waiting queue, not the processing client
        }
        if (waitingCount > maxWaitingCount) {
            maxWaitingCount = waitingCount;
            peakTime = clock.getCurrentTime();
        }
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("log_of_events.txt", false))) {
            // 🔹 Afișează starea inițială - Time 0
            String initialLog = buildLog();
            writer.println(initialLog);
            writer.flush();

            // 🔁 Bucla principală
            while (clock.hasNextTick()) {
                // 🔹 Tick (avansează timpul) și notifică serverele
                synchronized (clock.getLock()) {
                    clock.tick();
                }

                // 🔹 Preia clienții care au sosit la timpul curent
                ArrayList<Client> readyClients = getReadyClients();
                clients.removeAll(readyClients);
                for (Client client : readyClients) {
                    scheduler.dispatchClient(client);
                }

                // 🔹 Așteaptă ca toate thread-urile (serverele) să proceseze tick-ul
                try {
                    barrier.await();
                    updateMaxWaitingCount();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    ex.printStackTrace();
                    break;
                }

                // 🔹 Scrie log-ul pentru timpul curent
                String log = buildLog();
                writer.println(log);
                writer.flush();

                // 🔹 Notifică UI-ul dacă există listener
                if (updateListener != null) {
                    updateListener.onSimulationUpdated(clock.getCurrentTime());
                }

                // 🔹 Așteaptă 1 secundă (abia la finalul iteratiei)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // 🔹 La final, scrie statistici
            double avgWaitingTime = calculateAverageTime(waitingTimes);
            double avgServiceTime = calculateAverageTime(serviceTimes);
            writer.println(String.format("Average waiting time: %.2f", avgWaitingTime));
            writer.println(String.format("Average service time: %.2f", avgServiceTime));
            writer.println(String.format("Peak hour: %d (waiting clients: %d)", peakTime, maxWaitingCount));
            writer.flush();

            if (updateListener != null) {
                updateListener.onSimulationEnded();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        stopAllServers(); // 🔚
    }


    private String buildLog() {
        StringBuilder logBuilder = new StringBuilder();
        synchronized (clock.getLock()) {
            logBuilder.append("Time ").append(clock.getCurrentTime()).append("\n");

            // Afișează clienții din waiting (cu line break după 4 clienți)
            logBuilder.append("Waiting clients: ");
            if (clients.isEmpty()) {
                logBuilder.append("none");
            } else {
                int count = 0;
                for (Client client : clients) {
                    logBuilder.append(client).append("; ");
                    count++;
                    if (count % 8 == 0) {
                        logBuilder.append("\n         ");
                    }
                }
            }
            logBuilder.append("\n");

            // Afișează starea fiecărei cozi (server)
            for (Server server : servers) {
                logBuilder.append("Queue ").append(server.getId()).append(": ");
                Client processing = server.getProcessingClient();
                if (processing != null) {
                    logBuilder.append("Processing: ").append(processing).append(" | ");
                }
                if (server.getClients().isEmpty() && processing == null) {
                    logBuilder.append("closed");
                } else {
                    int count = 0;
                    for (Client client : server.getClients()) {
                        logBuilder.append(client).append("; ");
                        count++;
                        if (count % 8 == 0) {
                            logBuilder.append("\n         ");
                        }
                    }
                }
                logBuilder.append("\n");
            }
        }
        return logBuilder.toString();
    }

    private double calculateAverageTime(Queue<Integer> listOfTimes) {
        int sum = 0;
        int count = listOfTimes.size();
        for (int wt : listOfTimes) {
            sum += wt;
        }
        if(count > 0){
            return (double) sum / count;
        }
        else {
            return 0;
        }
    }

    private void stopAllServers() {
        for (Server server : servers) {
            server.stopServer();
        }
    }

    // Implementarea interfeței ClientCompletionListener:
    @Override
    public void clientCompleted(Client client, int waitingTime) {
        waitingTimes.add(waitingTime);
        serviceTimes.add(client.getServiceTime());
    }

    public void setUpdateListener(SimulationUpdate listener) {
        this.updateListener = listener;
    }

    // Getters...
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

    public int getCurrentTime() {
        return clock.getCurrentTime();
    }
}
