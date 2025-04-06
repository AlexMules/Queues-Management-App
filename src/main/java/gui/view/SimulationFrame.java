package gui.view;

import model.Server;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel queuesPanel;

    public SimulationFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));

        queuesPanel = new JPanel();
        queuesPanel.setLayout(new BoxLayout(queuesPanel, BoxLayout.Y_AXIS));

        // scroll pane (necesar pentru numar mare de cozi si clienti)
        JScrollPane scrollPane = new JScrollPane(
                queuesPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    public void updateQueues(List<Server> servers, int currentTime) {
        queuesPanel.removeAll();

        JLabel timeLabel = new JLabel("Time: " + currentTime);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        queuesPanel.add(timeLabel);
        queuesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        queuesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Server server : servers) {
            QueuePanel qp = new QueuePanel(server);
            qp.setAlignmentX(Component.LEFT_ALIGNMENT);
            qp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            queuesPanel.add(qp);
            queuesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        queuesPanel.revalidate();
        queuesPanel.repaint();
    }
}

