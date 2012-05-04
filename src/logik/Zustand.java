package logik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import logik.interfaces.Uhrzeit;
import logik.interfaces.Warteschlange;
import logik.interfaces.Zeitspanne;

public class Zustand
{

    private final Simulation simulation;
    private final Uhrzeit zeit;
    private final Uhrzeit naechsteZeit;
    private final int anzahlEinfahrt;
    private final int anzahlAusfahrt;
    private final List<Uhrzeit> ankunftzeiten;
    private final List<Zeitspanne> standzeiten;
    private final Warteschlange baustelle;
    private final Warteschlange parkplatz;
    private final Ampel ampel;
    private final Uhrzeit letzteAmpelSchaltzeit;

    public Zustand(Simulation simulation, Uhrzeit zeit, List<Uhrzeit> ankunftzeiten, List<Zeitspanne> standzeiten,
            int parkplatzMaximum, Ampel ampel)
    {
        this.simulation = simulation;

        this.zeit = zeit;
        this.ankunftzeiten = ankunftzeiten;
        this.standzeiten = standzeiten;
        this.ampel = ampel;
        this.letzteAmpelSchaltzeit = zeit;

        this.anzahlEinfahrt = 0;
        this.anzahlAusfahrt = 0;
        this.baustelle = new WarteschlangeImpl(parkplatzMaximum);
        this.parkplatz = new WarteschlangeImpl(parkplatzMaximum);

        this.naechsteZeit = this.naechsteZeit();
    }

    public Zustand(Simulation simulation, Uhrzeit zeit, List<Uhrzeit> ankunftzeiten, List<Zeitspanne> standzeiten,
            Warteschlange baustelle, Warteschlange parkplatz, int anzahlEinfahrt, int anzahlAusfahrt,
            Ampel ampel, Uhrzeit letzteAmpelSchaltzeit)
    {
        this.simulation = simulation;

        this.zeit = zeit;
        this.ankunftzeiten = ankunftzeiten;
        this.standzeiten = standzeiten;
        this.anzahlEinfahrt = anzahlEinfahrt;
        this.anzahlAusfahrt = anzahlAusfahrt;
        this.baustelle = baustelle;
        this.parkplatz = parkplatz;
        this.ampel = ampel;
        this.letzteAmpelSchaltzeit = letzteAmpelSchaltzeit;

        this.naechsteZeit = this.naechsteZeit();
    }

    public Uhrzeit zeit()
    {
        return zeit;
    }

    public int anzahlEinfahrt()
    {
        return anzahlEinfahrt;
    }

    public int anzahlAusfahrt()
    {
        return anzahlAusfahrt;
    }

    public Uhrzeit letzteAmpelSchaltzeit()
    {
        return letzteAmpelSchaltzeit;
    }

    @Override
    public String toString()
    {
        return String.format("Zustand %s : STR %d | EIN %d | BAU %d | PAR %d [STA %d] | AUS %d || %s",
                zeit, ankunftzeiten.size(), anzahlEinfahrt, baustelle.laenge(),
                parkplatz.laenge(), standzeiten.size(), anzahlAusfahrt, ampel);
    }

    public Zustand naechsterZustand()
    {
        // nichts passiert?
        if (this.zeit == this.naechsteZeit)
        {
            return this;
        }

        Warteschlange naechsteBaustelle = this.naechsteBaustelle();
        Warteschlange naechsterParkplatz = naechsterParkplatz();

        List<Uhrzeit> naechsteAnkunftzeiten = this.naechsteAnkunftzeiten();
        List<Zeitspanne> naechsteStandzeiten = this.naechsteStandzeiten();

        int naechsteAnzahlEinfahrt = this.naechsteAnzahlEinfahrt();
        int naechsteAnzahlAusfahrt = this.naechsteAnzahlAusfahrt();

        Ampel naechsteAmpel = this.naechsteAmpel();
        Uhrzeit naechsteAmpelSchaltzeit = this.naechsteAmpelSchaltzeit();

        return new Zustand(simulation, naechsteZeit, naechsteAnkunftzeiten, naechsteStandzeiten,
                naechsteBaustelle, naechsterParkplatz, naechsteAnzahlEinfahrt, naechsteAnzahlAusfahrt,
                naechsteAmpel, naechsteAmpelSchaltzeit);
    }

