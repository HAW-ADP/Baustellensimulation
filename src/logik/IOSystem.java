package logik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import logik.interfaces.Uhrzeit;
import logik.interfaces.Zeitspanne;

public class IOSystem {
	public static final String SEPERATOR = ";";
	private final File logDatei;
	
	public IOSystem(String dateiPfad) {
		logDatei = new File(dateiPfad);
		//logDatei = new File("SimResult_" + (new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")).format(new Date()) + ".txt");
	}
	
	public void loggenUndAnzeigen(String text) {
		System.out.println(text);
		
		try {
			FileWriter writer = new FileWriter(logDatei, true);
	        writer.write(text);
	        writer.write(System.getProperty("line.separator"));
	        writer.flush();
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Simulation ladeSimulationsDatei(String dateiPfad) {
		/*
		 * Standardwerte:
		 * 10h;1h;
		 * 1m;30m;
		 * 30;15;EINFAHRT;
		 * 20s;3s;10;
		 * 10s
		 */
		
		Uhrzeit startZeit = new UhrzeitImpl(10,0,0);
		Zeitspanne zeitSpanne = new ZeitspanneImpl(1,0,0);
		List<Uhrzeit> autoUhrzeiten = new ArrayList<Uhrzeit>();
		Zeitspanne minimaleStandzeit = new ZeitspanneImpl(0,1,0);
		Zeitspanne maximaleStandzeit = new ZeitspanneImpl(0,30,0);
		List<Zeitspanne> autoStandzeiten = new ArrayList<Zeitspanne>();
		int anzahlAutos = 30;
		int parkplatzMaximum = 15;
		Ampel startAmpel = Ampel.EINFAHRT;
		Zeitspanne baustellenZeit = new ZeitspanneImpl(0,0,20);
		Zeitspanne autoAbstand = new ZeitspanneImpl(0,0,3);
		int strassenFassungsvermoegen = 10;
		Zeitspanne minimaleAmpelSchaltzeit = new ZeitspanneImpl(0,0,10);
		
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(dateiPfad)));
            try {
                String[] sAry = input.readLine().split(SEPERATOR);
                
                /* startzeit;zeitspanne;
                 * minimalestandzeit;maximalestandzeit;
                 * anzahlAutos;parkplatzkapazit�t;startampel;
                 * baustellenzeit;abstandszeit;strassenkapazit�t;
                 * minimaleampelschaltzeit;
                 */
                if (sAry.length != 11) {
                	//System.out.println("SimDatei fehlerhaft!");
                	throw new IOException("SimDatei fehlerhaft!");
                }
                
                startZeit = new UhrzeitImpl(sAry[0]);
                zeitSpanne = new ZeitspanneImpl(sAry[1]);
                minimaleStandzeit = new ZeitspanneImpl(sAry[2]);
                maximaleStandzeit = new ZeitspanneImpl(sAry[3]);
                anzahlAutos = Integer.parseInt(sAry[4]);
                parkplatzMaximum = Integer.parseInt(sAry[5]);
                startAmpel = Ampel.parseString(sAry[6]);
                baustellenZeit = new ZeitspanneImpl(sAry[7]);
                autoAbstand = new ZeitspanneImpl(sAry[8]);
                strassenFassungsvermoegen = Integer.parseInt(sAry[9]);
                minimaleAmpelSchaltzeit = new ZeitspanneImpl(sAry[10]);
            
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage() + " Benutzt Standardwerte!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Datei nicht gefunden! Benutzt Standardwerte!");
        }
        
        autoUhrzeiten = Nuetzliches.generiereListe(startZeit, zeitSpanne, anzahlAutos, true);
        autoStandzeiten = Nuetzliches.generiereListe(minimaleStandzeit, maximaleStandzeit, anzahlAutos, false);
        
        return new Simulation(startZeit, autoUhrzeiten, autoStandzeiten, parkplatzMaximum, startAmpel, baustellenZeit, autoAbstand, strassenFassungsvermoegen, minimaleAmpelSchaltzeit);
	}
}
