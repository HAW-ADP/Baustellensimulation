package gui;

import gui.logik.*;
import java.util.PriorityQueue;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

public class GuiT extends PApplet {
    // Position Parkhaus

    final private int PARKHAUSX = 290;
    final private int PARKHAUSY = 190;
    // Position Wartebereich fï¿½r die Ausfahrt
    final private int AUSFAHRTWARTEBEREICHX = 100;
    final private int AUSFAHRTWARTEBEREICHY = 225;
    // Position Wartebereich fï¿½r die Einfahrt
    final private int EINFAHRTWARTEBEREICHX = 170;
    final private int EINFAHRTWARTEBEREICHY = 440;
    // Position Baustelle
    final private int BAUSTELLENBEREICHX = 85;
    final private int BAUSTELLENBEREICHY = 320;
    // Position der Ampel fï¿½r die Einfahrt
    final private int EINFAHRTAMPELX = 160;
    final private int EINFAHRTAMPELY = 450;
    // Position der Ampel fï¿½r die Ausfahrt
    final private int AUSFAHRTAMPELX = 90;
    final private int AUSFAHRTAMPELY = 240;
    // Welt
    private PImage welt = loadImage("src\\gui\\img\\welt.jpg");
    private PImage weltUnten = loadImage("src\\gui\\img\\weltUnten.jpg");
    private PImage auto1 = loadImage("src\\gui\\img\\auto1.jpg");
    private PImage auto2 = loadImage("src\\gui\\img\\auto2.jpg");
    // Initialisierung des ersten Zustands
    Zustand zustand = IOSystem.leseZustand("src\\conf\\konfiguration.txt");
    private float demoAuto1PosX = 500;
    private float demoAuto1PosY = 471;
    private float demoAuto2PosX = 0;
    private float demoAuto2PosY = 545;
    //Position des letzten besetzten Platzes
    private int posParkplatz = 0;
    private int posEinfahrt = 0;
    private int posAusfahrt = 0;
    private int posBaustelle = 0;
    //Ampel initialisierung
    private String ampelEinfahrt = "R";
    private String ampelAusfahrt = "R";
    //Automatischer Modus
    boolean gestartet = false;
    //Zeitverzögerung automatischer Modus
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
        // Parkplatz 
        fill(255);
        zeichneParkplatz(PARKHAUSX, PARKHAUSY, zustand.getParkhausgroesse());
        // Wartebereich vor der Einfahrt
        zeichneRaster(EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY,
                zustand.getEinfahrtgroesse(), 1);
    }

    @Override
    public void draw() {
        stroke(0);
        fill(0);
        rect(395, 7, 100, 15);
        fill(255);
        text(zustand.getUhrzeit().toString(), 400, 20);
        fill(0);
        rect(150, 408, 170, 15);
        fill(255);
        text(zustand.getVorbeigefahreneAutos(), 155, 420);
        text(": Autos vorbeigefahren", 175, 420);
        // Immer wieder laden, da das Spielfeld verwischt wird
        image(weltUnten, 0, 469);
        // Ampel fï¿½r die Einfahrt
        zeichneAmpel(EINFAHRTAMPELX, EINFAHRTAMPELY, ampelEinfahrt);
        // Ampel fï¿½r die Ausfahrt
        zeichneAmpel(AUSFAHRTAMPELX, AUSFAHRTAMPELY, ampelAusfahrt);
        bewegeDemoAuto();
        // Automatischer Modus
        if (pause >= 0) {
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
        } else if (mouseX >= 115 && mouseX <= 165 && mouseY >= 5
                && mouseY <= 30) {
            gestartet = false;
        }
    }

    // Der aktuelle Ampelzustand, Einfahrt = 1, STOP_EINFAHRT = 2, AUSFAHRT = 3,
    // STOP_AUSFAHRT = 4, STOP_BEIDE = 5
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
                if (this.posParkplatz < zustand.getParkhausgroesse()) {
                    this.addAutoAnPosition(PARKHAUSX, PARKHAUSY,
                            this.posParkplatz % 20, this.posParkplatz / 20);
                    this.posParkplatz++;
                }
                break;
            case "E":
                if (this.posEinfahrt < zustand.getEinfahrtgroesse()) {
                    this.addAutoAnPosition(EINFAHRTWARTEBEREICHX,
                            EINFAHRTWARTEBEREICHY, this.posEinfahrt % 10,
                            this.posEinfahrt / 10);
                    this.posEinfahrt++;
                }
                break;
            case "A":
                if (this.posAusfahrt < zustand.getParkhausgroesse()) {
                    this.addAutoAnPosition(AUSFAHRTWARTEBEREICHX,
                            AUSFAHRTWARTEBEREICHY, this.posAusfahrt % 10,
                            this.posAusfahrt / 10);
                    this.posAusfahrt++;
                }
                break;
            case "B":
                if (this.posBaustelle < zustand.getBaustellenAutos()) {
                    this.addAutoAnPosition(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY,
                            this.posBaustelle / 10, this.posBaustelle % 10);
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
                    this.subAutoAnPosition(PARKHAUSX, PARKHAUSY,
                            this.posParkplatz % 20, this.posParkplatz / 20);
                }
                break;
            case "E":
                if (this.posEinfahrt > 0) {
                    this.posEinfahrt--;
                    this.subAutoAnPosition(EINFAHRTWARTEBEREICHX,
                            EINFAHRTWARTEBEREICHY, this.posEinfahrt % 10,
                            this.posEinfahrt / 10);
                }
                break;
            case "A":
                if (this.posAusfahrt > 0) {
                    this.posAusfahrt--;
                    this.subAutoAnPosition(AUSFAHRTWARTEBEREICHX,
                            AUSFAHRTWARTEBEREICHY, this.posAusfahrt % 10,
                            this.posAusfahrt / 10);
                }
                break;
            case "B":
                if (this.posBaustelle > 0) {
                    this.posBaustelle--;
                    this.subAutoAnPosition(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY,
                            this.posBaustelle / 10, this.posBaustelle % 10);
                }
                break;
        }
    }

    private void addAutoAnPosition(int pX, int pY, int x, int y) {
        fill(0, 0, 255);
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

    private void zeichneParkplatz(int pX, int pY, int maximal) {
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                if (y * 20 + x < maximal) {
                    rect(pX + (x * 10), pY + (y * 10), 10, 10);
                }
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
        PApplet.main(new String[]{gui.GuiT.class.getName()});
    }
}