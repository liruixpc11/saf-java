package lab.cadl.analysis.utilities.graphviz;

/*
 * #%L
 * Wintersleep GraphViz
 * %%
 * Copyright (C) 2008 - 2014 Davy Verstappen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageViewer extends JFrame {
    private BufferedImage image;

    public ImageViewer(final String pathname) {
        this(pathname, true);
    }

    public ImageViewer(final String pathname, boolean exitOnClose) {
        try {
            image = ImageIO.read(new File(pathname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTitle("ImageViewer");

        JLabel label = new JLabel();
        Container contentPane = getContentPane();
        contentPane.add(label, "Center");
        label.setIcon(new ImageIcon(image));
        label.setSize(image.getWidth(), image.getHeight());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                setSize(getWidth() + image.getWidth(), getHeight() + image.getHeight());
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void view(String imagePath, boolean exitOnClose) {
        new ImageViewer(imagePath, exitOnClose).setVisible(true);
    }

    public static void view(String imagePath) {
        new ImageViewer(imagePath).setVisible(true);
    }

    public static void main(String[] args) {
        try {
            view("/Users/lirui/Pictures/1.png");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
