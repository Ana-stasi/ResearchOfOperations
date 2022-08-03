package model.service;

import model.Route;

import java.util.ArrayList;
import java.util.List;

public class PrepareDataService {
    static int I = 9999;

    public List<Float[][]>  FloydWarshall(String[][] distanceMatrix, String[][] chanceMatrix) {
        List<Float[][]> list = new ArrayList<>();
        Float[][] distanceMtr = new Float[distanceMatrix.length][distanceMatrix.length];
        Float[][] chanceMtr = new Float[chanceMatrix.length][chanceMatrix.length];

        for (int i = 0; i < distanceMtr.length; i++)
            for (int j = 0; j < distanceMtr.length; j++) {
                distanceMtr[i][j] = Float.parseFloat(distanceMatrix[i][j]);
                chanceMtr[i][j] = Float.parseFloat(chanceMatrix[i][j]);
            }

        for (int k = 0; k < distanceMtr.length; k++)
            for (int i = 0; i < distanceMtr.length; i++)
                for (int j = 0; j < distanceMtr.length; j++) {
                    if (i != j && (distanceMtr[i][k] + distanceMtr[k][j] < distanceMtr[i][j])) {
                        distanceMtr[i][j] = distanceMtr[i][k] + distanceMtr[k][j];
                        chanceMtr[i][j] = chanceMtr[i][k]*chanceMtr[k][j];
                        chanceMtr[j][i] = chanceMtr[i][k]*chanceMtr[k][j];
                    }
                }

        for (int i = 0; i < distanceMtr.length; i++)
            for (int j = 0; j < distanceMtr.length; j++) {
                if (Float.parseFloat(distanceMatrix[i][j]) != I) {
                    distanceMtr[i][j] = Float.parseFloat(distanceMatrix[i][j]);
                    chanceMtr[i][j] = Float.parseFloat(chanceMatrix[i][j]);
                }
            }

        list.add(distanceMtr);
        list.add(chanceMtr);
        return list;
    }

    public static Route calculateLengthAndSafety(List<Integer> route, Float[][] distanceMrx, Float[][] safetyMrx) {
        float length = 0;
        float safety = 1;
        for (int i = 0; i < route.size(); i++) {
            if (i != route.size() - 1) {
                length += distanceMrx[route.get(i)][route.get(i + 1)];
                safety *= safetyMrx[route.get(i)][route.get(i + 1)];
            }
        }
        return new Route(route, length, safety);
    }

    public static Route getBestPerson(List<Route> routes) {
        Route bestChild = routes.get(0);
        for (Route route : routes) {
            if (route.getSafety() / route.getLength() > bestChild.getSafety() / bestChild.getLength())
                bestChild = route;
        }
        return bestChild;
    }

    public static Route getNormalRoute(Route oldRoute) {
        List<Integer> route = new ArrayList<>();
        for (Integer integer : oldRoute.getCityRoute()) route.add(integer + 1);
        return new Route(route,oldRoute.getLength(),oldRoute.getSafety());
    }

}
