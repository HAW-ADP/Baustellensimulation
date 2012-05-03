package logik.interfaces;

import java.util.List;

public interface Zustand {
	public Zustand naechsterZustand(Zeitspanne baustellenZeit, int strassenFassungsverm�gen,
			Zeitspanne minimaleAmpelSchaltzeit);
	
	public Uhrzeit naechsteUhrzeit();
	public List<Uhrzeit> naechsteAutoZeiten();
	public int naechsteEinfahrt();
	public Warteschlange naechsteBaustelle();
	public Warteschlange naechsterParkplatz();
	public List<Zeitspanne> naechsteAutoStandzeiten();
	public int naechsteAusfahrt();
	
	public Uhrzeit duhrzeit();
	public int einfahrt();
	public Warteschlange baustelle();
	public Warteschlange parkplatz();
	public int ausfahrt();
}
