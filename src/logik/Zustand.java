package logik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logik.interfaces.Uhrzeit;
import logik.interfaces.Warteschlange;
import logik.interfaces.Zeitspanne;

public class Zustand {
	private final Uhrzeit zeit;
	private final List<Uhrzeit> autoUhrzeiten;
	private final int einfahrt;
	private final Warteschlange baustelle;
	private final Warteschlange parkplatz;
	private final List<Zeitspanne> autoStandzeiten;
	private final int ausfahrt;
	private final Ampel ampel;
	private final Uhrzeit letzteAmpelSchaltzeit;
	
	public Zustand(Uhrzeit startZeit, List<Uhrzeit> autoUhrzeiten, List<Zeitspanne> autoStandzeiten, 
			int parkplatzMaximum, Ampel ampel) {
		this.zeit = startZeit;
		this.autoUhrzeiten = autoUhrzeiten;
		this.einfahrt = 0;
		this.baustelle = new WarteschlangeImpl(parkplatzMaximum);
		this.parkplatz = new WarteschlangeImpl(parkplatzMaximum);
		this.autoStandzeiten = autoStandzeiten;
		this.ausfahrt = 0;
		this.ampel = ampel;
		this.letzteAmpelSchaltzeit = startZeit;
	}
	
	public Zustand(Uhrzeit zeit, List<Uhrzeit> autoUhrzeiten, int einfahrt,
			Warteschlange baustelle, Warteschlange parkplatz, List<Zeitspanne> autoStandzeiten, 
			int ausfahrt, Ampel ampel, Uhrzeit letzteAmpelSchaltzeit) {
		this.zeit = zeit;
		this.autoUhrzeiten = autoUhrzeiten;
		this.einfahrt = einfahrt;
		this.baustelle = baustelle;
		this.parkplatz = parkplatz;
		this.autoStandzeiten = autoStandzeiten;
		this.ausfahrt = ausfahrt;
		this.ampel = ampel;
		this.letzteAmpelSchaltzeit = letzteAmpelSchaltzeit;
	}
	
	public Uhrzeit zeit() {
		return zeit;
	}

	public List<Uhrzeit> autoUhrzeiten() {
		return autoUhrzeiten;
	}

	public int einfahrt() {
		return einfahrt;
	}

	public Warteschlange baustelle() {
		return baustelle;
	}

	public Warteschlange parkplatz() {
		return parkplatz;
	}

	public List<Zeitspanne> autoStandzeiten() {
		return autoStandzeiten;
	}

	public int ausfahrt() {
		return ausfahrt;
	}

	public Ampel ampel() {
		return ampel;
	}

	public Uhrzeit letzteAmpelSchaltzeit() {
		return letzteAmpelSchaltzeit;
	}

	@Override
	public String toString() {
		return "Zustand "+zeit+": STR "+autoUhrzeiten.size()+" | EIN "+einfahrt+" | BAU "+
			baustelle.laenge()+" | PAR "+parkplatz.laenge()+" [STA "+ autoStandzeiten.size()+
			"] | AUS "+ausfahrt+" || "+ampel;
	}
	
	public static Zustand naechsterZustand(Zustand alterZustand, Zeitspanne baustellenZeit,
			Zeitspanne autoAbstand, int strassenFassungsverm�gen, Zeitspanne minimaleAmpelSchaltzeit) {
		
			Uhrzeit naechsteZeit = naechsteZeit(alterZustand, baustellenZeit, autoAbstand, strassenFassungsverm�gen, minimaleAmpelSchaltzeit);
			// nix passiert?
			if (alterZustand.zeit == naechsteZeit) return alterZustand;
			
			List<Uhrzeit> naechsteAutoUhrzeiten = naechsteAutoUhrzeiten(alterZustand, naechsteZeit, strassenFassungsverm�gen);
			int naechsteEinfahrt = naechsteEinfahrt(alterZustand, naechsteZeit, autoAbstand, strassenFassungsverm�gen);
			Warteschlange naechsteBaustelle = naechsteBaustelle(alterZustand, naechsteZeit, autoAbstand, baustellenZeit);
			Warteschlange naechsterParkplatz = naechsterParkplatz(alterZustand, naechsteZeit, baustellenZeit, strassenFassungsverm�gen);
			List<Zeitspanne> naechsteAutoStandzeiten = naechsteAutoStandzeiten(alterZustand, naechsteZeit, naechsterParkplatz, baustellenZeit, strassenFassungsverm�gen);
			int naechsteAusfahrt = naechsteAusfahrt(alterZustand, naechsteZeit, autoAbstand, strassenFassungsverm�gen);
			Ampel naechsteAmpel = naechsteAmpel(alterZustand, naechsteZeit, minimaleAmpelSchaltzeit);
			Uhrzeit naechsteAmpelSchaltzeit = naechsteAmpelSchaltzeit(alterZustand, naechsteZeit, naechsteAmpel);
		
		return new Zustand(naechsteZeit, naechsteAutoUhrzeiten, naechsteEinfahrt, naechsteBaustelle,
				naechsterParkplatz, naechsteAutoStandzeiten, naechsteAusfahrt, naechsteAmpel,
				naechsteAmpelSchaltzeit);
	}

