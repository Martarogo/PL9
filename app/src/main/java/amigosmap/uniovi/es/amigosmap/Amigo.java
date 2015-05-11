package amigosmap.uniovi.es.amigosmap;

public class Amigo {

    private String _name;
    private double _latitude, _longitude;

    public Amigo (String name, double latitude, double longitude) {
        _name = name;
        _latitude = latitude;
        _longitude = longitude;
    }

    public String GetName() {
        return _name;
    }

    public double GetLatitude() {
        return _latitude;
    }

    public double GetLongitude() {
        return _longitude;
    }
}
