package logik;

import logik.interfaces.Uhrzeit;
import logik.interfaces.Zeitspanne;

public class UhrzeitImpl implements Uhrzeit {
	private final int stunden;
	private final int minuten;
	private final int sekunden;
	
	public UhrzeitImpl(String zeitString) {
		int stunden = INTERVALL_MINIMUM;
		int minuten = INTERVALL_MINIMUM;
		int sekunden = INTERVALL_MINIMUM;
		String str = zeitString.toLowerCase();
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
		
		this.sekunden = (sekunden < INTERVALL_MINIMUM ? INTERVALL_MINIMUM :
			(sekunden >= INTERVALL_MIN_SEK ? INTERVALL_MIN_SEK - 1 : sekunden));
		
		this.minuten = (minuten < INTERVALL_MINIMUM ? INTERVALL_MINIMUM :
			(minuten >= INTERVALL_MIN_SEK ? INTERVALL_MIN_SEK - 1 : minuten));
		
		this.stunden = (stunden < INTERVALL_MINIMUM ? INTERVALL_MINIMUM :
			(stunden >= INTERVALL_STUNDEN ? INTERVALL_STUNDEN - 1 : stunden));
	}

	public UhrzeitImpl(int stunden, int minuten, int sekunden) {
		this.sekunden = (sekunden < INTERVALL_MINIMUM ? INTERVALL_MINIMUM :
			(sekunden >= INTERVALL_MIN_SEK ? INTERVALL_MIN_SEK - 1 : sekunden));
		
		this.minuten = (minuten < INTERVALL_MINIMUM ? INTERVALL_MINIMUM :
			(minuten >= INTERVALL_MIN_SEK ? INTERVALL_MIN_SEK - 1 : minuten));
		
		this.stunden = (stunden < INTERVALL_MINIMUM ? INTERVALL_MINIMUM :
			(stunden >= INTERVALL_STUNDEN ? INTERVALL_STUNDEN - 1 : stunden));
	}
	
	public int stunden() {
		return stunden;
	}	
	
	public int minuten() {
		return minuten;
	}	
	
	public int sekunden() {
		return sekunden;
	}
	
	public Uhrzeit addiere(Zeitspanne intervall) {
		int stunden = this.stunden;
		int minuten = this.minuten;
		int sekunden = this.sekunden + intervall.sekunden();
		
		minuten += sekunden / INTERVALL_MIN_SEK;
		sekunden %= INTERVALL_MIN_SEK;
		
		stunden += minuten / INTERVALL_MIN_SEK;
		minuten %= INTERVALL_MIN_SEK;
		stunden %= INTERVALL_STUNDEN;
				
		return new UhrzeitImpl(stunden, minuten, sekunden);
	}
	
	@Override
	public int compareTo(Uhrzeit andere) {
		return (Integer.compare(this.stunden, andere.stunden()) == 0 ? (
				Integer.compare(this.minuten, andere.minuten()) == 0 ? 
						Integer.compare(this.sekunden, andere.sekunden()) : 
							Integer.compare(this.minuten, andere.minuten())) :
								Integer.compare(this.stunden, andere.stunden()));
	}
	 
	@Override
	public String toString() {
		return "Uhrzeit: "+
			(this.stunden < 10 ? "0" : "") + this.stunden +":"+
			(this.minuten < 10 ? "0" : "") + this.minuten +":"+
			(this.sekunden < 10 ? "0" : "") + this.sekunden;
	}
}
