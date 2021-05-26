package ro.fr33styler.grinch;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import ro.fr33styler.grinch.handler.Game;
import ro.fr33styler.grinch.handler.GameState;
import ro.fr33styler.grinch.handlerutils.MathUtils;
import ro.fr33styler.grinch.song.Instrument;
import ro.fr33styler.grinch.song.Layer;
import ro.fr33styler.grinch.song.Note;
import ro.fr33styler.grinch.song.NotePitch;
import ro.fr33styler.grinch.song.Song;

public class UpdateTask extends BukkitRunnable {

	private Main main;
	private long ticks = 0;

	public UpdateTask(Main main) {
		this.main = main;
		runTaskTimer(main, 1, 1);
	}

	@Override
	public void run() {
		for (Game g : main.getManager().getGames()) {
			Song song = g.getSong();
			if (song != null) {
				song.setTick(song.getTick()+1);
				if (song.getTick() >= 0 && song.getTick() % song.getDelay() == 0) {
					song.setFrame(song.getFrame()+1);
					if (song.getFrame() > song.getLength()) {
						g.getSongs().remove(song);
						g.setSong(g.getSongs().get(MathUtils.random().nextInt(g.getSongs().size())));
						return;
					}
					for (Player p : g.getPlayers()) {
						for (Layer l : song.getLayer().values()) {
							Note note = l.getNote(song.getFrame());
							if (note == null) {
								continue;
							}
							p.playSound(p.getLocation(), Instrument.getInstrument(note.getInstrument()), (l.getVolume() * 10000) / 1000000f, NotePitch.getPitch(note.getKey() - 33));
						}
					}
				}
			}
			try {
			if (g.getState() == GameState.IN_GAME || ticks % 20 == 0) {
				if (g.getState() == GameState.IN_GAME || g.getTimer() > 0) {
					g.getStatus().values().forEach(s -> main.getManager().updateStatus(g, s));
				}
				if (g.getState() == GameState.WAITING && g.getPlayers().size() < g.getMin()) {
					for (Player p : g.getPlayers()) {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.BAR_PLAYERS.toString().replace("%min%", g.getMin() + "")));
					}
				}
			}
			if (g.getState() == GameState.IN_GAME && ticks % 40 == 0) {
				g.refreshTop();
			}
			if (g.isStarted() && ticks % 20 == 0) {
				g.run();
			}
			} catch (Exception e) {
				main.getManager().stopGame(g, false);
				e.printStackTrace();
			}
		}
		ticks++;
	}

}