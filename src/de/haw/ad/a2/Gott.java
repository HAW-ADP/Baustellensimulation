package de.haw.ad.a2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.haw.ad.a2.interfaces.Uhrzeit;
import de.haw.ad.a2.interfaces.Zeitspanne;

public class Gott implements Iterable<Zustand> {
	private final Zeitspanne baustellenZeit;
	private final Zeitspanne autoAbstand;
	private final int strassenFassungsvermögen;
	private final Zeitspanne minimaleAmpelSchaltzeit;
	
	private final Zustand startZustand;
	private List<Zustand> zustandsHistorie;
	
	public Gott(Uhrzeit startZeit, List<Uhrzeit> autoUhrzeiten, List<Zeitspanne> autoStandzeiten, 
			int parkplatzMaximum, Ampel startAmpel, Zeitspanne baustellenZeit,
			Zeitspanne autoAbstand, int strassenFassungsvermögen, Zeitspanne minimaleAmpelSchaltzeit) {
		this.baustellenZeit = baustellenZeit;
		this.autoAbstand = autoAbstand;
		this.strassenFassungsvermögen = strassenFassungsvermögen;
		this.minimaleAmpelSchaltzeit = minimaleAmpelSchaltzeit;
		
		this.startZustand = new Zustand(startZeit, autoUhrzeiten, autoStandzeiten, parkplatzMaximum, startAmpel);
		this.zustandsHistorie = new ArrayList<Zustand>();
	}

	@Override
	public Iterator<Zustand> iterator() {
		return new ZustandsIter();
	}
	
	private class ZustandsIter implements Iterator<Zustand> {

		@Override
		public boolean hasNext() {
			Zustand current = (zustandsHistorie.size() == 0 ? null : zustandsHistorie.get(zustandsHistorie.size()-1));
			Zustand next = (zustandsHistorie.size() == 0 ? startZustand : Zustand.naechsterZustand(zustandsHistorie.get(zustandsHistorie.size()-1),
					baustellenZeit, autoAbstand, strassenFassungsvermögen, minimaleAmpelSchaltzeit));
			
			return current != next;
		}

		@Override
		public Zustand next() {
			Zustand next = (zustandsHistorie.size() == 0 ? startZustand : Zustand.naechsterZustand(zustandsHistorie.get(zustandsHistorie.size()-1),
							baustellenZeit, autoAbstand, strassenFassungsvermögen, minimaleAmpelSchaltzeit));
			zustandsHistorie.add(next);
			return next;
		}

		@Override
		public void remove() {
			if (zustandsHistorie.size() > 0) zustandsHistorie.remove(zustandsHistorie.size()-1);		
		}
	}
}
