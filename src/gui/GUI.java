package gui;

import gui.logik.*;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PImage;

public class GUI extends PApplet {
    // Position Parkhaus

    final private int PARKHAUSX = 390;
    final private int PARKHAUSY = 187;
    // Position Wartebereich f�r die Ausfahrt
    final private int AUSFAHRTWARTEBEREICHX = 100;
    final private int AUSFAHRTWARTEBEREICHY = 225;
    // Position Wartebereich f�r die Einfahrt
    final private int EINFAHRTWARTEBEREICHX = 170;
    final private int EINFAHRTWARTEBEREICHY = 440;
    // Position Baustelle
    final private int BAUSTELLENBEREICHX = 85;
    final private int BAUSTELLENBEREICHY = 320;
    // Position der Ampel f�r die Einfahrt
    final private int EINFAHRTAMPELX = 160;
    final private int EINFAHRTAMPELY = 450;
    // Position der Ampel f�r die Ausfahrt
    final private int AUSFAHRTAMPELX = 90;
    final private int AUSFAHRTAMPELY = 240;
    // Fixe Größen
    final private int PARKHAUSZEILEKAPAZITAET = 20;
    final private int PARKHAUSSPALTEKAPAZITAET = 10;
    final private int EINFAHRTKAPAZITAET = 3;
    // final private int AUSFAHRTKAPAZITAET = 10;
    private PImage welt = loadImage("src\\gui\\img\\welt.jpg");
    private PImage weltUnten = loadImage("src\\gui\\img\\weltUnten.jpg");
    private PImage auto1 = loadImage("src\\gui\\img\\auto1.jpg");
    private PImage auto2 = loadImage("src\\gui\\img\\auto2.jpg");
    //Daten der Simulation
    Uhrzeit minAutoAnkunft = new UhrzeitImpl(0, 0, 30);
    Uhrzeit maxAutoAnkunft = new UhrzeitImpl(0, 2, 0);
    Random rand = new Random();
    int minAnkunft = minAutoAnkunft.gesamtZeitSekunden();
    int maxAnkunft = maxAutoAnkunft.gesamtZeitSekunden();
    int naechstesAutoEinfahrtInt = minAnkunft + rand.nextInt(maxAnkunft - minAnkunft);
    Uhrzeit startzeit = new UhrzeitImpl(10, 0, 0);
    Uhrzeit naechstesAutoEinfahrt = new UhrzeitImpl(naechstesAutoEinfahrtInt).addiere(startzeit);
    Uhrzeit autoAbstand = new UhrzeitImpl(3);
    Uhrzeit baustellenZeit = new UhrzeitImpl(20);
    Uhrzeit minParkDauer = new UhrzeitImpl(0, 30, 0);
    Uhrzeit maxParkDauer = new UhrzeitImpl(1, 0, 0);
    Uhrzeit maxRot = new UhrzeitImpl(0, 2, 0);
    ZustandsUmgebung umgebung = new ZustandsUmgebung(autoAbstand, baustellenZeit,
            minParkDauer, maxParkDauer, minAutoAnkunft,
            maxAutoAnkunft, maxRot, PARKHAUSSPALTEKAPAZITAET * PARKHAUSZEILEKAPAZITAET,
            EINFAHRTKAPAZITAET);
    Zustand zustand = new ZustandImpl(0, 0, Zustand.STOP_AUSFAHRT, naechstesAutoEinfahrt,
            startzeit, new PriorityQueue<Uhrzeit>(), new PriorityQueue<Uhrzeit>(),
            naechstesAutoEinfahrt, new UhrzeitImpl(0, 0, 0), umgebung);
    private float demoAuto1PosX = 500;
    private float demoAuto1PosY = 471;
    private float demoAuto2PosX = 0;
    private float demoAuto2PosY = 545;
    private int posParkplatz = 0;
    private int posEinfahrt = 0;
    private int posAusfahrt = 0;
    private int posBaustelle = 0;
    private String ampelEinfahrt = "R";
    private String ampelAusfahrt = "R";
    boolean gestartet = false;
    int pause = 0;

