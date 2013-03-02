/*
 * Copyright (c) 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasologylauncher.gui;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** @author Skaldarnar */
public class LinkJLabel extends JLabel implements MouseListener {

    private static final long CLICK_DELAY = 200L;
    private long lastClicked = System.currentTimeMillis();

    private static final Color HOVER_COLOR = Color.DARK_GRAY;
    private static final Color STANDARD_COLOR = Color.LIGHT_GRAY;

    private String url;

    public LinkJLabel(String text, String url) {
        super(text);
        setForeground(STANDARD_COLOR);
        this.url = url;
        super.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (lastClicked + CLICK_DELAY > System.currentTimeMillis()) {
            return;
        }
        lastClicked = System.currentTimeMillis();
        try {
            URI uri = new URI(url);
            browse(uri);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void browse(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(HOVER_COLOR);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(STANDARD_COLOR);
    }
}