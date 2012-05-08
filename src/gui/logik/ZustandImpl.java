package gui.logik;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Iterator;

public class ZustandImpl implements Zustand {

	/**
	 * Die Anzahl der Autos in den jeweiligen Positionen: parkend, auf Ausfahrt
	 * wartend, auf einfahrt warten
	 */
	private final int anzahlAusfahrtAutos;
	private final int anzahlEinfahrtAutos;

	/**
	 * Der aktuelle Ampelzustand, Einfahrt = 1, STOP_EINFAHRT = 2, AUSFAHRT = 3,
	 * STOP_AUSFAHRT = 4
	 */
	private final int ampelzustand;

	/** Die Zeit, die der aktuelle Ampelzustand erhalten bleibt */
	private final Uhrzeit ampelzustandZeit;

	/** Die Uhrzeit zu der sich die Simulation in diesem Zustand befindet */
	private final Uhrzeit uhrzeit;

	/**
	 * Enthaelt die Uhrzeiten zu denen die Autos auf dem Parkplatz diesen wieder
	 * verlassen
	 */
	private final Queue<Uhrzeit> uhrzeitListeParkplatz;

	/**
	 * Enthaelt die Dauern zu denen die Autos in der Baustelle diese wieder
	 * verlassen
	 */
	private final Queue<Uhrzeit> uhrzeitListeBaustelle;

	/** Enthaelt die Zeit zu denr das naechste Auto in die Einfahrt faehrt */
	private final Uhrzeit naechsteAutoEinfahrt;

	/**
	 * Enthaelt die Zeit, zu der das letzte Auto in die Baustelle hineingefahren
	 * ist
	 */
	private final Uhrzeit letzteBaustellenEinfahrt;

	/** Enthaelt saemtliche Simulationsumgebungskonstanten */
	private final ZustandsUmgebung umgebung;

	public ZustandImpl(int anzahlAusfahrtAutos, int anzahlEinfahrtAutos,
			int ampelzustand, Uhrzeit ampelzustandZeit, Uhrzeit uhrzeit,
			Queue<Uhrzeit> uhrzeitListeParkplatz,
			Queue<Uhrzeit> uhrzeitListeBaustelle, Uhrzeit naechsteAutoEinfahrt,
			Uhrzeit letzteBaustellenEinfahrt, ZustandsUmgebung umgebung) {

		this.anzahlAusfahrtAutos = anzahlAusfahrtAutos;
		this.anzahlEinfahrtAutos = anzahlEinfahrtAutos;
		this.ampelzustand = ampelzustand;
		this.ampelzustandZeit = ampelzustandZeit;
		this.uhrzeit = uhrzeit;
		this.uhrzeitListeParkplatz = uhrzeitListeParkplatz;
		this.uhrzeitListeBaustelle = uhrzeitListeBaustelle;
		this.naechsteAutoEinfahrt = naechsteAutoEinfahrt;
		this.letzteBaustellenEinfahrt = letzteBaustellenEinfahrt;
		this.umgebung = umgebung;
	}

