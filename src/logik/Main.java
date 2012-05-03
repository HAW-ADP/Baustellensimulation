/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import logik.interfaces.Uhrzeit;
import logik.interfaces.Zeitspanne;

/**
 *
 * @author maiwald
 */
public class Main
{
    public void main()
        {Uhrzeit startZeit = new UhrzeitImpl("10h00m00s");
        Ampel ampel = Ampel.EINFAHRT;
        int parkplaetze = 4;
        int strassen = 2;
        Zeitspanne baustellenZeit = new ZeitspanneImpl("00m15s");
        Zeitspanne autoAbstandsZeit = new ZeitspanneImpl("00m03s");
        Zeitspanne minimaleAmpelSchaltzeit = new ZeitspanneImpl("00m30s");

        List<Uhrzeit> autoUhrzeiten = new ArrayList<Uhrzeit>();
        autoUhrzeiten.add(new UhrzeitImpl("10h01m00s"));
        autoUhrzeiten.add(new UhrzeitImpl("10h01m01s"));
        autoUhrzeiten.add(new UhrzeitImpl("10h01m02s"));
        autoUhrzeiten.add(new UhrzeitImpl("10h02m10s"));
        autoUhrzeiten.add(new UhrzeitImpl("10h05m04s"));
        autoUhrzeiten.add(new UhrzeitImpl("10h05m05s"));
        autoUhrzeiten.add(new UhrzeitImpl("10h05m06s"));

        List<Zeitspanne> autoStandzeiten = new ArrayList<Zeitspanne>();
        autoStandzeiten.add(new ZeitspanneImpl("04m03s"));
        autoStandzeiten.add(new ZeitspanneImpl("04m04s"));
        autoStandzeiten.add(new ZeitspanneImpl("04m05s"));
        autoStandzeiten.add(new ZeitspanneImpl("01h02m10s"));
        autoStandzeiten.add(new ZeitspanneImpl("01m10s"));
        autoStandzeiten.add(new ZeitspanneImpl("01m11s"));
        autoStandzeiten.add(new ZeitspanneImpl("01m12s"));

        Simulation g = new Simulation(startZeit, autoUhrzeiten, autoStandzeiten,
                        parkplaetze, ampel, baustellenZeit, autoAbstandsZeit, strassen,
                        minimaleAmpelSchaltzeit);
        Iterator<Zustand> it = g.iterator();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        char in = '0'; // Hier auf '0' umstellen, dass man schrittweise durchgehen kann!
        while (it.hasNext() && !(in=='a')) {
                System.out.println(it.next());
                try {
                        if (!(in=='x')) in = (char) br.read();
                } catch (IOException ioe) {
                        System.exit(1);
                }

            }
    }
}
