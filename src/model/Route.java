package model;

import java.util.List;

public class Route {
    private List<Integer> cityRoute;
    private float length;
    private float safety;

    public Route(List<Integer> cityRoute, float length, float safety) {
        this.cityRoute = cityRoute;
        this.length = length;
        this.safety = safety;
    }

    @Override
    public String toString() {
        return "Route{" +
                "cityRoute=" + cityRoute +
                ", length=" + length +
                ", safety=" + String.format("%.18f",safety)+
                '}';
    }

    public List<Integer> getCityRoute() {
        return cityRoute;
    }

    public float getLength() {
        return length;
    }

    public float getSafety() {
        return safety;
    }
}
