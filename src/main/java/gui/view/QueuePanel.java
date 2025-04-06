package gui.view;

import model.Client;
import model.Server;
import javax.swing.*;
import java.awt.*;

public class QueuePanel extends JPanel {
    private final Server server;
    private final int circleDiameter = 20;
    private final int spacing = 10;

    public QueuePanel(Server server) {
        this.server = server;
        setPreferredSize(new Dimension(300, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 10;
        int y = getHeight() / 2 - circleDiameter / 2;

        // Draw the client currently being processed (if any) in red.
        Client processing = server.getProcessingClient();
        if (processing != null) {
            g.setColor(Color.RED);
            g.fillOval(x, y, circleDiameter, circleDiameter);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, circleDiameter, circleDiameter);
            g.drawString(String.valueOf(processing.getId()), x + 5, y + 15);
            x += circleDiameter + spacing;
        }

        // Draw waiting clients in blue.
        for (Client client : server.getClients()) {
            g.setColor(Color.BLUE);
            g.fillOval(x, y, circleDiameter, circleDiameter);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, circleDiameter, circleDiameter);
            g.drawString(String.valueOf(client.getId()), x + 5, y + 15);
            x += circleDiameter + spacing;
        }
    }
}