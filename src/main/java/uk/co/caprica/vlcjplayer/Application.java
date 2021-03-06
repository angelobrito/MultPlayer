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

package uk.co.caprica.vlcjplayer;

import java.awt.Window;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.EventBus;

import multiplayer.MultiScreensHandler;
import uk.co.caprica.vlcjplayer.event.TickEvent;
import uk.co.caprica.vlcjplayer.view.action.mediaplayer.MediaPlayerActions;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;

/**
 * Global application state.
 */
public final class Application {

    private static final String RESOURCE_BUNDLE_BASE_NAME = "strings/MultTecnologia-player";

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME);

    private static final int MAX_RECENT_MEDIA_SIZE = 10;

    private final EventBus eventBus;
    
    private int screenQtt;
    
    private JFrame mainFrame;

    private MultiScreensHandler multiMediaPlayerComponent;

    private MediaPlayerActions mediaPlayerActions;

    private final ScheduledExecutorService tickService = Executors.newSingleThreadScheduledExecutor();

    private final Deque<String> recentMedia = new ArrayDeque<>(MAX_RECENT_MEDIA_SIZE);

    private static final class ApplicationHolder {
        private static final Application INSTANCE = new Application();
    }

    public static Application application() {
        return ApplicationHolder.INSTANCE;
    }

    public static ResourceBundle resources() {
        return resourceBundle;
    }

    private Application() {
        eventBus = new EventBus();
        screenQtt = 4;
        mainFrame = null;
        multiMediaPlayerComponent = null;
        mediaPlayerActions = null;
        tickService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                eventBus.post(TickEvent.INSTANCE);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void subscribe(Object subscriber) {
        eventBus.register(subscriber);
    }

    public void post(Object event) {
        // Events are always posted and processed on the Swing Event Dispatch thread
        if (SwingUtilities.isEventDispatchThread()) {
            eventBus.post(event);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    eventBus.post(event);
                }
            });
        }
    }

    public MultiScreensHandler getMediaPlayerComponent() {
        return multiMediaPlayerComponent;
    }

    public MediaPlayerActions mediaPlayerActions() {
        return mediaPlayerActions;
    }
    
    public MediaPlayerActions mediaNewPlayerActions() {
    	mediaPlayerActions = new MediaPlayerActions(multiMediaPlayerComponent);
        return mediaPlayerActions;
    }

    public void addRecentMedia(String mrl) {
        if (!recentMedia.contains(mrl)) {
            recentMedia.addFirst(mrl);
            while (recentMedia.size() > MAX_RECENT_MEDIA_SIZE) {
                recentMedia.pollLast();
            }
        }
    }

    public List<String> recentMedia() {
        return new ArrayList<>(recentMedia);
    }

    public void clearRecentMedia() {
        recentMedia.clear();
    }
    
    public String playlistItem() {
    	return "";
    }

	public int getScreenQtt() {
		return screenQtt;
	}
	
	public void setScreenQuantity(int screenQuantity) {
		this.screenQtt = screenQuantity;
		this.multiMediaPlayerComponent.setNewScreensLayout(screenQuantity);
	}

	/**
	 * @return the mainFrame
	 */
	public JFrame getMainFrame() {
		return mainFrame;
	}
	
	public JFrame getNewMainFrame() {
		mainFrame = new MainFrame();
		return mainFrame;
	}

	public MultiScreensHandler getNewMediaPlayerComponent(Window container) {
		multiMediaPlayerComponent = new MultiScreensHandler((JFrame) container) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 3106592667852822185L;

			@Override
            protected String[] onGetMediaPlayerFactoryExtraArgs() {
                return new String[] {"--no-osd"}; // Disables the display of the snapshot filename (amongst other things)
            }
        }; 
		return this.multiMediaPlayerComponent;
	}
	
	public void checkContinuousPlay() {
		System.out.println("Application to check continuous play...");
		if(this.hasNextToPlay()) {
			System.out.println("Application identifyed that there is next element to play on list");
			this.playNextItem();
		}
	}
	
	public void updateEnabledControlls() {
		((MainFrame) this.mainFrame).updateEnabledComponents();
	}

	public boolean hasNextToPlay() {
		return ((MultiScreensHandler) ((MainFrame) this.mainFrame).getPlayerHandler()).hasNextToPlay();
	}

	public boolean hasPreviousToPlay() {
		return ((MultiScreensHandler) ((MainFrame) this.mainFrame).getPlayerHandler()).hasPreviousToPlay();
	}

	public void playNextItem() {
		System.out.println("Next Video Item to play={" + this.multiMediaPlayerComponent.getNextVideo().getFileName() + "}");
	}

	public void playPreviousItem() {
		System.out.println("Previous Video Item to play={" + this.multiMediaPlayerComponent.getPreviousVideo().getFileName() + "}");
	}

	public void setScale(float zoom) {
		this.multiMediaPlayerComponent.setScale(zoom);
	}

	public void setAspectRatio(String aspectRatio) {
		this.multiMediaPlayerComponent.setAspectRatio(aspectRatio);
	}
}
