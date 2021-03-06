/*
 * This file was created over VLCJ.
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

package uk.co.caprica.vlcjplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import multiplayer.MultiScreensHandler;
import multiplayer.MultiPlayerInstance;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.log.NativeLog;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.streams.NativeStreams;
import uk.co.caprica.vlcjplayer.event.ShutdownEvent;
import uk.co.caprica.vlcjplayer.view.debug.DebugFrame;
import uk.co.caprica.vlcjplayer.view.effects.EffectsFrame;
import uk.co.caprica.vlcjplayer.view.messages.NativeLogFrame;

/**
 * Application entry-point.
 */
public class MultTecnologiaPlayer {

    private static final NativeStreams nativeStreams;

    // Redirect the native output streams to files, useful since VLC can generate a lot of noisy native logs we don't care about
    // (on the other hand, if we don't look at the logs we might won't see errors)
    static {
//        if (RuntimeUtil.isNix()) {
//            nativeStreams = new NativeStreams("stdout.log", "stderr.log");
//        }
//        else {
            nativeStreams = null;
//        }
    }

    private final JFrame mainFrame;

    @SuppressWarnings("unused")
    private final JFrame messagesFrame;

    @SuppressWarnings("unused")
    private final JFrame effectsFrame;

    @SuppressWarnings("unused")
    private final JFrame debugFrame;

    private final NativeLog nativeLog;

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
    	setLookAndFeel();

    	// This will locate LibVLC for the vast majority of cases
    	try {
    		NativeDiscovery env = new NativeDiscovery(); 
    		if(!env.discover()) throw new Exception("Native Library não foi encontrada. Contate o administrador de sistemas.");
    	}
    	catch(Exception e){
    		JOptionPane.showMessageDialog(null, e.toString(), "Error",
    				JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	}

        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MultTecnologiaPlayer().start();
            }
        });
    }

    private static void setLookAndFeel() {
        String lookAndFeelClassName;
        if (RuntimeUtil.isNix()) {
            lookAndFeelClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        }
        else {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        }
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        }
        catch(Exception e) {
            // Silently fail, it doesn't matter
        }
    }

    public MultTecnologiaPlayer() {
        
    	JFrame.setDefaultLookAndFeelDecorated(true);
    	        
    	mainFrame = application().getNewMainFrame();
    	MultiScreensHandler mediaPlayerComponent = application().getMediaPlayerComponent();
    	mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.getMediaPlayer().stop();
                mediaPlayerComponent.release();
                if (nativeStreams != null) {
                    nativeStreams.release();
                }
                application().post(ShutdownEvent.INSTANCE);
                for(MultiPlayerInstance pi : mediaPlayerComponent.getPlayers()) {
                    pi.mediaPlayer().release();
                }
                mediaPlayerComponent.getFactory().release();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            	System.exit(0);
            }
        });
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TODO Update the Fullscreen strategy
        for(int i = 0; i < application().getScreenQtt(); i++) {
        	EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerComponent.getMediaPlayer();
        	embeddedMediaPlayer.setFullScreenStrategy(new VlcjPlayerFullScreenStrategy(mainFrame));
        }

        nativeLog = mediaPlayerComponent.getMediaPlayerFactory().newLog();
        
        messagesFrame = new NativeLogFrame(nativeLog);
        effectsFrame = new EffectsFrame();
        debugFrame = new DebugFrame();
    }

    private void start() {
        mainFrame.setVisible(true);
    }
}