	public Zustand naechsterZustand() {

		IOSystem.loggeZustand(this);

		Uhrzeit neueUhrzeit = naechsteUhrzeit();
		Uhrzeit neueAmpelzustandZeit = ampelzustandZeit;
		int naechsterAmpelzustand = ampelzustand;

		int neueAnzahlEinfahrtAutos = anzahlEinfahrtAutos;
		int neueAnzahlAusfahrtAutos = anzahlAusfahrtAutos;
		Uhrzeit neueLetzteBaustellenEinfahrt = letzteBaustellenEinfahrt.copy();
		Queue<Uhrzeit> neueUhrzeitListeBaustelle = new PriorityQueue<Uhrzeit>();
		Queue<Uhrzeit> neueUhrzeitListeParkplatz = new PriorityQueue<Uhrzeit>();
		for (Uhrzeit zeit : uhrzeitListeBaustelle) {
			neueUhrzeitListeBaustelle.add(zeit.copy());
		}
		for (Uhrzeit zeit : uhrzeitListeParkplatz) {
			neueUhrzeitListeParkplatz.add(zeit.copy());
		}

		Uhrzeit neueNaechsteAutoEinfahrt = naechsteAutoEinfahrt;

		Random rand = new Random();

		/** Auto faehrt von Einfahrt in Baustelle */
		if (Zustand.EINFAHRT == naechsterAmpelzustand
				&& anzahlEinfahrtAutos > 0
				&& letzteBaustellenEinfahrt.compareTo(neueUhrzeit) <= (-1 * umgebung
						.getAutoAbstandzeit().gesamtZeitSekunden())) {
			neueAnzahlEinfahrtAutos--;

			System.out.println("AUTO Einfahrt => Baustelle");

			neueUhrzeitListeBaustelle.add(uhrzeit.addiere(umgebung
					.getBaustellenPassierZeit()));

			neueLetzteBaustellenEinfahrt = uhrzeit;
			Uhrzeit naechsteAutoZeit = uhrzeit.addiere(umgebung
					.getAutoAbstandzeit());
			neueUhrzeit = naechsteAutoZeit.compareTo(neueUhrzeit) < 0 ? naechsteAutoZeit
					: neueUhrzeit;

			/** Auto faehrt von Ausfahrt in Baustelle */
		} else if (Zustand.AUSFAHRT == naechsterAmpelzustand
				&& anzahlAusfahrtAutos > 0
				&& letzteBaustellenEinfahrt.compareTo(neueUhrzeit) <= (-1 * umgebung
						.getAutoAbstandzeit().gesamtZeitSekunden())) {
			neueAnzahlAusfahrtAutos--;

			neueUhrzeitListeBaustelle.add(uhrzeit.addiere(umgebung
					.getBaustellenPassierZeit()));

			neueLetzteBaustellenEinfahrt = uhrzeit;
			Uhrzeit naechsteAutoZeit = uhrzeit.addiere(umgebung
					.getAutoAbstandzeit());
			neueUhrzeit = naechsteAutoZeit.compareTo(neueUhrzeit) < 0 ? naechsteAutoZeit
					: neueUhrzeit;
		}

		naechsterAmpelzustand = (neueUhrzeit.compareTo(ampelzustandZeit) == 0) ? naechsterAmpelzustand()
				: ampelzustand;
		neueAmpelzustandZeit = (ampelzustand != naechsterAmpelzustand||naechsterAmpelzustand == Zustand.STOP_BEIDE) ? naechsteAmpelzustandZeit(
				naechsterAmpelzustand, neueUhrzeit) : ampelzustandZeit;

		if (neueAmpelzustandZeit.compareTo(neueUhrzeit) < 0)
			neueUhrzeit = neueAmpelzustandZeit;

		/** Autos verlassen Baustelle in beide Richtungen */
		if (!neueUhrzeitListeBaustelle.isEmpty()
				&& neueUhrzeitListeBaustelle.peek().compareTo(neueUhrzeit) == 0) {
			neueUhrzeitListeBaustelle.poll();
			if (Zustand.EINFAHRT == naechsterAmpelzustand
					|| Zustand.STOP_EINFAHRT == naechsterAmpelzustand) {
				int zufallsParkdauer = rand.nextInt(umgebung.getMaxParkdauer()
						.gesamtZeitSekunden()
						- umgebung.getMinParkdauer().gesamtZeitSekunden());
				Uhrzeit neueParkdauer = neueUhrzeit.addiere(umgebung
						.getMinParkdauer().addiere(
								new UhrzeitImpl(zufallsParkdauer)));
				neueUhrzeitListeParkplatz.add(neueParkdauer);
			}
		}

		/**
		 * Auto reiht sich in Einfahrt ein oder faehrt weiter Der Zeitpunkt der
		 * naechsten Autoankunft wird bestimmt
		 */
		if (naechsteAutoEinfahrt.compareTo(neueUhrzeit) == 0) {
			neueNaechsteAutoEinfahrt = neueNaechsteAutoEinfahrt(neueUhrzeit);
			if (neueAnzahlEinfahrtAutos < umgebung.getEinfahrtKapazitaet())
				neueAnzahlEinfahrtAutos++;
		}

		/** Autos verlassen Parkplatz -> Ausfahrt */
		while (!neueUhrzeitListeParkplatz.isEmpty()
				&& neueUhrzeitListeParkplatz.peek().compareTo(neueUhrzeit) == 0) {
			neueUhrzeitListeParkplatz.poll();
			neueAnzahlAusfahrtAutos++;
		}

		/** Den naechsten Zustand mit den neu berechneten Werten zurueckgeben */

		return new ZustandImpl(neueAnzahlAusfahrtAutos,
				neueAnzahlEinfahrtAutos, naechsterAmpelzustand,
				neueAmpelzustandZeit, neueUhrzeit, neueUhrzeitListeParkplatz,
				neueUhrzeitListeBaustelle, neueNaechsteAutoEinfahrt,
				neueLetzteBaustellenEinfahrt, umgebung);

	}

