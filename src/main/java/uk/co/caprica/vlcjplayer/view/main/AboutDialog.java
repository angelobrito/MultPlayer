/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */

package uk.co.caprica.vlcjplayer.view.main;

import static uk.co.caprica.vlcjplayer.view.action.Resource.resource;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.Info;
import uk.co.caprica.vlcj.version.LibVlcVersion;

final class AboutDialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9159057228235442665L;

	AboutDialog(Window owner) {
        super(owner, resource("dialog.about").name(), Dialog.ModalityType.DOCUMENT_MODAL);

        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load build.properties", e);
        }

        setLayout(new MigLayout("insets 30, fillx", "[shrink]30[shrink][grow]", "[]30[]10[]10[]30[]10[]10[]0[]"));
        getContentPane().setBackground(Color.white);
        
        ImageIcon img = new ImageIcon(getClass().getResource("/MultTecnologia-logo.png"));
        this.setIconImage(img.getImage());

        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon(getClass().getResource("/MultTecnologia-logo-name.png")));

        JLabel applicationLabel = new JLabel();
        applicationLabel.setFont(applicationLabel.getFont().deriveFont(30.0f));
        applicationLabel.setText(resource("dialog.about.application").name());

        JLabel blurb1Label = new JLabel();
        blurb1Label.setText(resource("dialog.about.blurb1").name());

        JLabel blurb2Label = new JLabel();
        blurb2Label.setText(resource("dialog.about.blurb2").name());

        JLabel attribution1Label = new JLabel();
        attribution1Label.setText(resource("dialog.about.attribution1").name());

        JLabel applicationVersionLabel = new JLabel();
        applicationVersionLabel.setText(resource("dialog.about.applicationVersion").name());

        JLabel applicationVersionValueLabel = new ValueLabel();
        applicationVersionValueLabel.setText(properties.getProperty("application.version"));

        JLabel vlcjVersionLabel = new JLabel();
        vlcjVersionLabel.setText(resource("dialog.about.vlcjVersion").name());

        JLabel vlcjVersionValueLabel = new ValueLabel();
        vlcjVersionValueLabel.setText(Info.getInstance().version().toString());

        JLabel vlcVersionLabel = new JLabel();
        vlcVersionLabel.setText(resource("dialog.about.vlcVersion").name());

        JLabel vlcVersionValueLabel = new ValueLabel();
        vlcVersionValueLabel.setText(LibVlcVersion.getVersion().toString());

        JLabel vlcChangesetValueLabel = new ValueLabel();
        vlcChangesetValueLabel.setText(LibVlcVersion.getChangeset());

        add(logoLabel, "shrink, top, spany 7");
        add(applicationLabel, "grow, spanx 2, wrap");
        add(blurb1Label, "grow, spanx 2, wrap");
        add(blurb2Label, "grow, spanx 2, wrap");
        add(attribution1Label, "grow, spanx 2, wrap");
        add(applicationVersionLabel, "");
        add(applicationVersionValueLabel, "wrap");
        add(vlcjVersionLabel);
        add(vlcjVersionValueLabel, "wrap");
        add(vlcVersionLabel);
        add(vlcVersionValueLabel, "wrap");
        add(vlcChangesetValueLabel, "skip 2");

        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setResizable(false);
    }

    private class ValueLabel extends JLabel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 8595718080095164467L;

		public ValueLabel() {
            setFont(getFont().deriveFont(Font.BOLD));
        }
    }
}
