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

package uk.co.caprica.vlcjplayer.view.action;

import static uk.co.caprica.vlcjplayer.Application.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public final class Resource {

	private final String id;

	public static Resource resource(String id) {
		return new Resource(id);
	}

	private Resource(String id) {
		this.id = id;
	}

	public String name() {
		if (resources().containsKey(id)) {

			/*
			 *  FIXME Because of a problem with the ResourceBundle way of reading 
			 *  Latin chars from the .properties file a work around was done here
			 *  to bypass the problematic id and solve the problem momentarily.
			 */
			if(id.equals("menu.playback")) return "Reprodução";
			else if(id.equals("menu.playback.item.title")) return "Título";
			else if(id.equals("menu.playback.item.stop.tooltip")) return "Parar Reprodução";
			else if(id.equals("menu.audio")) return "Áudio";
			else if(id.equals("menu.audio.item.device")) return "Dispositivo de Áudio";
			else if(id.equals("menu.video")) return "Vídeo";
			else if(id.equals("menu.video.item.track")) return "Trilha de Vídeo";
			else if(id.equals("menu.video.item.alwaysOnTop")) return "Sempre Visível";
			else if(id.equals("menu.video.item.aspectRatio.item.default") ||
					id.equals("menu.video.item.crop.item.default")){
				return "Padrão";
			}
			else if(id.equals("menu.tools.item.effects.tooltip")) return "Configurações Avançadas";
			else if(id.equals("menu.tools.item.debug")) return "Depuração";
			else if(id.equals("dialog.about.blurb1")) return "O MultTecnologia Player é um Software tocador de video desenvolvido pela MultTecnologia.";
			else if(id.equals("dialog.about.blurb2")) return "Este projeto tem por objetivo facilitar a exibição e monitoramento de vídeos gravados pelo sistema de monitoramento da MultTecnologia.";
			else if(id.equals("dialog.about.attribution1")) return "Este projeto foi baseado no projeto VLC, estudado, modificado e distribuído sobre a licença GPL versão 3 ou posterior.";
			else if(id.equals("dialog.about.applicationVersion")) return "Versão do MultTecnologia Player";
			else if(id.equals("dialog.about.vlcjVersion")) return "Versão do VLCJ";
			else if(id.equals("dialog.about.vlcVersion")) return "Versão da LibVLC";
			else if(id.equals("dialog.effects.tabs.audio")) return "Efeitos de Áudio";
			else if(id.equals("dialog.effects.tabs.video")) return "Efeitos de Vídeo";
			else if(id.equals("dialog.effects.tabs.video.adjust.saturation")) return "Saturação";
			else if(id.equals("")) return "";
			else if(id.equals("")) return "";
			else if(id.equals("")) return "";
			else return "" + resources().getString(id);
		}
		else {
			return null;
		}
	}

	public Integer mnemonic() {
		String key = id + ".mnemonic";
		if (resources().containsKey(key)) {
			return new Integer(resources().getString(key).charAt(0));
		}
		else {
			return null;
		}
	}

	public KeyStroke shortcut() {
		String key = id + ".shortcut";
		if (resources().containsKey(key)) {
			return KeyStroke.getKeyStroke(resources().getString(key));
		}
		else {
			return null;
		}
	}

	public String tooltip() {
		String key = id + ".tooltip";
		if (resources().containsKey(key)) {
			return resources().getString(key);
		}
		else {
			return null;
		}
	}

	public Icon menuIcon() {
		String key = id + ".menuIcon";
		if (resources().containsKey(key)) {
			return new ImageIcon(getClass().getResource("/icons/actions/" + resources().getString(key) + ".png"));
		}
		else {
			return null;
		}
	}

	public Icon buttonIcon() {
		String key = id + ".buttonIcon";
		if (resources().containsKey(key)) {
			return new ImageIcon(getClass().getResource("/icons/buttons/" + resources().getString(key) + ".png"));
		}
		else {
			return null;
		}
	}
}