	/**
	 * Gibt je nach der Simulationssituation EINFAHRT, AUSFAHRT, STOP_EINFAHRT,
	 * STOP_AUSFAHRT oder STOP_BEIDE zurueck sowie die Dauer dieses naechsten
	 * Zustands
	 */
	private int naechsterAmpelzustand() {

		// Baumarkt geschlossen
		if ((uhrzeit.compareTo(umgebung.getLadenschlusszeit()) >= 0)
				&& uhrzeit.compareTo(umgebung.getOeffnungszeit()) < 0) {
			if (!(ampelzustand == Zustand.EINFAHRT))
				return Zustand.AUSFAHRT;
			else
				return Zustand.STOP_EINFAHRT;
		}

		// Parkplatz voll
		if (uhrzeitListeParkplatz.size() >= (umgebung.getParkplatzKapazitaet() * 9 / 10)) {
			if (!(ampelzustand == Zustand.EINFAHRT))
				return Zustand.STOP_BEIDE;
			else
				return Zustand.STOP_EINFAHRT;
		}

		// STOP_EINFAHRT -> EINFAHRT
		// wenn Autos nur in Einfahrt
		if (anzahlEinfahrtAutos > 0 && anzahlAusfahrtAutos == 0) {
			if (ampelzustand == Zustand.STOP_EINFAHRT)
				return Zustand.EINFAHRT;
		}

		// STOP_AUSFAHRT -> AUSFAHRT
		// wenn Autos nur in Ausfahrt
		if (anzahlEinfahrtAutos == 0 && anzahlAusfahrtAutos > 0) {
			if (ampelzustand == Zustand.STOP_AUSFAHRT)
				return Zustand.AUSFAHRT;
			if (ampelzustand == Zustand.STOP_BEIDE)
				return Zustand.AUSFAHRT;
		}

		// STOP_EINFAHRT/STOP_AUSFAHRT -> STOP_BEIDE
		if (anzahlEinfahrtAutos == 0 && anzahlAusfahrtAutos == 0) {
			if (ampelzustand == Zustand.STOP_EINFAHRT
					|| ampelzustand == Zustand.STOP_AUSFAHRT)
				return Zustand.STOP_BEIDE;
		}

		// wenn keiner dieser Fï¿½lle zutrifft, schaltet die Ampel normal
		switch (ampelzustand) {
		case (Zustand.EINFAHRT):
			return Zustand.STOP_EINFAHRT;
		case (Zustand.STOP_EINFAHRT):
			return Zustand.AUSFAHRT;
		case (Zustand.AUSFAHRT):
			return Zustand.STOP_AUSFAHRT;
		case (Zustand.STOP_AUSFAHRT):
			return Zustand.EINFAHRT;
		case (Zustand.STOP_BEIDE):
			return Zustand.AUSFAHRT;
		default:
			return STOP_AUSFAHRT;
		}
	}

	private Uhrzeit naechsteAmpelzustandZeit(int neuerAmpelzustand,
			Uhrzeit neueUhrzeit) {
		// Dauer des Zustandes Einfahrt berechnen
		Uhrzeit zeitEinfahrt = umgebung.getAutoAbstandzeit().multipliziere(
				anzahlEinfahrtAutos);
		// Dauer des Zustandes Ausfahrt berechnen
		Uhrzeit zeitAusfahrt = umgebung.getAutoAbstandzeit().multipliziere(
				anzahlAusfahrtAutos);

		// Überschuss an Autos
		int ueberschussEinfahrt = (anzahlEinfahrtAutos + uhrzeitListeParkplatz
				.size()) - umgebung.getParkplatzKapazitaet()*9/10;

		// gibt es einen ï¿½berschuss an Autos?
		// wenn ja (ï¿½berschuss > 0), muss der ï¿½berschuss von der Anzahl
		// der Autos in der Einfahrt/Ausfahrt abgezogen werden

		Uhrzeit maximaleGruenphase = new UhrzeitImpl(umgebung
				.getMaximaleRotPhase().gesamtZeitSekunden()
				- umgebung.getBaustellenPassierZeit().gesamtZeitSekunden());
		if (ueberschussEinfahrt > 0)
			zeitEinfahrt = umgebung.getAutoAbstandzeit().multipliziere(
					anzahlEinfahrtAutos - ueberschussEinfahrt);

		switch (neuerAmpelzustand) {
		case (Zustand.EINFAHRT):
			if (zeitEinfahrt.compareTo(maximaleGruenphase) > 0)
				return neueUhrzeit.addiere(maximaleGruenphase);
			else
				return neueUhrzeit.addiere(zeitEinfahrt);
		case (Zustand.STOP_EINFAHRT):
			return neueUhrzeit.addiere(umgebung.getBaustellenPassierZeit());
		case (Zustand.AUSFAHRT):
			if (zeitAusfahrt.compareTo(maximaleGruenphase) > 0)
				return neueUhrzeit.addiere(maximaleGruenphase);
			else
				return neueUhrzeit.addiere(zeitAusfahrt);
		case (Zustand.STOP_AUSFAHRT):
			return neueUhrzeit.addiere(umgebung.getBaustellenPassierZeit());
		case (Zustand.STOP_BEIDE):
			Uhrzeit naechste;
			if (uhrzeitListeParkplatz.size() >= (umgebung
					.getParkplatzKapazitaet() * 9 / 10)) {
				naechste = uhrzeitListeParkplatz.peek();
			} else {
				naechste = naechsteAutoEinfahrt;
				Uhrzeit curr = uhrzeitListeParkplatz.peek();

				if (curr != null && curr.compareTo(naechste) < 0) {
					naechste = curr;
				}
			}
			System.out.println("!!!!!!!!!!!!!!!!!STOP BEIDE: " + naechste);

			return naechste.addiere(umgebung.getAutoAbstandzeit());
		default:
			return neueUhrzeit.addiere(maximaleGruenphase);
		}
	}

