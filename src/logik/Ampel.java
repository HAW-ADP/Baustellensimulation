package logik;

import logik.interfaces.Uhrzeit;
import logik.interfaces.Zeitspanne;

public enum Ampel {
	EINFAHRT("Grün: auf den Parkplatz"),
	AUSFAHRT("Grün: runter vom Parkplatz"),
	STOP_EINFAHRT("Grün: niemand (hiernach folgt EINFAHRT)"),
	STOP_AUSFAHRT("Grün: niemand (hiernach folgt AUSFAHRT)");
	
	private String bezeichnung;
	
	private Ampel(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public String bezeichnung() {
		return this.bezeichnung;
	}
	
	public static Ampel parseString(String str) {
		if (str.contains("STOP_EINFAHRT")) return STOP_EINFAHRT;
		if (str.contains("STOP_AUSFAHRT")) return STOP_AUSFAHRT;
		if (str.contains("AUSFAHRT")) return AUSFAHRT;
		return EINFAHRT;
	}
	
	public static Ampel naechsteAmpel(Ampel derzeitigeAmpel, int einfahrtAnzahl, int baustellenAnzahl,
			int parkplatzAnzahl, int parkplatzMaximaleAnzahl, int ausfahrtAnzahl, Uhrzeit letzteSchaltzeit,
			Uhrzeit derzeitigeZeit, Zeitspanne minimaleSchaltzeit) {
		
		// minimaleSchaltzeit noch nicht vorbei (wenn auf grün)?
		if ((derzeitigeAmpel == EINFAHRT || derzeitigeAmpel == AUSFAHRT) && 
				derzeitigeZeit.addiere(minimaleSchaltzeit).compareTo(letzteSchaltzeit) < 0) return derzeitigeAmpel;
		
		// Ausfahrt > Einfahrt?
		if (derzeitigeAmpel == EINFAHRT && ausfahrtAnzahl > einfahrtAnzahl) return schalten(derzeitigeAmpel);
		
		// Parkplatz voll?
		if (derzeitigeAmpel == EINFAHRT && baustellenAnzahl + parkplatzAnzahl >= parkplatzMaximaleAnzahl) return schalten(derzeitigeAmpel);
		
		// Ausfahrt < Einfahrt?
		if (derzeitigeAmpel == AUSFAHRT && ausfahrtAnzahl < einfahrtAnzahl && baustellenAnzahl + parkplatzAnzahl < parkplatzMaximaleAnzahl) return schalten(derzeitigeAmpel);
		
		// rot und Baustelle leer?
		if ((derzeitigeAmpel == STOP_EINFAHRT || derzeitigeAmpel == STOP_AUSFAHRT) && baustellenAnzahl == 0) return schalten(derzeitigeAmpel);
		
		// nix passiert
		return derzeitigeAmpel;
	}
	
	private static Ampel schalten(Ampel ampel) {
		return (ampel == EINFAHRT ? STOP_AUSFAHRT : 
			ampel == STOP_AUSFAHRT ? AUSFAHRT :
				ampel == AUSFAHRT ? STOP_EINFAHRT :
					EINFAHRT);
	}
	
	@Override
	public String toString() {
		return "Ampel: "+this.name()+" ["+this.bezeichnung+"]";
	}
}
