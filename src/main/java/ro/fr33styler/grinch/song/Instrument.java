package ro.fr33styler.grinch.song;

import org.bukkit.Sound;

public class Instrument {

    public static Sound getInstrument(byte instrument) {
        switch (instrument) {
          case 0:
            return Sound.BLOCK_NOTE_BLOCK_HARP;
          case 1:
            return Sound.BLOCK_NOTE_BLOCK_BASS;
          case 2:
            return Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
          case 3:
            return Sound.BLOCK_NOTE_BLOCK_SNARE;
          case 4:
            return Sound.BLOCK_NOTE_BLOCK_HAT;
          default:
            return Sound.BLOCK_NOTE_BLOCK_HARP;
        }
    }
}