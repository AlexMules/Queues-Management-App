package logic;

public class SimulationClock {
    private int currentTime = 0;
    private final int simulationInterval;
    private final Object lock = new Object();

    public SimulationClock(int simulationInterval) {
        this.simulationInterval = simulationInterval;
    }

    public boolean hasNextTick() {
        return currentTime < simulationInterval;
    }

    // Această metodă incrementează timpul și notifică thread-urile
    public void tick() {
        synchronized(lock) {
            currentTime++;
            lock.notifyAll(); // notifică toate thread-urile care așteaptă
        }
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public Object getLock() {
        return lock;
    }
}