import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class BookingChartPanel extends JPanel {
    private static final Color ACCENT_COLOR = new Color(18, 40, 63);
    private static final Color AVAILABLE_COLOR = new Color(22, 163, 74);
    private static final Color BOOKED_COLOR = new Color(220, 38, 38);
    private static final Color MUTED_COLOR = new Color(69, 97, 120);
    private static final Color GRID_COLOR = new Color(156, 200, 209);

    public BookingChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(760, 260));
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int left = 60;
        int top = 52;
        int bottom = getHeight() - 46;
        int chartHeight = Math.max(1, bottom - top);

        String[] labels = {"Tickets", "Available", "Booked"};
        int[] values = {
                DataStore.totalTicketsSold(),
                DataStore.availableBoatsCount(),
                DataStore.bookedBoatsCount()
        };
        Color[] colors = {ACCENT_COLOR, AVAILABLE_COLOR, BOOKED_COLOR};

        int max = 1;
        for (int value : values) {
            max = Math.max(max, value);
        }

        g2.setColor(ACCENT_COLOR);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("Booking Statistics", left, 28);

        g2.setColor(GRID_COLOR);
        g2.drawLine(left, top, left, bottom);
        g2.drawLine(left, bottom, getWidth() - 40, bottom);

        int barWidth = 92;
        int gap = 60;
        int x = left + 44;

        for (int i = 0; i < values.length; i++) {
            int barHeight = (int) ((values[i] / (double) max) * (chartHeight - 16));
            int y = bottom - barHeight;

            g2.setColor(colors[i]);
            g2.fillRoundRect(x, y, barWidth, Math.max(4, barHeight), 12, 12);

            g2.setColor(ACCENT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString(String.valueOf(values[i]), x + 36, y - 10);

            g2.setColor(MUTED_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.drawString(labels[i], x + 8, bottom + 24);

            x += barWidth + gap;
        }

        g2.dispose();
    }
}
