package org.chy.lamiaplugin.marker;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class EditorCustomIcon extends JPanel implements EditorCustomElementRenderer {

    private final Icon icon;

    private final JButton button;
    private final MarkerStatusButton markerStatusButton;

    public EditorCustomIcon(Icon icon, Project project) {
        super(new BorderLayout());
        this.icon = icon;
        this.button = new JButton("Button");
        this.button.setSize(this.button.getPreferredSize());
        this.markerStatusButton = new MarkerStatusButton("conversion success", project);
        this.button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute when the button is clicked
                System.out.println("Button clicked!");
            }
        });


        add(markerStatusButton.actionButton ,BorderLayout.CENTER);
    }
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        super.paint(g);
    }


    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return getPreferredSize().width;
    }

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return getPreferredSize().height;
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics2D g, @NotNull Rectangle2D targetRegion, @NotNull TextAttributes textAttributes) {
        super.paint(g);
    }


}
