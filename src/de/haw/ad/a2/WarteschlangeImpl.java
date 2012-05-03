package de.haw.ad.a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.haw.ad.a2.interfaces.Uhrzeit;
import de.haw.ad.a2.interfaces.Warteschlange;
import de.haw.ad.a2.interfaces.Zeitspanne;

public class WarteschlangeImpl implements Warteschlange {
    private int maximaleElemente;
	private List<Uhrzeit> wertListe = new ArrayList<Uhrzeit>();
    
    public WarteschlangeImpl(int maximaleElemente) {
    	this.maximaleElemente = maximaleElemente;
    }
   
    private WarteschlangeImpl(int maximaleElemente, List<Uhrzeit> liste) {
    	this.maximaleElemente = maximaleElemente;
        this.wertListe.addAll(liste);
    }
    
    public Warteschlange hinzufuegen(Uhrzeit wert) {
    	if (istVoll()) return this;
    	
        List<Uhrzeit> liste = new ArrayList<Uhrzeit>();
        liste.addAll(wertListe);
        liste.add(wert);
        return new WarteschlangeImpl(this.maximaleElemente, liste);
    }
   
    public Warteschlange entfernen() {
    	if (istLeer()) return this;
    	
        List<Uhrzeit> liste = new ArrayList<Uhrzeit>();
        for (int i = 1; i < wertListe.size(); i++) liste.add(wertListe.get(i));
        return new WarteschlangeImpl(this.maximaleElemente, liste);
    }
   
	public Warteschlange schiebeUmZeit(Zeitspanne zeit) {
		if (istLeer()) return this;
		
        List<Uhrzeit> liste = new ArrayList<Uhrzeit>();
        for (Uhrzeit z : wertListe) liste.add(z.addiere(zeit));
        return new WarteschlangeImpl(this.maximaleElemente, liste);
	}
    
	public Warteschlange sortieren() {
		 List<Uhrzeit> liste = new ArrayList<Uhrzeit>();
		 liste.addAll(wertListe);
		 // Aufteigend
		 Collections.sort(wertListe);	 		 
		 return new WarteschlangeImpl(this.maximaleElemente, liste);
	}
	
    public boolean istLeer() {
        return wertListe.isEmpty();
    }
   
    public boolean istVoll() {
        return laenge() == maximaleElemente;
    }
    
    public int laenge() {
    	return wertListe.size();
    }
    
    public int maximaleElemente() {
    	return maximaleElemente;
    }
    
    public Uhrzeit letzter() {
        return (laenge() > 0 ? wertListe.get(laenge()-1) : null);
    }
    
    public Uhrzeit naechster() {
        return (laenge() > 0 ? wertListe.get(0) : null);
    }
    
    @Override
    public String toString() {
        return "ZeitintervallWarteschlange: "+wertListe.toString();
    }
}