	private static Uhrzeit naechsteZeit(Zustand alterZustand, Zeitspanne baustellenZeit,
			Zeitspanne autoAbstand, int strassenFassungsverm�gen, Zeitspanne minimaleAmpelSchaltzeit) {
		List<Uhrzeit> zeiten = new ArrayList<Uhrzeit>();
		
		// auf die Einfahrt? Einfahrt voll?
		if (alterZustand.autoUhrzeiten.size() > 0 && alterZustand.einfahrt < strassenFassungsverm�gen)
			zeiten.add(alterZustand.autoUhrzeiten.get(0));
		
		// auf die Baustelle? Ampel gr�n? Letztes Auto in der Baustelle >= 3s weg?
		if (alterZustand.einfahrt > 0 && alterZustand.ampel == Ampel.EINFAHRT && (alterZustand.baustelle.laenge() == 0 ||
				alterZustand.baustelle.letzter().addiere(autoAbstand).compareTo(alterZustand.zeit) > -1))
			zeiten.add(alterZustand.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT));			
		
		// auf den Parkplatz? 
		if (alterZustand.baustelle.laenge() > 0)
			zeiten.add(alterZustand.baustelle.naechster().addiere(baustellenZeit));
		
		// auf die Ausfahrt? Ausfahrt voll?
		if (alterZustand.parkplatz.laenge() > 0 && alterZustand.ausfahrt < strassenFassungsverm�gen) 
			zeiten.add(alterZustand.parkplatz.naechster());
		
