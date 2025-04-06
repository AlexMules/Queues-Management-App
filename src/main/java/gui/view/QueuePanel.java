package gui.view;

import model.Client;
import model.Server;
import javax.swing.*;
import java.awt.*;

public class QueuePanel extends JPanel {
    private final Server server;
    private final int circleDiameter = 30;
    private final int spacing = 12;

    protected QueuePanel(Server server) {
        this.server = server;
        setPreferredSize(new Dimension(300, 50));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(new Color(245, 245, 245));
        setOpaque(true);
    }

    private int drawClientCircle(Graphics g, Client client, int x, int y, Color color) {
        g.setColor(color);
        g.fillOval(x, y, circleDiameter, circleDiameter);

        g.setColor(Color.WHITE);
        String idText = String.valueOf(client.getId());
        int textWidth = g.getFontMetrics().stringWidth(idText);
        int textHeight = g.getFontMetrics().getAscent();
        g.drawString(idText, x + (circleDiameter - textWidth) / 2, y + (circleDiameter + textHeight) / 2 - 2);

        return x + circleDiameter + spacing;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 10;
        int y = getHeight() / 2 - circleDiameter / 2;

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.BLACK);
        String queueLabel = "Queue " + server.getId() + ": ";
        int labelHeight = g.getFontMetrics().getAscent();
        g.drawString(queueLabel, x, y + labelHeight / 2);
        int labelWidth = g.getFontMetrics().stringWidth(queueLabel);
        x += labelWidth + spacing;

        g.setFont(new Font("SansSerif", Font.BOLD, 12));

        // clientul in procesare (rosu)
        Client processing = server.getProcessingClient();
        if (processing != null) {
            x = drawClientCircle(g, processing, x, y, Color.RED);
        }

        // clientii din coada (albastru)
        for (Client client : server.getClients()) {
            x = drawClientCircle(g, client, x, y, Color.BLUE);
        }
    }
}