package gui;

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
    private PImage welt = loadImage("src\\gui\\img\\welt.jpg");
    private PImage weltUnten = loadImage("src\\gui\\img\\weltUnten.jpg");
    private PImage auto1 = loadImage("src\\gui\\img\\auto1.jpg");
    private PImage auto2 = loadImage("src\\gui\\img\\auto2.jpg");
    private float demoAuto1PosX = 500;
    private float demoAuto1PosY = 471;
    private float demoAuto2PosX = 0;
    private float demoAuto2PosY = 545;
    private int pos = 0;
    private int posEinfahrt = 0;
    private int posAusfahrt = 0;
    private int posBaustelle = 0;
    private int steps = 0;
    private int stepsEinfahrt = 0;
    private int stepsAusfahrt = 0;
    private int stepsBaustelle = 0;
    private String ampelEinfahrt = "R";
    private String ampelAusfahrt = "R";
    private int ampelZustand = 1;

    @Override
    public void setup() {
        size(502, 576);
        background(255);
        frameRate(30);
        image(welt, 0, 0);
        image(weltUnten, 0, 469);

        // Parkplatz mit 10x10 Pl�tzen
        zeichneRaster(PARKHAUSX, PARKHAUSY, 10, 10);
        // Warteschlange vor der Ausfahrt
        zeichneRaster(AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY, 10, 1);
        // Wartebereich vor der Einfahrt
        zeichneRaster(EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY, 10, 1);
        // Baustellenbereich
        zeichneRaster(BAUSTELLENBEREICHX, BAUSTELLENBEREICHY, 1, 4);
    }

    @Override
    public void draw() {
        stroke(0);
        //Immer wieder laden, da das Spielfeld verwischt wird
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
        // Ampel f�r die Einfahrt
        zeichneAmpel(EINFAHRTAMPELX, EINFAHRTAMPELY, ampelEinfahrt);
        // Ampel f�r die Ausfahrt
        zeichneAmpel(AUSFAHRTAMPELX, AUSFAHRTAMPELY, ampelAusfahrt);
        bewegeDemoAuto();
        // addiere Auto
        if (this.steps > 0) {
            this.addiereAutos("P", PARKHAUSX, PARKHAUSY);
            this.steps--;
        } else if (this.stepsEinfahrt > 0) {
            this.addiereAutos("A", EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY);
            this.stepsEinfahrt--;
        } else if (this.stepsAusfahrt > 0) {
            this.addiereAutos("E", AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY);
            this.stepsAusfahrt--;
        } else if (this.stepsBaustelle > 0) {
            this.addiereAutos("B", BAUSTELLENBEREICHX, BAUSTELLENBEREICHY);
            this.stepsBaustelle--;
        }
    }

    private void zeichneAmpel(int x, int y, String farbe) {
        if (farbe.equals("R")) {
            fill(255, 0, 0);
        }
        if (farbe.equals("G")) {
            fill(0, 255, 0);
        }
        ellipse(x, y, 10, 10);
    }

    @Override
    public void mouseReleased() {
        if (mouseX >= 5 && mouseX <= 55 && mouseY >= 5 && mouseY <= 30) {
            this.addiereAutos("P", PARKHAUSX, PARKHAUSY);
        } else if (mouseX >= 60 && mouseX <= 110 && mouseY >= 5 && mouseY <= 30) {
            this.subtrahiereAutos("P", PARKHAUSX, PARKHAUSY);
        } else if (mouseX >= 115 && mouseX <= 165 && mouseY >= 5
                && mouseY <= 30) {
            this.addiereAutos("E", EINFAHRTWARTEBEREICHX, EINFAHRTWARTEBEREICHY);
        } else if (mouseX >= 170 && mouseX <= 220 && mouseY >= 5
                && mouseY <= 30) {
            this.subtrahiereAutos("E", EINFAHRTWARTEBEREICHX,
                    EINFAHRTWARTEBEREICHY);
        } else if (mouseX >= 225 && mouseX <= 275 && mouseY >= 5
                && mouseY <= 30) {
            this.addiereAutos("A", AUSFAHRTWARTEBEREICHX, AUSFAHRTWARTEBEREICHY);
        } else if (mouseX >= 280 && mouseX <= 330 && mouseY >= 5
                && mouseY <= 30) {
            this.subtrahiereAutos("A", AUSFAHRTWARTEBEREICHX,
                    AUSFAHRTWARTEBEREICHY);
        } else if (mouseX >= 335 && mouseX <= 385 && mouseY >= 5
                && mouseY <= 30) {
            this.addiereAutos("B", BAUSTELLENBEREICHX, BAUSTELLENBEREICHY);
        } else if (mouseX >= 390 && mouseX <= 440 && mouseY >= 5
                && mouseY <= 30) {
            this.subtrahiereAutos("B", BAUSTELLENBEREICHX,
                    BAUSTELLENBEREICHY);
        } else if (mouseX >= 445 && mouseX <= 495 && mouseY >= 5
                && mouseY <= 30) {
            this.ampelWechsleZustand();
        }
    }

    private void ampelWechsleZustand() {
        if (ampelZustand == 0) {
            ampelAusfahrt = "R";
            ampelEinfahrt = "R";
            ampelZustand++;
        } else if (ampelZustand == 1) {
            ampelAusfahrt = "G";
            ampelEinfahrt = "R";
            ampelZustand++;
        } else if (ampelZustand == 2) {
            ampelAusfahrt = "R";
            ampelEinfahrt = "R";
            ampelZustand++;
        } else if (ampelZustand == 3) {
            ampelAusfahrt = "R";
            ampelEinfahrt = "G";
            ampelZustand++;
        }
        if (ampelZustand > 3) {
            ampelZustand = 0;
        }
    }

    public void addiereAutos(String wo, int x, int y) {
        int anz;
        switch (wo) {
            case "P":
                anz = 100;
                if (this.pos < anz) {
                    int zeile = this.pos / 10;
                    int spalte = this.pos % 10;
                    this.addAutoAnPosition(x, y, spalte, zeile);
                    this.pos++;
                }
                break;
            case "E":
                anz = 10;
                if (this.posEinfahrt < anz) {
                    int zeile = this.posEinfahrt / 10;
                    int spalte = this.posEinfahrt % 10;
                    this.addAutoAnPosition(x, y, spalte, zeile);
                    this.posEinfahrt++;
                }
                break;
            case "A":
                anz = 10;
                if (this.posAusfahrt < anz) {
                    int zeile = this.posAusfahrt / 10;
                    int spalte = this.posAusfahrt % 10;
                    this.addAutoAnPosition(x, y, spalte, zeile);
                    this.posAusfahrt++;
                }
                break;
            case "B":
                anz = 4;
                if (this.posBaustelle < anz) {
                    int zeile = this.posBaustelle / 10;
                    int spalte = this.posBaustelle % 10;
                    this.addAutoAnPosition(x, y, zeile, spalte);
                    this.posBaustelle++;
                }
                break;
        }
    }

    public void subtrahiereAutos(String wo, int x, int y) {
        switch (wo) {
            case "P":
                if (this.pos > 0) {
                    this.pos--;
                    int zeile = this.pos / 10;
                    int spalte = this.pos % 10;
                    this.subAutoAnPosition(x, y, spalte, zeile);
                }
                break;
            case "E":
                if (this.posEinfahrt > 0) {
                    this.posEinfahrt--;
                    int zeile = this.posEinfahrt / 10;
                    int spalte = this.posEinfahrt % 10;
                    this.subAutoAnPosition(x, y, spalte, zeile);
                }
                break;
            case "A":
                if (this.posAusfahrt > 0) {
                    this.posAusfahrt--;
                    int zeile = this.posAusfahrt / 10;
                    int spalte = this.posAusfahrt % 10;
                    this.subAutoAnPosition(x, y, spalte, zeile);
                }
                break;
           case "B":
                if (this.posBaustelle > 0) {
                    this.posBaustelle--;
                    int zeile = this.posBaustelle / 10;
                    int spalte = this.posBaustelle % 10;
                    this.subAutoAnPosition(x, y, zeile, spalte);
                }
                break;
        }
    }

    public void addAutoAnPosition(int pX, int pY, int x, int y) {
        fill(255, 0, 0);
        rect(pX + (x * 10), pY + (y * 10), 10, 10);
        fill(255, 255, 255);
    }

    public void subAutoAnPosition(int pX, int pY, int x, int y) {
        fill(255, 255, 255);
        rect(pX + (x * 10), pY + (y * 10), 10, 10);
    }

    public void zeichneRaster(int pX, int pY, int cols, int rows) {
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
