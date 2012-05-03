package de.haw.ad.a2.interfaces;

public interface Warteschlange {
	//public static enum WarteschlangenSortierung { AUFSTEIGEND, ABSTEIGEND; }
	
    public Warteschlange hinzufuegen(Uhrzeit wert);
   
    public Warteschlange entfernen();
   
    public Warteschlange schiebeUmZeit(Zeitspanne zeit);
    
    public Warteschlange sortieren();
    
    public boolean istLeer();
   
    public boolean istVoll();
    
    public int laenge();
    
    public int maximaleElemente();
    
    public Uhrzeit letzter();
    
    public Uhrzeit naechster();
    
    @Override
    public String toString();
}
