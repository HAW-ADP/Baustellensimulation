package gui.logik;

public class ZustandsUmgebung {
	
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
	
	/** Die Dauer der Simulation (10:00am bis 08:00pm) */
	private static final Uhrzeit oeffnungsZeit = new UhrzeitImpl(10,0,0);
	private static final Uhrzeit ladenschlussZeit = new UhrzeitImpl(20,0,0);
	
	public ZustandsUmgebung(Uhrzeit autoAbstandzeit, Uhrzeit baustellenPassierZeit, Uhrzeit minParkdauer,
							Uhrzeit maxParkdauer, Uhrzeit minAutoAnkunft, Uhrzeit maxAutoAnkunft,
							Uhrzeit maximaleRotPhase, int parkplatzKapazitaet, int einfahrtKapazitaet){
		
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

	public Uhrzeit getOeffnungszeit() {
		return oeffnungsZeit.copy();
	}

	public Uhrzeit getLadenschlusszeit() {
		return ladenschlussZeit.copy();
	}

	public Uhrzeit getAutoAbstandzeit() {
		return autoAbstandzeit.copy();
	}

	public Uhrzeit getBaustellenPassierZeit() {
		return baustellenPassierZeit.copy();
	}

	public Uhrzeit getMinParkdauer() {
		return minParkdauer.copy();
	}

	public Uhrzeit getMaxParkdauer() {
		return maxParkdauer.copy();
	}

	public Uhrzeit getMinAutoAnkunft() {
		return minAutoAnkunft.copy();
	}

	public Uhrzeit getMaxAutoAnkunft() {
		return maxAutoAnkunft.copy();
	}

	public Uhrzeit getMaximaleRotPhase() {
		return maximaleRotPhase.copy();
	}

	public int getParkplatzKapazitaet() {
		return parkplatzKapazitaet;
	}

	public int getEinfahrtKapazitaet() {
		return einfahrtKapazitaet;
	}
}
