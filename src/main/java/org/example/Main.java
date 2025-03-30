package org.example;

import gui.Controller;
import gui.view.SimulationSetupFrame;
import logic.Generator;

public class Main {
    public static void main(String[] args) {
        Generator generator = new Generator();
        SimulationSetupFrame frame = new SimulationSetupFrame("Queues Management App");

        Controller controller = new Controller(frame, generator);
        frame.setController(controller);

        frame.setVisible(true);
    }
}