package gui.logik;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class ZustandImpl implements Zustand {
	
	/** Die Anzahl der Autos in den jeweiligen Positionen: parkend, auf Ausfahrt wartend, auf einfahrt warten */
	private final int anzahlAusfahrtAutos;
	private final int anzahlEinfahrtAutos;
	
	/** Der aktuelle Ampelzustand, Einfahrt = 1, STOP_EINFAHRT = 2, AUSFAHRT = 3, STOP_AUSFAHRT = 4 */
	private final int ampelzustand;
	
	/** Die Zeit, die der aktuelle Ampelzustand erhalten bleibt */
	private final Uhrzeit ampelzustandZeit;
	
	/** Die Uhrzeit zu der sich die Simulation in diesem Zustand befindet */
	private final Uhrzeit uhrzeit;
	
	/** Enthaelt die Uhrzeiten zu denen die Autos auf dem Parkplatz diesen wieder verlassen */
	private final Queue<Uhrzeit> uhrzeitListeParkplatz;
	
	/** Enthaelt die Dauern zu denen die Autos in der Baustelle diese wieder verlassen */
	private final Queue<Uhrzeit> uhrzeitListeBaustelle;
	
	/** Enthaelt die Zeit zu denr das naechste Auto in die Einfahrt faehrt */
	private final Uhrzeit naechsteAutoEinfahrt;
	
	/** Enthaelt die Zeit, zu der das letzte Auto in die Baustelle hineingefahren ist */
	private final Uhrzeit letzteBaustellenEinfahrt;
	
	/** Benutzerdefinierte Simulationskonstanten */
	private final Uhrzeit autoAbstandzeit;
	private final Uhrzeit baustellenPassierZeit;
	
	/** Das Intervall, das den Einkaufszeitraum beschreibt */
	private final Uhrzeit minParkdauer;
	private final Uhrzeit maxParkdauer;
	
	/** Das Intervall/die Frequenz in der neue Autos auf den Parkplatz fahren wollen */
	private final Uhrzeit minAutoAnkunft;
	private final Uhrzeit maxAutoAnkunft;
	
	/** Die maximale Zeitlaenge, die eine Rotphase dauern darf */
	private final Uhrzeit maximaleRotPhase;
	
	/** Die Kapazitaeten der Einfahrt und die maximale Anzahl an verfuegbaren Parkplaetzen */
	private final int parkplatzKapazitaet;
	private final int einfahrtKapazitaet;
	
	public ZustandImpl(int anzahlAusfahrtAutos, int anzahlEinfahrtAutos,
					   int ampelzustand, Uhrzeit ampelzustandZeit, Uhrzeit uhrzeit, Queue<Uhrzeit> uhrzeitListeParkplatz,
					   Queue<Uhrzeit> uhrzeitListeBaustelle, Uhrzeit naechsteAutoEinfahrt, Uhrzeit letzteBaustellenEinfahrt,
					   Uhrzeit autoAbstandzeit, Uhrzeit baustellenPassierZeit, Uhrzeit minParkdauer,
					   Uhrzeit maxParkdauer, Uhrzeit minAutoAnkunft, Uhrzeit maxAutoAnkunft, Uhrzeit maximaleRotPhase,
					   int parkplatzKapazitaet, int einfahrtKapazitaet){
		
		this.anzahlAusfahrtAutos = anzahlAusfahrtAutos;
		this.anzahlEinfahrtAutos = anzahlEinfahrtAutos;
		this.ampelzustand = ampelzustand;
		this.ampelzustandZeit = ampelzustandZeit;
		this.uhrzeit = uhrzeit;
		this.uhrzeitListeParkplatz = uhrzeitListeParkplatz;
		this.uhrzeitListeBaustelle = uhrzeitListeBaustelle;
		this.naechsteAutoEinfahrt = naechsteAutoEinfahrt;
		this.letzteBaustellenEinfahrt = letzteBaustellenEinfahrt;
		this.autoAbstandzeit = autoAbstandzeit;
		this.baustellenPassierZeit = baustellenPassierZeit;
		this.minParkdauer = minParkdauer;
		this.maxParkdauer = maxParkdauer;
		this.minAutoAnkunft = minAutoAnkunft;
		this.maxAutoAnkunft = maxAutoAnkunft;
		this.maximaleRotPhase = maximaleRotPhase;
		this.parkplatzKapazitaet = parkplatzKapazitaet;
		this.einfahrtKapazitaet = einfahrtKapazitaet;
	}
	
	public Zustand naechsterZustand(){
		
		Uhrzeit neueUhrzeit = naechsteUhrzeit();
		int naechsterAmpelzustand = (neueUhrzeit.compareTo(ampelzustandZeit)==0)? naechsterAmpelzustand() : ampelzustand;
		Uhrzeit neueAmpelzustandZeit = (ampelzustand != naechsterAmpelzustand) ? naechsteAmpelzustandZeit(naechsterAmpelzustand, neueUhrzeit) : ampelzustandZeit;
		
		
		int neueAnzahlEinfahrtAutos = anzahlEinfahrtAutos;
		int neueAnzahlAusfahrtAutos = anzahlAusfahrtAutos;
		Uhrzeit neueLetzteBaustellenEinfahrt = letzteBaustellenEinfahrt.copy();
		Queue<Uhrzeit> neueUhrzeitListeBaustelle = new PriorityQueue<Uhrzeit>();
		Queue<Uhrzeit> neueUhrzeitListeParkplatz = new PriorityQueue<Uhrzeit>();
		for(Uhrzeit zeit : uhrzeitListeBaustelle){ neueUhrzeitListeBaustelle.add(zeit.copy()); }
		for(Uhrzeit zeit : uhrzeitListeParkplatz){ neueUhrzeitListeParkplatz.add(zeit.copy()); }
		
		Uhrzeit neueNaechsteAutoEinfahrt = naechsteAutoEinfahrt;
		
		Random rand = new Random();
		
		/** Auto faehrt von Einfahrt in Baustelle */
		if(Zustand.EINFAHRT == naechsterAmpelzustand && anzahlEinfahrtAutos > 0  && letzteBaustellenEinfahrt.compareTo(neueUhrzeit) <= -3){
			neueAnzahlEinfahrtAutos--;
			neueUhrzeitListeBaustelle.add(uhrzeit.addiere(baustellenPassierZeit).addiere(autoAbstandzeit));
			neueLetzteBaustellenEinfahrt = uhrzeit;
			neueUhrzeit = uhrzeit.addiere(autoAbstandzeit);
			System.out.println("Neue Uhrzeit: " + neueUhrzeit);
			System.out.println("baustellen-if");
			
		
		/** Auto faehrt von Ausfahrt in Baustelle */
		} else if(Zustand.AUSFAHRT == naechsterAmpelzustand && anzahlAusfahrtAutos > 0 && letzteBaustellenEinfahrt.compareTo(neueUhrzeit) <= -3){
			neueAnzahlAusfahrtAutos--;
			neueUhrzeitListeBaustelle.add(uhrzeit.addiere(baustellenPassierZeit).addiere(autoAbstandzeit));
			neueLetzteBaustellenEinfahrt = uhrzeit;
			neueUhrzeit = uhrzeit.addiere(autoAbstandzeit);
			System.out.println("Neue Uhrzeit: " + neueUhrzeit);
			System.out.println("baustellen-if");
		}
		
		/** Autos verlassen Baustelle in beide Richtungen */
		if(!neueUhrzeitListeBaustelle.isEmpty() && neueUhrzeitListeBaustelle.peek().compareTo(neueUhrzeit)==0){
			neueUhrzeitListeBaustelle.poll();
			if(Zustand.EINFAHRT == naechsterAmpelzustand || Zustand.STOP_EINFAHRT == naechsterAmpelzustand){
				int zufallsParkdauer = rand.nextInt(maxParkdauer.gesamtZeitSekunden() - minParkdauer.gesamtZeitSekunden());
				Uhrzeit neueParkdauer = neueUhrzeit.addiere(minParkdauer.addiere(new UhrzeitImpl(zufallsParkdauer)));
				neueUhrzeitListeParkplatz.add(neueParkdauer);
			}
		}
		
		/** Auto reiht sich in Einfahrt ein oder faehrt weiter
		 *  Der Zeitpunkt der naechsten Autoankunft wird bestimmt */
		if(naechsteAutoEinfahrt.compareTo(neueUhrzeit)==0){
			neueNaechsteAutoEinfahrt = neueNaechsteAutoEinfahrt(neueUhrzeit);
			if(neueAnzahlEinfahrtAutos < einfahrtKapazitaet)
				neueAnzahlEinfahrtAutos++;
		}
		
		
		/** Autos verlassen Parkplatz -> Ausfahrt */
		while(!neueUhrzeitListeParkplatz.isEmpty() && neueUhrzeitListeParkplatz.peek().compareTo(neueUhrzeit)==0){
			neueUhrzeitListeParkplatz.poll();
			neueAnzahlAusfahrtAutos++;
		}
				
		/** Den naechsten Zustand mit den neu berechneten Werten zurueckgeben */
		
		return new ZustandImpl(neueAnzahlAusfahrtAutos, neueAnzahlEinfahrtAutos, naechsterAmpelzustand,
				 neueAmpelzustandZeit ,neueUhrzeit, neueUhrzeitListeParkplatz,
				 neueUhrzeitListeBaustelle, neueNaechsteAutoEinfahrt, neueLetzteBaustellenEinfahrt,
				 autoAbstandzeit.copy(), baustellenPassierZeit.copy(), minParkdauer.copy(), maxParkdauer.copy(),
				 minAutoAnkunft.copy(), maxAutoAnkunft.copy(), maximaleRotPhase.copy(), parkplatzKapazitaet, einfahrtKapazitaet);
		
	}
	
	/** Gibt je nach der Simulationssituation EINFAHRT, AUSFAHRT, STOP_EINFAHRT oder STOP_AUSFAHRT zurueck
	 *  sowie die Dauer dieses naechsten Zustands */
	//TODO: Ampelalgorithmus
	private int naechsterAmpelzustand(){
		
		switch(ampelzustand){
			case(Zustand.EINFAHRT): return Zustand.STOP_EINFAHRT;
			case(Zustand.STOP_EINFAHRT): return Zustand.AUSFAHRT;
			case(Zustand.AUSFAHRT): return Zustand.STOP_AUSFAHRT;
			case(Zustand.STOP_AUSFAHRT): return Zustand.EINFAHRT;
			default: return STOP_AUSFAHRT;
		}
	}
	
	//TODO: Ampelschaltdauer-Algorithmus
	private Uhrzeit naechsteAmpelzustandZeit(int neuerAmpelzustand, Uhrzeit neueUhrzeit){
		switch(neuerAmpelzustand){
			case(Zustand.EINFAHRT): return neueUhrzeit.addiere(maximaleRotPhase);
			case(Zustand.STOP_EINFAHRT): return neueUhrzeit.addiere(baustellenPassierZeit);
			case(Zustand.AUSFAHRT): return neueUhrzeit.addiere(maximaleRotPhase);
			case(Zustand.STOP_AUSFAHRT): return neueUhrzeit.addiere(baustellenPassierZeit);
			default: return neueUhrzeit.addiere(maximaleRotPhase);
		}
	}
	
	private Uhrzeit neueNaechsteAutoEinfahrt(Uhrzeit neueUhrzeit){
		Random rand = new Random();

		int minAnkunft = minAutoAnkunft.gesamtZeitSekunden();
		int maxAnkunft = maxAutoAnkunft.gesamtZeitSekunden();
		
		int naechsteAutoEinfahrtInt = minAnkunft + rand.nextInt(maxAnkunft - minAnkunft);
		
		return new UhrzeitImpl(naechsteAutoEinfahrtInt).addiere(neueUhrzeit);
	}
	
	/** Berechnet die Uhrzeit, in der ein neuer Zustand auftritt */
	private Uhrzeit naechsteUhrzeit(){
		
		Queue<Uhrzeit> pq = new PriorityQueue<Uhrzeit>();
		pq.add(ampelzustandZeit);
		pq.add(naechsteAutoEinfahrt);
		
		Uhrzeit naechsteBaustellenAusfahrt = uhrzeitListeBaustelle.peek();
		if(naechsteBaustellenAusfahrt != null)
			pq.add(naechsteBaustellenAusfahrt);
		
		Uhrzeit naechsteParkplatzAusfahrt = uhrzeitListeParkplatz.peek();
		if(naechsteParkplatzAusfahrt != null)
			pq.add(naechsteParkplatzAusfahrt);
		
		return new UhrzeitImpl(pq.poll().gesamtZeitSekunden());

	}
	
	@Override
	public String toString(){
		return "SimulationsZustand um " + uhrzeit + "\n" +
				"| Autos auf Einfahrt: " + anzahlEinfahrtAutos + "\n" +
				"| Autos in Baustelle: " + uhrzeitListeBaustelle.size() + "\n" +
				"| Autos auf Ausfahrt: " + anzahlAusfahrtAutos + "\n" +
				"| Autos auf Parkplatz:" + uhrzeitListeParkplatz.size() + "\n" +
				"| Ampelzustand: " + ampelzustand + "\n" +
				"| Baustellenqueue: " + uhrzeitListeBaustelle + "\n" +
				"| Parkplatzqueue: " + uhrzeitListeParkplatz + "\n" +
				"| Zeit bis zur naechsten Ampelschaltung: " + ampelzustandZeit + "\n" +
				"| Naechstes Auto um: " + naechsteAutoEinfahrt + "\n";
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