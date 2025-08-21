/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview;

/**
 *
 * @author Lyz
 */

import co.unicauca.gestiontrabajogrado.presentation.common.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarPanel extends JPanel {

    private static final Dimension BTN_SIZE = new Dimension(220, 48);
    private final JPanel submenu; // contenedor de submenú

    public SidebarPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 16, 16, 12));
        setPreferredSize(new Dimension(240, 0));

        // 1) Menú de usuario (#A7C6EA)
        add(pillButton("Menú de usuario", new Color(0xA7C6EA), Color.BLACK));
        add(Box.createVerticalStrut(12));

        // 2) estudiante (#73AAEB) con flecha y submenú
        JPanel estudianteHeader = headerWithArrow("Estudiante", new Color(0x73AAEB), Color.WHITE);
        add(estudianteHeader);
        add(Box.createVerticalStrut(8));

        submenu = new JPanel();
        submenu.setOpaque(false);
        submenu.setLayout(new BoxLayout(submenu, BoxLayout.Y_AXIS));
        submenu.add(subItemButton("Ver estado del Trabajo de Grado"));
        submenu.add(Box.createVerticalStrut(6));
        submenu.add(subItemButton("Crear nuevo Trabajo de Grado"));
        submenu.setVisible(false);
        add(submenu);

        add(Box.createVerticalStrut(12));

        // 3) Cerrar sesión (#2665C4)
        add(pillButton("Cerrar Sesión", new Color(0x2665C4), Color.BLACK));
        add(Box.createVerticalGlue());

        // Acción: desplegar submenú al hacer clic en Estudiante
        estudianteHeader.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                toggleSubmenu();
            }
        });
    }

    private void toggleSubmenu() {
        submenu.setVisible(!submenu.isVisible());
        submenu.revalidate();
        submenu.getParent().revalidate();
        submenu.repaint();
    }

    /** Botón principal tipo pastilla */
    private JComponent pillButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setFont(UIConstants.BODY);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(new LineBorder(bg.darker(), 1, true));
        b.setPreferredSize(BTN_SIZE);
        b.setMaximumSize(BTN_SIZE);
        b.setMinimumSize(BTN_SIZE);
        return b;
    }

    /** Cabecera con flecha ▾ */
    private JPanel headerWithArrow(String text, Color bg, Color fg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBackground(bg);
        p.setOpaque(true);
        p.setBorder(new LineBorder(bg.darker(), 1, true));
        p.setPreferredSize(BTN_SIZE);
        p.setMaximumSize(BTN_SIZE);
        p.setMinimumSize(BTN_SIZE);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel l = new JLabel(text);
        l.setFont(UIConstants.BODY);
        l.setForeground(fg);

        JLabel arrow = new JLabel("▾");
        arrow.setFont(UIConstants.BODY);
        arrow.setForeground(fg);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        center.setOpaque(false);
        center.add(l);
        p.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        right.setOpaque(false);
        right.add(arrow);
        p.add(right, BorderLayout.EAST);

        return p;
    }

    /** Botones del submenú */
    private JComponent subItemButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setFont(UIConstants.BODY);
        b.setForeground(UIConstants.TEXT_PRIMARY);
        b.setBackground(Color.WHITE);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xD0D8E8), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        Dimension d = new Dimension(BTN_SIZE.width, 40);
        b.setPreferredSize(d);
        b.setMaximumSize(d);
        b.setMinimumSize(d);

        // Acción demo (puedes cambiar por navegación real)
        b.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Acción: " + text, "Info",
                        JOptionPane.INFORMATION_MESSAGE)
        );
        return b;
    }
}