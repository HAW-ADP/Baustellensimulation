package gui.logik;

public interface Zustand {
	
	/** Die Ampelzustaende */
	public static final int EINFAHRT = 1;
	public static final int STOP_EINFAHRT = 2;
	public static final int AUSFAHRT = 3;
	public static final int STOP_AUSFAHRT = 4;
	public static final int STOP_BEIDE = 5;
	
	public Zustand naechsterZustand();
	
	public Uhrzeit getUhrzeit();
	
	public int getAusfahrtAutos();
	
	public int getEinfahrtAutos();
	
	public int getAmpelzustand();
	
	public int getParkplatzAutos();
	
	public int getBaustellenAutos();
        
	public int getParkhausgroesse();
        
    public int getEinfahrtgroesse();
    
    public int getVorbeigefahreneAutos();
	
}
