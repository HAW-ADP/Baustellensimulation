package de.haw.ad.a2.interfaces;

public interface Uhrzeit extends Zeitkonstanten, Comparable<Uhrzeit> {
	public int stunden();
	
	public int minuten();
	
	public int sekunden();
	
	public Uhrzeit addiere(Zeitspanne intervall);
	
	@Override
	public int compareTo(Uhrzeit andere);
	 
	@Override
	public String toString();
}
