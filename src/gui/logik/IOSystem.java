package gui.logik;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Scanner;

public class IOSystem {
	
	public static void loggeZustand(Zustand zustand){
		File logFile = new File("log" + new SimpleDateFormat("yyy_MM_dd_HH_mm_ss").format(new Date()) + ".txt");
		
		try {
			FileWriter writer = new FileWriter(logFile, true);
			writer.write(zustand.toString());
			writer.write("\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Zustand konnte nicht geloggt werden.");
		}
	}
	
	public static Zustand leseZustand(String inputFileName){

		//Daten mit Defaultwerten belegen
		Uhrzeit autoAbstand = new UhrzeitImpl(3);
		Uhrzeit baustellenZeit = new UhrzeitImpl(20);
		Uhrzeit minParkDauer = new UhrzeitImpl(0,15,0);
		Uhrzeit maxParkDauer = new UhrzeitImpl(2,0,0);
		Uhrzeit minAutoAnkunft = new UhrzeitImpl(0,0,15);
		Uhrzeit maxAutoAnkunft = new UhrzeitImpl(0,1,0);
		Uhrzeit maxRot = new UhrzeitImpl(0,2,0);
		int parkplatzKapazitaet = 100;
		int einfahrtKapazitaet = 25;
		
		Uhrzeit naechsteAutoEinfahrt = new UhrzeitImpl(10,0,0);
		
		File file = new File(inputFileName);
		Scanner fileScanner = null;
		
		
		try {
			fileScanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("Die Datei: " + file + " wurde nicht gefunden");
		}
		
		String[] inputAry = null;
		
		if(fileScanner != null)
			inputAry = fileScanner.next().split(";");
		
		if(inputAry != null && inputAry.length == 10){
			autoAbstand = new UhrzeitImpl(inputAry[0]);
			baustellenZeit = new UhrzeitImpl(inputAry[1]);
			minParkDauer = new UhrzeitImpl(inputAry[2]);
			maxParkDauer = new UhrzeitImpl(inputAry[3]);
			minAutoAnkunft = new UhrzeitImpl(inputAry[4]);
			maxAutoAnkunft = new UhrzeitImpl(inputAry[5]);
			maxRot = new UhrzeitImpl(inputAry[6]);
			parkplatzKapazitaet = Integer.parseInt(inputAry[7]);
			einfahrtKapazitaet = Integer.parseInt(inputAry[8]);
			
			naechsteAutoEinfahrt = new UhrzeitImpl(inputAry[9]);
		}
		
		ZustandsUmgebung umgebung = new ZustandsUmgebung(autoAbstand, baustellenZeit,
					 									 minParkDauer, maxParkDauer, minAutoAnkunft,
					 									 maxAutoAnkunft, maxRot, parkplatzKapazitaet,
					 									 einfahrtKapazitaet);

		Zustand zustand = new ZustandImpl(0,0, Zustand.STOP_AUSFAHRT, naechsteAutoEinfahrt,
										  umgebung.getOeffnungszeit(), new PriorityQueue<Uhrzeit>(), new PriorityQueue<Uhrzeit>(),
										  naechsteAutoEinfahrt, new UhrzeitImpl(0,0,0), umgebung);
		
		return zustand;
	}

}
