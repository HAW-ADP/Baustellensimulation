package gui.logik;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Scanner;

public class IOSystem
{

    public static void loggeZustand(Zustand zustand)
    {
        File logFile = new File("log" + new SimpleDateFormat("yyy_MM_dd_HH_mm_ss").format(new Date()) + ".txt");

        try
        {
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(zustand.toString());
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e)
        {
            System.out.println("Zustand konnte nicht geloggt werden.");
        }
    }

    public static Zustand leseZustand2(String fileName) throws FileNotFoundException
    {
        BufferedReader buffer;

        try
        {
            buffer = new BufferedReader(new FileReader(fileName));

            try
            {
                buffer.readLine();
                String valueString = buffer.readLine();

                String[] values = valueString.split("\\s*;\\s*");

                if (values.length == 10)
                {
                    Uhrzeit autoAbstand = new UhrzeitImpl(values[0]);
                    Uhrzeit baustellenZeit = new UhrzeitImpl(values[1]);
                    Uhrzeit minParkDauer = new UhrzeitImpl(values[2]);
                    Uhrzeit maxParkDauer = new UhrzeitImpl(values[3]);
                    Uhrzeit minAutoAnkunft = new UhrzeitImpl(values[4]);
                    Uhrzeit maxAutoAnkunft = new UhrzeitImpl(values[5]);
                    Uhrzeit maxRot = new UhrzeitImpl(values[6]);
                    int parkplatzKapazitaet = Integer.parseInt(values[7]);
                    int einfahrtKapazitaet = Integer.parseInt(values[8]);

                    Uhrzeit naechsteAutoEinfahrt = new UhrzeitImpl(values[9]);

                    ZustandsUmgebung umgebung = new ZustandsUmgebung(autoAbstand, baustellenZeit, minParkDauer, maxParkDauer, minAutoAnkunft,
                            maxAutoAnkunft, maxRot, parkplatzKapazitaet,
                            einfahrtKapazitaet);

                    Zustand zustand = new ZustandImpl(0, 0, Zustand.STOP_AUSFAHRT, naechsteAutoEinfahrt,
                            umgebung.getOeffnungszeit(), new PriorityQueue<Uhrzeit>(), new PriorityQueue<Uhrzeit>(),
                            naechsteAutoEinfahrt, new UhrzeitImpl(0, 0, 0), umgebung, 0);
                    
                    return zustand;
                } else
                {
                    System.out.println("Falsche Anzahl an Parametern.");
                }


            } catch (IOException e)
            {
                System.out.println("Falsche Anzahl von Zeilen in der Datei.");
            }
            {
            }

        } catch (FileNotFoundException e)
        {
            System.out.println("Datei nicht gefunden.");
        }

        return null;
    }

    public static Zustand leseZustand(String inputFileName)
    {

        //Daten mit Defaultwerten belegen
        Uhrzeit autoAbstand = new UhrzeitImpl(3);
        Uhrzeit baustellenZeit = new UhrzeitImpl(20);
        Uhrzeit minParkDauer = new UhrzeitImpl(0, 15, 0);
        Uhrzeit maxParkDauer = new UhrzeitImpl(2, 0, 0);
        Uhrzeit minAutoAnkunft = new UhrzeitImpl(0, 0, 15);
        Uhrzeit maxAutoAnkunft = new UhrzeitImpl(0, 1, 0);
        Uhrzeit maxRot = new UhrzeitImpl(0, 2, 0);
        int parkplatzKapazitaet = 15;
        int einfahrtKapazitaet = 15;

        Uhrzeit naechsteAutoEinfahrt = new UhrzeitImpl(10, 0, 0);

        File file = new File(inputFileName);
        Scanner fileScanner = null;


        try
        {
            fileScanner = new Scanner(file);
        } catch (FileNotFoundException e)
        {
            System.out.println("Die Datei: " + file + " wurde nicht gefunden");
        }

        String[] inputAry = null;

        if (fileScanner != null)
        {
            inputAry = fileScanner.next().split(";");
        }

        if (inputAry != null && inputAry.length == 10)
        {
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

        ZustandsUmgebung umgebung = new ZustandsUmgebung(autoAbstand, baustellenZeit, minParkDauer, maxParkDauer, minAutoAnkunft,
                maxAutoAnkunft, maxRot, parkplatzKapazitaet,
                einfahrtKapazitaet);

        Zustand zustand = new ZustandImpl(0, 0, Zustand.STOP_AUSFAHRT, naechsteAutoEinfahrt,
                umgebung.getOeffnungszeit(), new PriorityQueue<Uhrzeit>(), new PriorityQueue<Uhrzeit>(),
                naechsteAutoEinfahrt, new UhrzeitImpl(0, 0, 0), umgebung, 0);

        return zustand;
    }
}
