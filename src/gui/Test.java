package gui;

import gui.logik.Uhrzeit;
import gui.logik.UhrzeitImpl;
import gui.logik.Zustand;
import gui.logik.ZustandImpl;
import java.util.PriorityQueue;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

public class Test extends PApplet {
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
    final private int BAUSTELLEKAPAZITAET = 6;
    final private int PARKHAUSZEILEKAPAZITAET = 20;
    final private int PARKHAUSSPALTEKAPAZITAET = 10;
    final private int EINFAHRTKAPAZITAET = 10;
    final private int AUSFAHRTKAPAZITAET = 10;
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
    
    Zustand zustand = new ZustandImpl(0, 0, Zustand.STOP_AUSFAHRT, naechstesAutoEinfahrt,
            startzeit, new PriorityQueue<Uhrzeit>(), new PriorityQueue<Uhrzeit>(),
            naechstesAutoEinfahrt, new UhrzeitImpl(0, 0, 0), autoAbstand, baustellenZeit,
            minParkDauer, maxParkDauer, minAutoAnkunft, maxAutoAnkunft, maxRot,
            PARKHAUSSPALTEKAPAZITAET * PARKHAUSZEILEKAPAZITAET, EINFAHRTKAPAZITAET);
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

    @Override
    public void setup() {
        size(502, 576);
        background(255);
        frameRate(30);
        image(welt, 0, 0);
        image(weltUnten, 0, 469);
        // Farbe f�r die Kn�pfe (WEISS)
        fill(255);
        // Knopf ++Auto Parkplatz
        rect(5, 5, 50, 25);
        // Knopf --Auto Parkplatz
        rect(60, 5, 50, 25);
        // Knopf ++Auto Wartebereich an der Einfahrt
        rect(115, 5, 50, 25);
        // Knopf --Auto Wartebereich an der Einfahrt
        rect(170, 5, 50, 25);
        // Knopf ++Auto Wartebereich an der Ausfahrt
        rect(225, 5, 50, 25);
        // Knopf --Auto Wartebereich an der Ausfahrt
        rect(280, 5, 50, 25);
        // Knopf ++Auto Wartebereich an der Ausfahrt
        rect(335, 5, 50, 25);
        // Knopf --Auto Wartebereich an der Ausfahrt
        rect(390, 5, 50, 25);
        // Knopf Ampelzustand
        rect(445, 5, 50, 25);
        // Knopf nächster Zustand
        rect(5, 35, 50, 25);
        // Parkplatz mit 10x10 Pl�tzen
        zeichneRaster(PARKHAUSX, PARKHAUSY, PARKHAUSSPALTEKAPAZITAET, PARKHAUSZEILEKAPAZITAET);
        // Warteschlange vor der Ausfahrt
        zeichneRaster(AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY, AUSFAHRTKAPAZITAET, 1);
        // Wartebereich vor der Einfahrt
        zeichneRaster(EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY, EINFAHRTKAPAZITAET, 1);
        // Baustellenbereich
        zeichneRaster(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY, 1, BAUSTELLEKAPAZITAET);
    }

    @Override
    public void draw() {
        stroke(0);
        //Immer wieder laden, da das Spielfeld verwischt wird
        image(weltUnten, 0, 469);
        // Ampel f�r die Einfahrt
        zeichneAmpel(EINFAHRTAMPELX, EINFAHRTAMPELY, ampelEinfahrt);
        // Ampel f�r die Ausfahrt
        zeichneAmpel(AUSFAHRTAMPELX, AUSFAHRTAMPELY, ampelAusfahrt);
        bewegeDemoAuto();
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
            this.addiereAutos("P");
        } else if (mouseX >= 60 && mouseX <= 110 && mouseY >= 5 && mouseY <= 30) {
            this.subtrahiereAutos("P");
        } else if (mouseX >= 115 && mouseX <= 165 && mouseY >= 5
                && mouseY <= 30) {
            this.addiereAutos("E");
        } else if (mouseX >= 170 && mouseX <= 220 && mouseY >= 5
                && mouseY <= 30) {
            this.subtrahiereAutos("E");
        } else if (mouseX >= 225 && mouseX <= 275 && mouseY >= 5
                && mouseY <= 30) {
            this.addiereAutos("A");
        } else if (mouseX >= 280 && mouseX <= 330 && mouseY >= 5
                && mouseY <= 30) {
            this.subtrahiereAutos("A");
        } else if (mouseX >= 335 && mouseX <= 385 && mouseY >= 5
                && mouseY <= 30) {
            this.addiereAutos("B");
        } else if (mouseX >= 390 && mouseX <= 440 && mouseY >= 5
                && mouseY <= 30) {
            this.subtrahiereAutos("B");
        } else if (mouseX >= 445 && mouseX <= 495 && mouseY >= 5
                && mouseY <= 30) {
            this.ampelWechsleZustand();
        } else if (mouseX >= 5 && mouseX <= 55 && mouseY >= 35 && mouseY <= 60) {
            this.zustand = this.zustand.naechsterZustand();
            this.ampelWechsleZustand();
            this.updateWartebereiche();
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
                if (this.posAusfahrt < AUSFAHRTKAPAZITAET) {
                    this.addAutoAnPosition(AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY, this.posAusfahrt % 10, this.posAusfahrt / 10);
                    this.posAusfahrt++;
                }
                break;
            case "B":
                if (this.posBaustelle < BAUSTELLEKAPAZITAET) {
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
        fill(255, 0, 0);
        rect(pX + (x * 10), pY + (y * 10), 10, 10);
    }

    private void subAutoAnPosition(int pX, int pY, int x, int y) {
        fill(255, 255, 255);
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
        if (demoAuto1PosX < - 20) {
            demoAuto1PosX = 500;
        }
        demoAuto2PosX = demoAuto2PosX + 3;
        image(auto2, demoAuto2PosX, demoAuto2PosY);
        if (demoAuto2PosX > 500) {
            demoAuto2PosX = 0;
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{gui.Test.class.getName()});
    }
}
