package logik;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import logik.interfaces.Uhrzeit;
import logik.interfaces.Zeitspanne;

public class Simulation implements Iterable<Zustand>
{

    private final Zeitspanne baustellenZeit;
    private final Zeitspanne autoAbstand;
    private final int strassenFassungsvermoegen;
    private final Zeitspanne minimaleAmpelSchaltzeit;
    private final Zustand startZustand;
    private List<Zustand> zustandsHistorie;

    public Simulation(Uhrzeit startZeit, List<Uhrzeit> autoUhrzeiten, List<Zeitspanne> autoStandzeiten,
            int parkplatzMaximum, Ampel startAmpel, Zeitspanne baustellenZeit,
            Zeitspanne autoAbstand, int strassenFassungsvermoegen, Zeitspanne minimaleAmpelSchaltzeit)
    {
        this.baustellenZeit = baustellenZeit;
        this.autoAbstand = autoAbstand;
        this.strassenFassungsvermoegen = strassenFassungsvermoegen;
        this.minimaleAmpelSchaltzeit = minimaleAmpelSchaltzeit;

        this.startZustand = new Zustand(this, startZeit, autoUhrzeiten, autoStandzeiten, parkplatzMaximum, startAmpel);
        this.zustandsHistorie = new ArrayList<Zustand>();
    }
    
    public Zeitspanne baustellenZeit(){
        return this.baustellenZeit;
    }
    
    public Zeitspanne autoAbstand(){
        return this.autoAbstand;
    }
    
    public Zeitspanne minimaleAmpelSchaltzeit() {
        return this.minimaleAmpelSchaltzeit;
    }
    
    public int strassenFassungsvermoegen() {
        return this.strassenFassungsvermoegen;
    }

    @Override
    public Iterator<Zustand> iterator()
    {
        return new ZustandsIter();
    }

    private class ZustandsIter implements Iterator<Zustand>
    {

        @Override
        public boolean hasNext()
        {
            if (zustandsHistorie.isEmpty())
            {
                return true;
            }

            Zustand current = zustandsHistorie.get(zustandsHistorie.size() - 1);
            Zustand next = current.naechsterZustand();

            return current != next;
        }

        @Override
        public Zustand next()
        {
            Zustand next;
            if (zustandsHistorie.isEmpty())
            {
                next = startZustand;
            }
            else
            {
                Zustand current = zustandsHistorie.get(zustandsHistorie.size() - 1);
                next = current.naechsterZustand();
            }

            zustandsHistorie.add(next);
            return next;
        }

        @Override
        public void remove()
        {
            zustandsHistorie.remove(zustandsHistorie.size() - 1);
        }
    }
}
