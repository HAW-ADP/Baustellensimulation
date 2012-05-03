package logik.interfaces;

public interface Zeitspanne extends Zeitkonstanten, Comparable<Zeitspanne> {
	public int sekunden();

	@Override
	public int compareTo(Zeitspanne andere);
	
	@Override
	public String toString();
}
