import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RevenueChartPanel extends JPanel {
    private static final Color ACCENT_COLOR = new Color(18, 40, 63);
    private static final Color MUTED_COLOR = new Color(69, 97, 120);
    private static final Color GRID_COLOR = new Color(156, 200, 209);

    public RevenueChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(760, 320));
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int left = 60;
        int right = width - 32;
        int top = 44;
        int bottom = height - 54;
        int chartHeight = Math.max(1, bottom - top);

        g2.setColor(ACCENT_COLOR);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("Revenue by Booking", left, 24);

        g2.setColor(GRID_COLOR);
        g2.drawLine(left, top, left, bottom);
        g2.drawLine(left, bottom, right, bottom);

        if (DataStore.bookings.isEmpty()) {
            g2.setColor(MUTED_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            g2.drawString("No booking revenue yet", left + 18, top + 42);
            g2.dispose();
            return;
        }

        double maxRevenue = 1;
        for (Booking booking : DataStore.bookings) {
            maxRevenue = Math.max(maxRevenue, booking.getAmount());
        }

        int count = DataStore.bookings.size();
        int gap = 14;
        int availableWidth = Math.max(1, right - left - gap);
        int barWidth = Math.max(26, Math.min(64, (availableWidth - (count * gap)) / count));
        int x = left + gap;

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        for (Booking booking : DataStore.bookings) {
            int barHeight = (int) ((booking.getAmount() / maxRevenue) * (chartHeight - 22));
            int y = bottom - barHeight;

            g2.setColor(ACCENT_COLOR);
            g2.fillRoundRect(x, y, barWidth, Math.max(4, barHeight), 10, 10);

            g2.setColor(ACCENT_COLOR);
            g2.drawString("Rs " + String.format("%.0f", booking.getAmount()), x - 2, y - 8);

            g2.setColor(MUTED_COLOR);
            g2.drawString("B" + booking.getBookingId(), x + 4, bottom + 22);

            x += barWidth + gap;
            if (x > right - barWidth) {
                break;
            }
        }

        g2.dispose();
    }
}
