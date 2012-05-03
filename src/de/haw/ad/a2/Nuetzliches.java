package de.haw.ad.a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.haw.ad.a2.interfaces.Uhrzeit;
import de.haw.ad.a2.interfaces.Zeitspanne;

public class Nuetzliches {
    public static List<Uhrzeit> generiereListe(Uhrzeit start, Zeitspanne spanne, int anzahl, boolean sortieren) {
        List<Uhrzeit> liste = new ArrayList<Uhrzeit>();
        Random r = new Random();
        for (int i = 0; i < anzahl; i++) {
        	liste.add(start.addiere(new ZeitspanneImpl(0 + r.nextInt(spanne.sekunden() - 0))));
        }
        if (sortieren) Collections.sort(liste);
        return liste;
    }
    
    public static List<Zeitspanne> generiereListe(Zeitspanne minimum, Zeitspanne maximum, int anzahl, boolean sortieren) {
        List<Zeitspanne> liste = new ArrayList<Zeitspanne>();
        Random r = new Random();
        for (int i = 0; i < anzahl; i++) {
        	liste.add(new ZeitspanneImpl(minimum.sekunden() + r.nextInt(maximum.sekunden() - minimum.sekunden())));
        }
        if (sortieren) Collections.sort(liste);
        return liste;
    }
}