    private Uhrzeit naechsteZeit()
    {
        List<Uhrzeit> zeiten = new ArrayList<Uhrzeit>();

        // auf die Einfahrt? Einfahrt voll?
        if (this.ankunftzeiten.size() > 0 && this.anzahlEinfahrt < simulation.strassenFassungsvermoegen())
        {
            zeiten.add(this.ankunftzeiten.get(0));
        }

        // auf die Baustelle? Ampel grün? Letztes Auto in der Baustelle >= 3s weg?
        if (this.anzahlEinfahrt > 0 && this.ampel == Ampel.EINFAHRT && (this.baustelle.laenge() == 0
                || this.baustelle.letzter().addiere(simulation.autoAbstand()).compareTo(this.zeit) > -1))
        {
            zeiten.add(this.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT));
        }

        // auf den Parkplatz? 
        if (this.baustelle.laenge() > 0)
        {
            zeiten.add(this.baustelle.naechster().addiere(simulation.baustellenZeit()));
        }

        // auf die Ausfahrt? Ausfahrt voll?
        if (this.parkplatz.laenge() > 0 && this.anzahlAusfahrt < simulation.strassenFassungsvermoegen())
        {
            zeiten.add(this.parkplatz.naechster());
        }

        // auf die Baustelle? Ampel gr�n? Letztes Auto in der Baustelle >= 3s weg?
        if (this.anzahlAusfahrt > 0 && this.ampel == Ampel.AUSFAHRT && (this.baustelle.laenge() == 0
                || this.baustelle.letzter().addiere(simulation.autoAbstand()).compareTo(this.zeit) > -1))
        {
            zeiten.add(this.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT));
        }

        // Autos auf der Ein/Ausfahrt und Ampel auf Stopp?
        if ((this.anzahlEinfahrt > 0 || this.anzahlAusfahrt > 0) && (this.ampel == Ampel.STOP_EINFAHRT
                || this.ampel == Ampel.STOP_AUSFAHRT) && this.baustelle.laenge() == 0)
        {
            zeiten.add(this.letzteAmpelSchaltzeit.addiere(simulation.minimaleAmpelSchaltzeit()));
        }

        Collections.sort(zeiten);
        return (zeiten.size() > 0 ? zeiten.get(0) : this.zeit);
    }

    private List<Uhrzeit> naechsteAnkunftzeiten()
    {
        if (this.ankunftzeiten.contains(naechsteZeit) && this.anzahlEinfahrt < simulation.strassenFassungsvermoegen())
        {
            List<Uhrzeit> zeiten = new ArrayList<Uhrzeit>();
            zeiten.addAll(this.ankunftzeiten);
            zeiten.remove(naechsteZeit);
            return zeiten;
        }

        return this.ankunftzeiten;
    }

    private int naechsteAnzahlEinfahrt()
    {
        int einfahrt = this.anzahlEinfahrt;

        // Straße -> Einfahrt
        if (this.ankunftzeiten.contains(naechsteZeit) && this.anzahlEinfahrt < simulation.strassenFassungsvermoegen())
        {
            einfahrt++;
        }

        // Einfahrt -> Baustelle
        if (this.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT).compareTo(naechsteZeit) == 0 && (this.baustelle.laenge() == 0
                || this.baustelle.letzter().addiere(simulation.autoAbstand()).compareTo(naechsteZeit) > -1)
                && this.ampel == Ampel.EINFAHRT && this.anzahlEinfahrt > 0)
        {
            einfahrt--;
        }

        return einfahrt;
    }

    private Warteschlange naechsteBaustelle()
    {
        Warteschlange naechsteBaustelle = this.baustelle;

        // Einfahrt/Ausfahrt -> Baustelle
        if (this.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT).compareTo(naechsteZeit) == 0
                && (this.baustelle.laenge() == 0 || this.baustelle.letzter().addiere(simulation.autoAbstand()).compareTo(naechsteZeit) > -1)
                && (this.ampel == Ampel.EINFAHRT && this.anzahlEinfahrt > 0)
                || (this.ampel == Ampel.AUSFAHRT && this.anzahlAusfahrt > 0))
        {
            naechsteBaustelle = baustelle.hinzufuegen(naechsteZeit);
        }

        // Baustelle -> Parkplatz/Strasse
        if (this.baustelle.laenge() > 0 && this.baustelle.naechster().addiere(simulation.baustellenZeit()).compareTo(naechsteZeit) < 1)
        {
            naechsteBaustelle = baustelle.entfernen();
        }

        return naechsteBaustelle;
    }

    private Warteschlange naechsterParkplatz()
    {
        Warteschlange naechsterParkplatz = this.parkplatz;

        // Baustelle -> Parkplatz
        if (this.baustelle.laenge() > 0 && (this.ampel == Ampel.EINFAHRT || this.ampel == Ampel.STOP_AUSFAHRT)
                && this.baustelle.naechster().addiere(simulation.baustellenZeit()).compareTo(naechsteZeit) == 0)
        {
            naechsterParkplatz = parkplatz.hinzufuegen(naechsteZeit.addiere(this.standzeiten.get(0)));
        }

        // Parkplatz -> Ausfahrt
        if (this.parkplatz.laenge() > 0 && this.parkplatz.naechster().compareTo(naechsteZeit) == 0
                && this.anzahlAusfahrt < simulation.strassenFassungsvermoegen())
        {
            naechsterParkplatz = parkplatz.entfernen();
        }

        return naechsterParkplatz;
    }

    private List<Zeitspanne> naechsteStandzeiten()
    {
        if (this.baustelle.laenge() > 0 && (this.ampel == Ampel.EINFAHRT || this.ampel == Ampel.STOP_AUSFAHRT)
                && this.baustelle.naechster().addiere(simulation.baustellenZeit()).compareTo(naechsteZeit) == 0)
        {
            List<Zeitspanne> zeiten = new ArrayList<Zeitspanne>();
            zeiten.addAll(this.standzeiten);
            zeiten.remove(0);
            return zeiten;
        }

        return this.standzeiten;
    }

    private int naechsteAnzahlAusfahrt()
    {
        int naechsteAnzahlAusfahrt = this.anzahlAusfahrt;

        // Parkplatz -> Ausfahrt
        if (this.parkplatz.laenge() > 0 && this.parkplatz.naechster().compareTo(naechsteZeit) == 0
                && this.anzahlAusfahrt < simulation.strassenFassungsvermoegen())
        {
            naechsteAnzahlAusfahrt++;
        }

        // Ausfahrt -> Baustelle
        if (this.zeit.addiere(ZeitspanneImpl.NAECHSTE_ZEITEINHEIT).compareTo(naechsteZeit) == 0
                && (this.baustelle.laenge() == 0 || this.baustelle.letzter().addiere(simulation.autoAbstand()).compareTo(naechsteZeit) > -1)
                && (this.ampel == Ampel.AUSFAHRT || this.ampel == Ampel.STOP_EINFAHRT) && this.anzahlAusfahrt > 0)
        {

            naechsteAnzahlAusfahrt--;
        }

        return naechsteAnzahlAusfahrt;
    }

    private Ampel naechsteAmpel()
    {
        return Ampel.naechsteAmpel(this.ampel, this.anzahlEinfahrt,
                this.baustelle.laenge(), this.parkplatz.laenge(),
                this.parkplatz.maximaleElemente(), this.anzahlAusfahrt,
                this.letzteAmpelSchaltzeit, naechsteZeit, simulation.minimaleAmpelSchaltzeit());
    }

    private Uhrzeit naechsteAmpelSchaltzeit()
    {
        return (this.ampel == this.naechsteAmpel() ? this.letzteAmpelSchaltzeit : naechsteZeit);
    }
}
