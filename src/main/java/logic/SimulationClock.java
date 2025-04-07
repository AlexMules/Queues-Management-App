package logic;

public class SimulationClock {
    private int currentTime = 0; //timpul curent al simularii
    private final int simulationInterval; //intervalul de timp pentru simulare
    private final Object lock = new Object(); //lacat necesar pentru sincronizare

    public SimulationClock(int simulationInterval) {
        this.simulationInterval = simulationInterval;
    }

    public boolean hasNextTick() {
        return currentTime < simulationInterval;
    }

    // metoda incrementeaza timpul si notifica thread-urile
    public void tick() {
        synchronized(lock) {
            currentTime++;
        }
    }

    // notifica toate thread-urile care asteapta
    public void notifyAllThreads() {
        synchronized(lock) {
            lock.notifyAll();
        }
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public Object getLock() {
        return lock;
    }
}