package app;

import gui.Controller;
import gui.view.SimulationSetupFrame;
import logic.SimulationManager;

public class Main {
    public static void main(String[] args) {
        SimulationSetupFrame frame = new SimulationSetupFrame("Queues Management App");

        SimulationManager manager = new SimulationManager();
        Controller controller = new Controller(frame, manager);

        frame.setController(controller);
        frame.setVisible(true);
    }
}