package logic;

public interface SimulationUpdate {
    void onSimulationUpdated(int currentTime);
    void onSimulationEnded();
}
