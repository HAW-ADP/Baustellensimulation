package gui.logik;

public interface Uhrzeit extends Zeitkonstanten, Comparable<Uhrzeit> {
	public int stunden();
	
	public int minuten();
	
	public int sekunden();
	
	public Uhrzeit addiere(Uhrzeit zeit);
	
	public Uhrzeit multipliziere(int fac);
	
	public int gesamtZeitSekunden();
	
	public Uhrzeit copy();
	
	@Override
	public int compareTo(Uhrzeit andere);
	 
	@Override
	public String toString();
}
