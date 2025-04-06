package gui.view;

import model.Server;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel queuesPanel;

    public SimulationFrame(String title, List<Server> servers) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        queuesPanel = new JPanel();
        queuesPanel.setLayout(new GridLayout(servers.size(), 1, 5, 5));
        mainPanel.add(queuesPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // Metodă de actualizare a panourilor din fereastră:
    public void updateQueues(List<Server> servers) {
        queuesPanel.removeAll();
        for (Server server : servers) {
            QueuePanel qp = new QueuePanel(server);
            queuesPanel.add(qp);
        }
        queuesPanel.revalidate();
        queuesPanel.repaint();
    }
}