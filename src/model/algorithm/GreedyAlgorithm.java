package model.algorithm;

import model.service.PrepareDataService;
import model.Route;

import java.util.ArrayList;
import java.util.List;

public class GreedyAlgorithm {
    int actionsAmount = 0;

    public Route findSolution(Float[][] distanceMatrix, Float[][] safetyMatrix, int startCity) {
        List<Integer> route = new ArrayList<>();
        float length = 0;
        float safety = 1;
        float[][] specificSafety = getSpecificSafety(distanceMatrix, safetyMatrix);//питома надійність маршруту
        int currentCity = startCity - 1;

        do {
            float maxSafety = 0;
            int nextCity = startCity - 1;
            for (int i = 0; i < specificSafety.length; i++) {
                actionsAmount++;
                if (!route.contains(i))
                    if (maxSafety < specificSafety[currentCity][i]) {
                        maxSafety = specificSafety[currentCity][i];
                        nextCity = i;
                    }
            }
            route.add(currentCity);
            length += distanceMatrix[currentCity][nextCity];
            safety *= safetyMatrix[currentCity][nextCity];
            currentCity = nextCity;
        } while (route.size() != distanceMatrix.length);
        route.add(currentCity);

        return PrepareDataService.getNormalRoute(new Route(route,length,safety));
    }

    private float[][] getSpecificSafety(Float[][] distanceMatrix, Float[][] safetyMatrix) {
        float[][] specificSafety = new float[distanceMatrix.length][distanceMatrix.length];
        for (int i = 0; i < specificSafety.length; i++)
            for (int j = 0; j < specificSafety.length; j++){
                actionsAmount++;
                if (i != j)
                    specificSafety[i][j] = safetyMatrix[i][j] / distanceMatrix[i][j];
            }
        return specificSafety;
    }

    public int getActionsAmount() {
        return actionsAmount;
    }
}
