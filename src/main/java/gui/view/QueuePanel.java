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
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(new Color(245, 245, 245));
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 10;
        int y = getHeight() / 2 - circleDiameter / 2;

        // Desenează eticheta cozii (ex: "Queue 1:")
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.BLACK);
        String queueLabel = "Queue " + server.getId() + ": ";
        int labelHeight = g.getFontMetrics().getAscent();
        g.drawString(queueLabel, x, y + labelHeight / 2);
        int labelWidth = g.getFontMetrics().stringWidth(queueLabel);
        x += labelWidth + spacing;

        // Setează fontul pentru client (bulină) – textul se desenează cu alb
        g.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Clientul în procesare – desenat în roșu
        Client processing = server.getProcessingClient();
        if (processing != null) {
            g.setColor(Color.RED);
            g.fillOval(x, y, circleDiameter, circleDiameter);
            g.setColor(Color.WHITE);
            String idText = String.valueOf(processing.getId());
            int textWidth = g.getFontMetrics().stringWidth(idText);
            int textHeight = g.getFontMetrics().getAscent();
            g.drawString(idText, x + (circleDiameter - textWidth) / 2, y + (circleDiameter + textHeight) / 2 - 2);
            x += circleDiameter + spacing;
        }

        // Clienții din coadă – desenat în albastru
        for (Client client : server.getClients()) {
            g.setColor(Color.BLUE);
            g.fillOval(x, y, circleDiameter, circleDiameter);
            g.setColor(Color.WHITE);
            String idText = String.valueOf(client.getId());
            int textWidth = g.getFontMetrics().stringWidth(idText);
            int textHeight = g.getFontMetrics().getAscent();
            g.drawString(idText, x + (circleDiameter - textWidth) / 2, y + (circleDiameter + textHeight) / 2 - 2);
            x += circleDiameter + spacing;
        }
    }
}