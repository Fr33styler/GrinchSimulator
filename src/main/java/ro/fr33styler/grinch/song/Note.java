package ro.fr33styler.grinch.song;

public class Note {

    private Byte instrument;
    private Byte key;

    public Note(Byte instrument, Byte key) {
        this.instrument = instrument;
        this.key = key;
    }

    public Byte getInstrument() {
        return instrument;
    }

    public void setInstrument(Byte instrument) {
        this.instrument = instrument;
    }

    public Byte getKey() {
        return key;
    }

    public void setKey(Byte key) {
        this.key = key;
    }
}