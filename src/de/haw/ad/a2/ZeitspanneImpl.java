package de.haw.ad.a2;

import de.haw.ad.a2.interfaces.Zeitspanne;

public class ZeitspanneImpl implements Zeitspanne {
	public static Zeitspanne NAECHSTE_ZEITEINHEIT = new ZeitspanneImpl(1);
	private final int sekunden;
	
	public ZeitspanneImpl(String zeitString) {
		this.sekunden = interpretiereString(zeitString);
	}

	public ZeitspanneImpl(int stunden, int minuten, int sekunden) {
		this.sekunden = sekunden + minuten * INTERVALL_MIN_SEK + stunden * INTERVALL_MIN_SEK * INTERVALL_MIN_SEK;
	}
	
	public ZeitspanneImpl(int minuten, int sekunden) {
		this.sekunden = sekunden + minuten * INTERVALL_MIN_SEK;
	}
	
	public ZeitspanneImpl(int sekunden) {
		this.sekunden = sekunden;
	}
	
	private int interpretiereString(String string) {
		int stunden = INTERVALL_MINIMUM;
		int minuten = INTERVALL_MINIMUM;
		int sekunden = INTERVALL_MINIMUM;
		String str = string.toLowerCase();
		String[] array = null;
			
		if (str.contains(STRING_STUNDEN)) {
			array = str.split(STRING_STUNDEN);
			stunden = Integer.parseInt(array[0]);
			if (array.length>1)	str = array[1];
		}
		
		if (str.contains(STRING_MINUTEN)) {
			array = str.split(STRING_MINUTEN);
			minuten = Integer.parseInt(array[0]);
			if (array.length>1)	str = array[1];
		}
		
		if (str.contains(STRING_SEKUNDEN)) {
			array = str.split(STRING_SEKUNDEN);
			sekunden = Integer.parseInt(array[0]);
		}
		
		return sekunden + minuten * INTERVALL_MIN_SEK + stunden * INTERVALL_MIN_SEK * INTERVALL_MIN_SEK;
	}
	
	public int sekunden() {
		return this.sekunden;
	}

	@Override
	public int compareTo(Zeitspanne andere) {
		return Integer.compare(this.sekunden, andere.sekunden());
	}
	 
	@Override
	public String toString() {
		return "Zeitintervall: "+this.sekunden+" Sekunden";
	}
}
