package vehicles;

public interface VesselObserver {
	
	public abstract void onVesselEvent(Vessel v, Vessel.VesselEvents e);

}