    @Override
    public void setup() {
        size(502, 576);
        background(255);
        frameRate(30);
        image(welt, 0, 0);
        image(weltUnten, 0, 469);
        // Knopf Step-Modus Parkplatz
        fill(255);
        rect(5, 5, 50, 25);
        fill(0);
        rect(20, 10, 5, 15);
        fill(0);
        triangle(30, 10, 30, 25, 40, 17);
        // Knopf AutomatischerModus start
        fill(255);
        rect(60, 5, 50, 25);
        fill(0);
        triangle(80, 10, 80, 25, 90, 17);
        // Knopf AutomatischerModus stop
        fill(255);
        rect(115, 5, 50, 25);
        fill(0);
        rect(132, 10, 15, 15);
        // Parkplatz mit 10x10 Pl�tzen
        fill(255);
        zeichneRaster(PARKHAUSX, PARKHAUSY, PARKHAUSSPALTEKAPAZITAET, PARKHAUSZEILEKAPAZITAET);
        // Warteschlange vor der Ausfahrt
//        zeichneRaster(AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY, AUSFAHRTKAPAZITAET, 1);
        // Wartebereich vor der Einfahrt
        zeichneRaster(EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY, EINFAHRTKAPAZITAET, 1);
        // Baustellenbereich
//        zeichneRaster(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY, 1, zustand.getBaustellenAutos());
    }

    @Override
    public void draw() {
        stroke(0);
        fill(0);
        rect(395, 7, 100, 15);
        fill(255);
        text(zustand.getUhrzeit().toString(), 400, 20);
        //Immer wieder laden, da das Spielfeld verwischt wird
        image(weltUnten, 0, 469);
        // Ampel f�r die Einfahrt
        zeichneAmpel(EINFAHRTAMPELX, EINFAHRTAMPELY, ampelEinfahrt);
        // Ampel f�r die Ausfahrt
        zeichneAmpel(AUSFAHRTAMPELX, AUSFAHRTAMPELY, ampelAusfahrt);
        bewegeDemoAuto();
        //Automatischer Modus
        if (pause >= 10) {
            if (gestartet) {
                this.zustand = this.zustand.naechsterZustand();
                this.ampelWechsleZustand();
                this.updateWartebereiche();
            }
            pause = 0;
        }
        pause++;
    }

    private void zeichneAmpel(int x, int y, String farbe) {
        switch (farbe) {
            case "R":
                fill(255, 0, 0);
                break;
            case "G":
                fill(0, 255, 0);
                break;
        }
        ellipse(x, y, 10, 10);
    }

    @Override
    public void mouseReleased() {
        if (mouseX >= 5 && mouseX <= 55 && mouseY >= 5 && mouseY <= 30) {
            this.zustand = this.zustand.naechsterZustand();
            this.ampelWechsleZustand();
            this.updateWartebereiche();
        } else if (mouseX >= 60 && mouseX <= 110 && mouseY >= 5 && mouseY <= 30) {
            gestartet = true;
        } else if (mouseX >= 115 && mouseX <= 165 && mouseY >= 5 && mouseY <= 30) {
            gestartet = false;
        }
    }

    //Der aktuelle Ampelzustand, Einfahrt = 1, STOP_EINFAHRT = 2, AUSFAHRT = 3, STOP_AUSFAHRT = 4 
    private void ampelWechsleZustand() {
        if (zustand.getAmpelzustand() == 1) {
            ampelEinfahrt = "G";
        } else if (zustand.getAmpelzustand() == 2) {
            ampelEinfahrt = "R";
        } else if (zustand.getAmpelzustand() == 3) {
            ampelAusfahrt = "G";
        } else if (zustand.getAmpelzustand() == 4) {
            ampelAusfahrt = "R";
        } else if (zustand.getAmpelzustand() == 5) {
            ampelAusfahrt = "R";
            ampelEinfahrt = "R";
        }

    }