	private Uhrzeit neueNaechsteAutoEinfahrt(Uhrzeit neueUhrzeit) {
		Random rand = new Random();

		if (neueUhrzeit.compareTo(umgebung.getLadenschlusszeit()) < 0) {
			int minAnkunft = umgebung.getMinAutoAnkunft().gesamtZeitSekunden();
			int maxAnkunft = umgebung.getMaxAutoAnkunft().gesamtZeitSekunden();

			int naechsteAutoEinfahrtInt = minAnkunft
					+ rand.nextInt(maxAnkunft - minAnkunft);

			return new UhrzeitImpl(naechsteAutoEinfahrtInt)
					.addiere(neueUhrzeit);
		}

		// Wenn es nach 20 Uhr ist, fahren keine Autos mehr auf den Parkplatz
		// 23:59:59 Uhr ist ein unerreichbarer Fuellwert
		return new UhrzeitImpl(23, 59, 59);
	}

	/** Berechnet die Uhrzeit, in der ein neuer Zustand auftritt */
	private Uhrzeit naechsteUhrzeit() {

		Queue<Uhrzeit> pq = new PriorityQueue<Uhrzeit>();
		pq.add(ampelzustandZeit);
		pq.add(naechsteAutoEinfahrt);

		Uhrzeit naechsteBaustellenAusfahrt = uhrzeitListeBaustelle.peek();
		if (naechsteBaustellenAusfahrt != null)
			pq.add(naechsteBaustellenAusfahrt);

		Uhrzeit naechsteParkplatzAusfahrt = uhrzeitListeParkplatz.peek();
		if (naechsteParkplatzAusfahrt != null)
			pq.add(naechsteParkplatzAusfahrt);

		return new UhrzeitImpl(pq.poll().gesamtZeitSekunden());

	}

	@Override
	public String toString() {
		return "SimulationsZustand um " + uhrzeit + "\n"
				+ "| Autos auf Einfahrt: " + anzahlEinfahrtAutos + "\n"
				+ "| Autos in Baustelle: " + uhrzeitListeBaustelle.size()
				+ "\n" + "| Autos auf Ausfahrt: " + anzahlAusfahrtAutos + "\n"
				+ "| Autos auf Parkplatz:" + uhrzeitListeParkplatz.size()
				+ "\n" + "| Ampelzustand: " + ampelzustand + "\n"
				+ "| Baustellenqueue: " + uhrzeitListeBaustelle + "\n"
				+ "| Parkplatzqueue: " + uhrzeitListeParkplatz + "\n"
				+ "| Zeit bis zur naechsten Ampelschaltung: "
				+ ampelzustandZeit + "\n" + "| Naechstes Auto um: "
				+ naechsteAutoEinfahrt + "\n";
	}

	@Override
	public Uhrzeit getUhrzeit() {
		return uhrzeit.copy();
	}

	@Override
	public int getAusfahrtAutos() {
		return anzahlAusfahrtAutos;
	}

	@Override
	public int getEinfahrtAutos() {
		return anzahlEinfahrtAutos;
	}

	@Override
	public int getAmpelzustand() {
		return ampelzustand;
	}

	@Override
	public int getParkplatzAutos() {
		return uhrzeitListeParkplatz.size();
	}

	@Override
	public int getBaustellenAutos() {
		return uhrzeitListeBaustelle.size();
	}
}