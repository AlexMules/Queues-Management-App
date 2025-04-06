package gui.view;

import gui.Controller;
import logic.SimulationManager;
import model.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SimulationSetupFrame extends JFrame {
    private JPanel contentPane;
    private JPanel inputPanel;

    private JLabel numberOfClientsLabel;
    private JTextField numberOfClientsTextField;

    private JLabel numberOfQueuesLabel;
    private JTextField numberOfQueuesTextField;

    private JLabel simulationIntervalLabel;
    private JTextField simulationIntervalTextField;

    private JLabel minimumArrivalTimeLabel;
    private JTextField minimumArrivalTimeTextField;

    private JLabel maximumArrivalTimeLabel;
    private JTextField maximumArrivalTimeTextField;

    private JLabel minimumServiceTimeLabel;
    private JTextField minimumServiceTimeTextField;

    private JLabel maximumServiceTimeLabel;
    private JTextField maximumServiceTimeTextField;

    private JButton validateDataButton;

    private Controller controller;

    public SimulationSetupFrame(String name) {
        super(name);
        prepareGui();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void prepareGui() {
        this.setSize(700, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // centerizează fereastra

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        prepareInputDataPanel();
        contentPane.add(inputPanel, BorderLayout.CENTER);

        validateDataButton = new JButton("Validate Data");
        validateDataButton.addActionListener(e -> controller.validateData(
                numberOfClientsTextField.getText(),
                numberOfQueuesTextField.getText(),
                simulationIntervalTextField.getText(),
                minimumArrivalTimeTextField.getText(),
                maximumArrivalTimeTextField.getText(),
                minimumServiceTimeTextField.getText(),
                maximumServiceTimeTextField.getText()
        ));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(validateDataButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(contentPane);
    }

    private void prepareInputDataPanel() {
        numberOfClientsLabel = new JLabel("Number of Clients:");
        numberOfClientsTextField = new JTextField(10);

        numberOfQueuesLabel = new JLabel("Number of Queues:");
        numberOfQueuesTextField = new JTextField(10);

        simulationIntervalLabel = new JLabel("Simulation Interval:");
        simulationIntervalTextField = new JTextField(10);

        minimumArrivalTimeLabel = new JLabel("Minimum Arrival Time:");
        minimumArrivalTimeTextField = new JTextField(10);

        maximumArrivalTimeLabel = new JLabel("Maximum Arrival Time:");
        maximumArrivalTimeTextField = new JTextField(10);

        minimumServiceTimeLabel = new JLabel("Minimum Service Time:");
        minimumServiceTimeTextField = new JTextField(10);

        maximumServiceTimeLabel = new JLabel("Maximum Service Time:");
        maximumServiceTimeTextField = new JTextField(10);

        inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.add(numberOfClientsLabel);
        inputPanel.add(numberOfClientsTextField);

        inputPanel.add(numberOfQueuesLabel);
        inputPanel.add(numberOfQueuesTextField);

        inputPanel.add(simulationIntervalLabel);
        inputPanel.add(simulationIntervalTextField);

        inputPanel.add(minimumArrivalTimeLabel);
        inputPanel.add(minimumArrivalTimeTextField);

        inputPanel.add(maximumArrivalTimeLabel);
        inputPanel.add(maximumArrivalTimeTextField);

        inputPanel.add(minimumServiceTimeLabel);
        inputPanel.add(minimumServiceTimeTextField);

        inputPanel.add(maximumServiceTimeLabel);
        inputPanel.add(maximumServiceTimeTextField);
    }

    public void showSimulationPanel(SimulationManager manager) {
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("Input Data:\n");
        logBuilder.append("Number of Clients: ").append(manager.getNumberOfClients()).append("\n");
        logBuilder.append("Number of Queues: ").append(manager.getNumberOfQueues()).append("\n");
        logBuilder.append("Simulation Interval: ").append(manager.getSimulationInterval()).append("\n");
        logBuilder.append("Minimum Arrival Time: ").append(manager.getMinimumArrivalTime()).append("\n");
        logBuilder.append("Maximum Arrival Time: ").append(manager.getMaximumArrivalTime()).append("\n");
        logBuilder.append("Minimum Service Time: ").append(manager.getMinimumServiceTime()).append("\n");
        logBuilder.append("Maximum Service Time: ").append(manager.getMaximumServiceTime()).append("\n\n");

        logBuilder.append("Time 0\n");
        logBuilder.append("Waiting clients:\n");
        ArrayList<Client> clients = manager.getGeneratedClients();
        for (Client client : clients) {
            logBuilder.append("(")
                    .append(client.getId()).append(",")
                    .append(client.getArrivalTime()).append(",")
                    .append(client.getServiceTime()).append(")\n");
        }

        for(int i = 1; i <= manager.getNumberOfQueues(); i++) {
            logBuilder.append("Queue ").append(i).append(": closed\n");
        }

        JTextArea simulationLogArea = new JTextArea(logBuilder.toString());
        simulationLogArea.setEditable(false);
        simulationLogArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(simulationLogArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JButton startSimulationButton = new JButton("Start Simulation");
        startSimulationButton.addActionListener(e -> {
            controller.startSimulation();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(startSimulationButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        contentPane.revalidate();
        contentPane.repaint();
    }
}
