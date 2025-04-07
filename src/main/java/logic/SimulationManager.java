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
    private SimulationClock clock; // simulation clock-ul coordoneaza evolutia in timp a simularii
    private CyclicBarrier barrier; // bariera pentru sincronizarea thread-urilor in functie de tick-uri

    private final Generator generator; // genereaza random clientii si serverele (queues)
    private final Scheduler scheduler; // adauga clientii in cozile corepunzatoare

    private final Queue<Integer> waitingTimes = new ConcurrentLinkedQueue<>(); // timpii de asteptare
    private final Queue<Integer> serviceTimes = new ConcurrentLinkedQueue<>(); // timpii de service

    private int peakHour = 0; // timpul in care se afla in asteptare cei mai multi clienti
    private int maxWaitingCount = 0; // numarul de clienti care asteapta la peakHour

    private SimulationUpdate updateListener; // referinta la interfata SimulationUpdate (necesara pentru update in GUI)

    public SimulationManager() {
        this.generator = new Generator();
        this.scheduler = new Scheduler();
    }

    public void setInputData(int numberOfClients, int numberOfQueues, int simulationInterval, int minimumArrivalTime,
                             int maximumArrivalTime, int minimumServiceTime, int maximumServiceTime) {
        this.simulationInterval = simulationInterval;
        generator.setInputData(numberOfClients, numberOfQueues, minimumArrivalTime, maximumArrivalTime,
                minimumServiceTime, maximumServiceTime);
        this.clock = new SimulationClock(simulationInterval);
        // bariera are (numarul de servere) + 1 (pentru SimulationManager)
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
        this.servers = generator.generateServers(clock, barrier, this);
        setServersForScheduler(servers);
        startAllServers(servers);
    }

    private ArrayList<Client> getReadyClients() {
        ArrayList<Client> readyClients = new ArrayList<>();
        for (Client client : new ArrayList<>(clients)) {
            if (client.getArrivalTime() == clock.getCurrentTime()) {
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
            peakHour = clock.getCurrentTime();
        }
    }

    private void writeToFile(PrintWriter writer, String log) {
        writer.println(log);
        writer.flush();
    }

    private void writeStatisticsToFile(PrintWriter writer) {
        double avgWaitingTime = calculateAverageTime(waitingTimes);
        double avgServiceTime = calculateAverageTime(serviceTimes);
        writer.println(String.format("Average waiting time: %.2f", avgWaitingTime));
        writer.println(String.format("Average service time: %.2f", avgServiceTime));
        writer.println(String.format("Peak hour: %d (waiting clients: %d)", peakHour, maxWaitingCount));
        writer.flush();
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

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test_1.txt", false))) {
            // afiseaza starea initiala - Time 0
            String initialLog = buildLog();
            writeToFile(writer, initialLog);

            while (clock.hasNextTick()) {
                // tick avanseaza timpul de simulare
                synchronized (clock.getLock()) {
                    clock.tick();
                }

                // preluam clientii care au sosit la timpul curent
                ArrayList<Client> readyClients = getReadyClients();
                clients.removeAll(readyClients);// ii eliminam din waiting clients
                for (Client client : readyClients) {
                    scheduler.dispatchClient(client);
                }

                clock.notifyAllThreads(); // serverele sunt notificate

                // asteptam ca toate thread-urile sa proceseze tick-ul
                try {
                    barrier.await();
                    updateMaxWaitingCount(); // se recalculeaza peakHour si maxWaitingCount
                } catch (InterruptedException | BrokenBarrierException ex) {
                    ex.printStackTrace();
                    break;
                }

                // scriem log-ul pentru timpul curent al simularii
                String log = buildLog();
                writeToFile(writer, log);

                // notifica UI-ul
                if (updateListener != null) {
                    updateListener.onSimulationUpdated(clock.getCurrentTime());
                }

                // asteapta 1 secunda
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // scrie statisticile
            writeStatisticsToFile(writer);

            if (updateListener != null) {
                updateListener.onSimulationEnded();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        stopAllServers();
    }


    private String buildLog() {
        StringBuilder logBuilder = new StringBuilder();
        synchronized (clock.getLock()) {
            logBuilder.append("Time ").append(clock.getCurrentTime()).append("\n");

            // afiseaza waiting clients
            logBuilder.append("Waiting clients: ");
            if (clients.isEmpty()) {
                logBuilder.append("none");
            } else {
                int count = 0;
                for (Client client : clients) {
                    logBuilder.append(client).append("; ");
                    count++;
                    if (count % 7 == 0) {
                        logBuilder.append("\n         ");
                    }
                }
            }
            logBuilder.append("\n");

            // afiseaza fiecare server (queue)
            for (Server server : servers) {
                logBuilder.append("Queue ").append(server.getId()).append(": ");
                Client processingClient = server.getProcessingClient();
                if (processingClient != null) {
                    logBuilder.append("Processing: ").append(processingClient).append(" | ");
                }
                if (server.isQueueEmpty()) {
                    logBuilder.append("closed");
                } else {
                    int count = 0;
                    for (Client client : server.getClients()) {
                        logBuilder.append(client).append("; ");
                        count++;
                        if (count % 7 == 0) {
                            logBuilder.append("\n         ");
                        }
                    }
                }
                logBuilder.append("\n");
            }
        }
        logBuilder.append("\n");
        logBuilder.append("-----------------------------------------------------------------------------------------" +
                                                                                    "-----------------------------\n");
        return logBuilder.toString();
    }

    private void stopAllServers() {
        for (Server server : servers) {
            server.stopServer();
        }
    }

    // implementarea interfetei ClientCompletionListener
    @Override
    public void clientCompleted(Client client, int waitingTime) {
        waitingTimes.add(waitingTime);
        serviceTimes.add(client.getServiceTime());
    }

    public void setUpdateListener(SimulationUpdate listener) {
        this.updateListener = listener;
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
