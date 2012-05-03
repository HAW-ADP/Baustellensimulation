package de.haw.ad.a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.haw.ad.a2.interfaces.Uhrzeit;
import de.haw.ad.a2.interfaces.Zeitspanne;
import static de.haw.ad.a2.IOSystem.*;

public class Test {
	public static void main(String[] args) {
		// testeZeitIntervall();
		// testeUhrzeit();
		testeGott();
		// testeIO();
	}

	private static void testeZeitIntervall() {
		System.out.println("### Teste ZeitIntervall: ###");

		Zeitspanne a = new ZeitspanneImpl("1h5m2s");
		System.out.println("1h5m2s => " + a + " == [3600+300+2 = 3902]");

		a = new ZeitspanneImpl("100m8s");
		System.out.println("100m8s => " + a + " == [6000+8 = 6008]");
	}

	private static void testeUhrzeit() {
		System.out.println("### Teste Uhrzeit: ###");

		Uhrzeit a = new UhrzeitImpl("12h50m12s");
		System.out.println("12h50m12s => " + a + " == 12:50:12");

		a = new UhrzeitImpl("60m8s");
		System.out.println("60m8s => " + a + " == 00:59:08");

		a = new UhrzeitImpl("12h50m12s");
		Zeitspanne b = new ZeitspanneImpl("1h5m2s");
		System.out.println("12h50m12s plus 1h5m2s => " + a.addiere(b)
				+ " == 13:55:14");

		b = new ZeitspanneImpl("1h12m2s");
		System.out.println("12h50m12s plus 1h12m2s => " + a.addiere(b)
				+ " == 14:02:14");

		b = new ZeitspanneImpl("1h12m50s");
		System.out.println("12h50m12s plus 1h12m50s => " + a.addiere(b)
				+ " == 14:03:02");

		b = new ZeitspanneImpl("12h12m50s");
		System.out.println("12h50m12s plus 12h12m50s => " + a.addiere(b)
				+ " == 01:03:02");
	}

	private static void testeGott() {
		Uhrzeit startZeit = new UhrzeitImpl("10h00m00s");
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

		Gott g = new Gott(startZeit, autoUhrzeiten, autoStandzeiten,
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

	private static void testeIO() {
		Gott g = IOSystem.ladeSimulationsDatei("test.txt");
		Iterator<Zustand> it = g.iterator();
		IOSystem io = new IOSystem("log.txt");
		while (it.hasNext())
			io.loggenUndAnzeigen(it.next().toString());
			//System.out.println(it.next());
	}
}