		// auf die Baustelle? Ampel gr�n? Letztes Auto in der Baustelle >= 3s weg?
		if (alterZustand.ausfahrt > 0 && alterZustand.ampel == Ampel.AUSFAHRT && (alterZustand.baustelle.laenge() == 0 ||
				alterZustand.baustelle.letzter().addiere(autoAbstand).compareTo(alterZustand.zeit) > -1))
			zeiten.add(alterZustand.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT));		
		
		// Autos auf der Ein/Ausfahrt und Ampel auf Stopp?
		if ((alterZustand.einfahrt > 0 || alterZustand.ausfahrt > 0) && (alterZustand.ampel == Ampel.STOP_EINFAHRT ||
				alterZustand.ampel == Ampel.STOP_AUSFAHRT) && alterZustand.baustelle.laenge() == 0)
			zeiten.add(alterZustand.letzteAmpelSchaltzeit.addiere(minimaleAmpelSchaltzeit));
		
		// sortieren
		Collections.sort(zeiten);
		return (zeiten.size() > 0 ? zeiten.get(0) : alterZustand.zeit);
	}
	
	private static List<Uhrzeit> naechsteAutoUhrzeiten(Zustand alterZustand, Uhrzeit naechsteZeit, int strassenFassungsverm�gen) {
		if (alterZustand.autoUhrzeiten.contains(naechsteZeit) && alterZustand.einfahrt < strassenFassungsverm�gen) {
			List<Uhrzeit> zeiten = new ArrayList<Uhrzeit>();
			zeiten.addAll(alterZustand.autoUhrzeiten);
			zeiten.remove(naechsteZeit);
			return zeiten;
		}		
		return alterZustand.autoUhrzeiten;
	}
	
	private static int naechsteEinfahrt(Zustand alterZustand, Uhrzeit naechsteZeit, Zeitspanne autoAbstand, int strassenFassungsverm�gen) {
		int einfahrt = alterZustand.einfahrt;
		
		// Stra�e -> Einfahrt
		if (alterZustand.autoUhrzeiten.contains(naechsteZeit) && alterZustand.einfahrt < strassenFassungsverm�gen) einfahrt++;
		// Einfahrt -> Baustelle
		if (alterZustand.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT).compareTo(naechsteZeit) == 0 && (alterZustand.baustelle.laenge() == 0 ||
				alterZustand.baustelle.letzter().addiere(autoAbstand).compareTo(naechsteZeit) > -1) && 
					alterZustand.ampel == Ampel.EINFAHRT && alterZustand.einfahrt > 0)	
			einfahrt--;
		
		return einfahrt;
	}

	private static Warteschlange naechsteBaustelle(Zustand alterZustand, Uhrzeit naechsteZeit, Zeitspanne autoAbstand, Zeitspanne baustellenZeit) {
		Warteschlange baustelle = alterZustand.baustelle;
		
		// Einfahrt/Ausfahrt -> Baustelle
		if (alterZustand.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT).compareTo(naechsteZeit) == 0 &&
			(alterZustand.baustelle.laenge() == 0 || alterZustand.baustelle.letzter().addiere(autoAbstand).compareTo(naechsteZeit) > -1) && 
				(alterZustand.ampel == Ampel.EINFAHRT && alterZustand.einfahrt > 0) ||
					(alterZustand.ampel == Ampel.AUSFAHRT && alterZustand.ausfahrt > 0)) {		
			baustelle = baustelle.hinzufuegen(naechsteZeit);
		}
		
		// Baustelle -> Parkplatz/Strasse
		if (alterZustand.baustelle.laenge() > 0 && alterZustand.baustelle.naechster().addiere(baustellenZeit).compareTo(naechsteZeit) < 1) {
			baustelle = baustelle.entfernen();
		}
		
		return baustelle;
	}

	private static Warteschlange naechsterParkplatz(Zustand alterZustand, Uhrzeit naechsteZeit, Zeitspanne baustellenZeit, int strassenFassungsverm�gen) {
		Warteschlange parkplatz = alterZustand.parkplatz;
		
		// Baustelle -> Parkplatz
		if (alterZustand.baustelle.laenge() > 0 && (alterZustand.ampel == Ampel.EINFAHRT || alterZustand.ampel == Ampel.STOP_AUSFAHRT) &&
				alterZustand.baustelle.naechster().addiere(baustellenZeit).compareTo(naechsteZeit) == 0) {
			parkplatz = parkplatz.hinzufuegen(naechsteZeit.addiere(alterZustand.autoStandzeiten.get(0)));
		}
		
		// Parkplatz -> Ausfahrt
		if (alterZustand.parkplatz.laenge() > 0 && alterZustand.parkplatz.naechster().compareTo(naechsteZeit) == 0 &&
				alterZustand.ausfahrt < strassenFassungsverm�gen) {
			parkplatz = parkplatz.entfernen();
		}
		
		return parkplatz;
	}

	private static List<Zeitspanne> naechsteAutoStandzeiten(Zustand alterZustand, Uhrzeit naechsteZeit, Warteschlange naechsterParkplatz, Zeitspanne baustellenZeit,
			int strassenFassungsverm�gen) {
		if (alterZustand.baustelle.laenge() > 0 && (alterZustand.ampel == Ampel.EINFAHRT || alterZustand.ampel == Ampel.STOP_AUSFAHRT) && 
				alterZustand.baustelle.naechster().addiere(baustellenZeit).compareTo(naechsteZeit) == 0) {
			List<Zeitspanne> zeiten = new ArrayList<Zeitspanne>();
			zeiten.addAll(alterZustand.autoStandzeiten);
			zeiten.remove(0);
			return zeiten;
		}
		return alterZustand.autoStandzeiten;
	}

	private static int naechsteAusfahrt(Zustand alterZustand, Uhrzeit naechsteZeit, Zeitspanne autoAbstand, int strassenFassungsverm�gen) {
		int ausfahrt = alterZustand.ausfahrt;
		
		// Parkplatz -> Ausfahrt
		if (alterZustand.parkplatz.laenge() > 0 && alterZustand.parkplatz.naechster().compareTo(naechsteZeit) == 0 &&
				alterZustand.ausfahrt < strassenFassungsverm�gen) ausfahrt++;
		
		// Ausfahrt -> Baustelle
		if (alterZustand.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT).compareTo(naechsteZeit) == 0 &&
				(alterZustand.baustelle.laenge() == 0 || alterZustand.baustelle.letzter().addiere(autoAbstand).compareTo(naechsteZeit) > -1) && 
				(alterZustand.ampel == Ampel.AUSFAHRT || alterZustand.ampel == Ampel.STOP_EINFAHRT) && alterZustand.ausfahrt > 0) {
						
				ausfahrt--;
		}
		
		return ausfahrt;
	}

	private static Ampel naechsteAmpel(Zustand alterZustand, Uhrzeit naechsteZeit, Zeitspanne minimaleAmpelSchaltzeit) {
		return Ampel.naechsteAmpel(alterZustand.ampel, alterZustand.einfahrt,
				 alterZustand.baustelle.laenge(),  alterZustand.parkplatz.laenge(), 
				 alterZustand.parkplatz.maximaleElemente(),  alterZustand.ausfahrt,
				 alterZustand.letzteAmpelSchaltzeit, naechsteZeit, minimaleAmpelSchaltzeit);
	}

	private static Uhrzeit naechsteAmpelSchaltzeit(Zustand alterZustand, Uhrzeit naechsteZeit, Ampel naechsteAmpel) {
		return (alterZustand.ampel == naechsteAmpel ? alterZustand.letzteAmpelSchaltzeit : naechsteZeit);
	}
}
