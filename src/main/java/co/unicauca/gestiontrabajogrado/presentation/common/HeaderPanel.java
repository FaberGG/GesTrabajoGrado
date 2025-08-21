/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.gestiontrabajogrado.presentation.common;

/**
 *
 * @author Lyz
 */

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setPreferredSize(new Dimension(10, 86));
        setBackground(UIConstants.BLUE_MAIN);
        setLayout(new BorderLayout());

        // Títulos a la izquierda
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel uni = new JLabel("Universidad");
        JLabel del = new JLabel("del Cauca");
        JLabel title = new JLabel("Gestión del Proceso de");
        JLabel subtitle = new JLabel("Trabajo de Grado");

        for (JLabel l : new JLabel[]{uni, del, title, subtitle}) {
            l.setForeground(Color.WHITE);
        }
        uni.setFont(new Font("Serif", Font.BOLD, 22));
        del.setFont(new Font("Serif", Font.BOLD, 22));
        title.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JPanel left = new JPanel(new GridLayout(2,1));
        left.setOpaque(false);
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.add(uni);
        leftTop.add(Box.createHorizontalStrut(6));
        leftTop.add(del);

        JPanel leftBottom = new JPanel();
        leftBottom.setOpaque(false);
        leftBottom.add(title);
        leftBottom.add(Box.createHorizontalStrut(8));
        leftBottom.add(subtitle);

        left.add(leftTop);
        left.add(leftBottom);

        add(left, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
        add(new RibbonRight(), BorderLayout.EAST);
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x8E44AD))); // franja morada finita
    }

    // Dibuja las “mordidas” rojas de la derecha
    static class RibbonRight extends JComponent {
        RibbonRight() {
            setPreferredSize(new Dimension(120, 86));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(UIConstants.BLUE_MAIN);
            g2.fillRect(0,0,getWidth(),getHeight());

            g2.setColor(UIConstants.ACCENT_RED);
            int w = getWidth();
            int h = getHeight();
            int tooth = 22;
            int x = w - tooth;
            for (int y = 0; y < h; y += tooth) {
                int[] xs = {w, x, w};
                int[] ys = {y, y + tooth/2, y + tooth};
                g2.fillPolygon(xs, ys, 3);
            }
            g2.dispose();
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // degradado azul
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0,0, UIConstants.BLUE_MAIN, 0,getHeight(), UIConstants.BLUE_DARK));
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.dispose();
    }
}