    private void updateWartebereiche() {
        if (posEinfahrt < zustand.getEinfahrtAutos()) {
            addiereAutos("E");
        }
        if (posEinfahrt > zustand.getEinfahrtAutos()) {
            subtrahiereAutos("E");
        }
        if (posAusfahrt < zustand.getAusfahrtAutos()) {
            addiereAutos("A");
        }
        if (posAusfahrt > zustand.getAusfahrtAutos()) {
            subtrahiereAutos("A");
        }
        if (posBaustelle < zustand.getBaustellenAutos()) {
            addiereAutos("B");
        }
        if (posBaustelle > zustand.getBaustellenAutos()) {
            subtrahiereAutos("B");
        }
        if (posParkplatz < zustand.getParkplatzAutos()) {
            addiereAutos("P");
        }
        if (posParkplatz > zustand.getParkplatzAutos()) {
            subtrahiereAutos("P");
        }
        System.out.println(zustand);
    }

    private void addiereAutos(String wo) {
        switch (wo) {
            case "P":
                if (this.posParkplatz < PARKHAUSSPALTEKAPAZITAET * PARKHAUSZEILEKAPAZITAET) {
                    this.addAutoAnPosition(PARKHAUSX, PARKHAUSY, this.posParkplatz % 10, this.posParkplatz / 10);
                    this.posParkplatz++;
                }
                break;
            case "E":
                if (this.posEinfahrt < EINFAHRTKAPAZITAET) {
                    this.addAutoAnPosition(EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY, this.posEinfahrt % 10, this.posEinfahrt / 10);
                    this.posEinfahrt++;
                }
                break;
            case "A":
                if (this.posAusfahrt < PARKHAUSSPALTEKAPAZITAET * PARKHAUSZEILEKAPAZITAET) {
                    this.addAutoAnPosition(AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY, this.posAusfahrt % 10, this.posAusfahrt / 10);
                    this.posAusfahrt++;
                }
                break;
            case "B":
                if (this.posBaustelle < zustand.getBaustellenAutos()) {
                    this.addAutoAnPosition(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY, this.posBaustelle / 10, this.posBaustelle % 10);
                    this.posBaustelle++;
                }
                break;
        }
    }

    private void subtrahiereAutos(String wo) {
        switch (wo) {
            case "P":
                if (this.posParkplatz > 0) {
                    this.posParkplatz--;
                    this.subAutoAnPosition(PARKHAUSX, PARKHAUSY, this.posParkplatz % 10, this.posParkplatz / 10);
                }
                break;
            case "E":
                if (this.posEinfahrt > 0) {
                    this.posEinfahrt--;
                    this.subAutoAnPosition(EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY, this.posEinfahrt % 10, this.posEinfahrt / 10);
                }
                break;
            case "A":
                if (this.posAusfahrt > 0) {
                    this.posAusfahrt--;
                    this.subAutoAnPosition(AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY, this.posAusfahrt % 10, this.posAusfahrt / 10);
                }
                break;
            case "B":
                if (this.posBaustelle > 0) {
                    this.posBaustelle--;
                    this.subAutoAnPosition(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY, this.posBaustelle / 10, this.posBaustelle % 10);
                }
                break;
        }
    }

    private void addAutoAnPosition(int pX, int pY, int x, int y) {
        fill(0, 0, 255);
//        stroke(255);
        rect(pX + (x * 10), pY + (y * 10), 10, 10);
    }

    private void subAutoAnPosition(int pX, int pY, int x, int y) {
        fill(255, 255, 255);
//        stroke(255);
        rect(pX + (x * 10), pY + (y * 10), 10, 10);

    }

    private void zeichneRaster(int pX, int pY, int cols, int rows) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                rect(pX + (x * 10), pY + (y * 10), 10, 10);
            }
        }
    }

    private void bewegeDemoAuto() {
        demoAuto1PosX = demoAuto1PosX - 2;
        image(auto1, demoAuto1PosX, demoAuto1PosY);
        if (demoAuto1PosX < -20) {
            demoAuto1PosX = 520;
        }
        demoAuto2PosX = demoAuto2PosX + 3;
        image(auto2, demoAuto2PosX, demoAuto2PosY);
        if (demoAuto2PosX > 520) {
            demoAuto2PosX = 0;
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{gui.GUI.class.getName()});
    }
}
