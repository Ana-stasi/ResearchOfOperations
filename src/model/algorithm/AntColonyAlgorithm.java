package model.algorithm;

import model.service.PrepareDataService;
import model.Route;

import java.util.*;

public class AntColonyAlgorithm {
    int actionsAmount = 0;

    public Route findSolution(Float[][] distanceMrx, Float[][] safetyMrx, int iterations, int startCity,
                                     int antsAmount, float crowdLevel, float distanceGreedyLevel, float safetyGreedyLevel,
                                     float startPheromone, float evapormationPheromone) {
        Route bestRoute = null;
        Float[][] pheromones = new Float[distanceMrx.length][distanceMrx.length];
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                actionsAmount++;
                if (i != j)
                    pheromones[i][j] = startPheromone;
                else pheromones[i][j] = 9999F;
            }
        }

        int i = 0;
        while (i < iterations) {
            Float[][] distanceCopy = new Float[distanceMrx.length][distanceMrx.length];
            for (int j = 0; j < distanceMrx.length; j++){
                actionsAmount++;
                System.arraycopy(distanceMrx[j], 0, distanceCopy[j], 0, distanceMrx.length);
            }

            Float[][] safetyCopy = new Float[safetyMrx.length][safetyMrx.length];
            for (int j = 0; j < safetyMrx.length; j++){
                actionsAmount++;
                System.arraycopy(safetyMrx[j], 0, safetyCopy[j], 0, safetyMrx.length);
            }

            List<Route> antsColony = findAllRoutes(antsAmount, startCity, distanceCopy, safetyCopy, pheromones,
                    crowdLevel, distanceGreedyLevel, safetyGreedyLevel);
            updatePheromones(pheromones, distanceCopy, safetyCopy, evapormationPheromone, antsColony);
            bestRoute = PrepareDataService.getBestPerson(antsColony);
            i++;
        }
        return PrepareDataService.getNormalRoute(bestRoute);
    }

    private List<Route> findAllRoutes(int antsAmount, int startCity, Float[][] distanceMrx, Float[][] safetyMrx,
                                             Float[][] pheromones, float crowdLevel, float distanceGreedyLevel,
                                             float safetyGreedyLevel) {
        List<Route> antsColony = new ArrayList<>(antsAmount);
        int i = 0;
        while (i < antsAmount) {
            actionsAmount++;
            Route route = createRoute(startCity, distanceMrx, safetyMrx, pheromones, crowdLevel, distanceGreedyLevel,
                    safetyGreedyLevel);
            antsColony.add(route);
            i++;
        }
        return antsColony;
    }

    private Route createRoute(int startCity, Float[][] distanceMrx, Float[][] safetyMrx, Float[][] pheromones,
                                     float crowdLevel, float distanceGreedyLevel, float safetyGreedyLevel) {
        List<Integer> route = new ArrayList<>();
        Integer currentCity = startCity - 1;
        route.add(currentCity);
        List<Integer> cities = new ArrayList<>();
        for (int i = 0; i < distanceMrx.length; i++) cities.add(i);
        cities.remove(currentCity);

        while (route.size() < distanceMrx.length) {
            actionsAmount++;
            Map<Integer, Float> accessibleCities = calculateProbabilitiesOfTransition(cities, currentCity, pheromones,
                    crowdLevel, distanceMrx, distanceGreedyLevel, safetyMrx, safetyGreedyLevel);
            Map<Integer, Float> cumulativeSeries = getCumulativeSeries(accessibleCities);
            currentCity = getNextCity(cumulativeSeries, startCity - 1);
            cities.remove(currentCity);
            route.add(currentCity);
        }
        route.add(startCity - 1);
        return PrepareDataService.calculateLengthAndSafety(route, distanceMrx, safetyMrx);
    }

    private Map<Integer, Float> calculateProbabilitiesOfTransition(List<Integer> cities, int currentCity, Float[][] pheromones, float crowdLevel,
                                                                          Float[][] distanceMrx, float distanceGreedyLevel, Float[][] safetyMrx, float safetyGreedyLevel) {
        float sum = 0;
        Map<Integer, Float> availableCities = new HashMap<>();
        for (Integer city : cities){
            actionsAmount++;
            sum += calculateProbability(currentCity,city,pheromones,crowdLevel,distanceMrx, distanceGreedyLevel,
                    safetyMrx,safetyGreedyLevel);
        }
        for (Integer city : cities) {
            actionsAmount++;
            float numenator = calculateProbability(currentCity,city,pheromones,crowdLevel,distanceMrx,
                                                    distanceGreedyLevel,safetyMrx,safetyGreedyLevel);
            Float probability = (numenator / sum);
            availableCities.put(city, probability);
        }
        return availableCities;
    }
    private float calculateProbability(int currentCity, int city, Float[][] pheromones,
                                              float crowdLevel, Float[][] distanceMrx, float distanceGreedyLevel,
                                              Float[][] safetyMrx, float safetyGreedyLevel){
        actionsAmount++;
        float pheromone = (float) Math.pow(pheromones[currentCity][city], crowdLevel);
        float distance = (float) Math.pow(distanceMrx[currentCity][city], distanceGreedyLevel);
        float safety = (float) Math.pow(safetyMrx[currentCity][city], safetyGreedyLevel);
        return pheromone * 1 / distance * safety;
    }

    private Map<Integer, Float> getCumulativeSeries(Map<Integer, Float> cities) {
        Map<Integer, Float> cumulativeSeries = new HashMap<>();
        float sum = 0;
        for (Map.Entry entry : cities.entrySet()) {
            actionsAmount++;
            sum += (float) entry.getValue();
            cumulativeSeries.put((Integer) entry.getKey(), sum);
        }
        return cumulativeSeries;
    }

    private Integer getNextCity(Map<Integer, Float> cities, int startCity) {
        double randomNumber = 0 + Math.random() * 1;
        Integer city = -1;
        for (Map.Entry entry : cities.entrySet()) {
            actionsAmount++;
            float number = (float) entry.getValue();
            if (randomNumber < number) {
                city = (Integer) entry.getKey();
                break;
            }
        } return city;
    }


    private Float[][] updatePheromones(Float[][] pheromones, Float[][] distanceMrx, Float[][] safetyMrx,
                                              float evapormationPheromone, List<Route> antsColony) {
        float idealRoute = getIdeal(distanceMrx, safetyMrx);
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                float sum = 0;
                for (Route route : antsColony) {
                    for (int k = 0; k < route.getCityRoute().size() - 1; k++){
                        actionsAmount++;
                        if (route.getCityRoute().get(k) == i && route.getCityRoute().get(k + 1) == j )
                        {
                            sum += (route.getSafety() / route.getLength()) / idealRoute;
                        }
                    }
                    if (i != j) {
                        float pheromone = pheromones[i][j] * (1-evapormationPheromone) + sum;
                        pheromones[i][j] = pheromone;
                        pheromones[j][i] = pheromone;
                    }
                }
            }
        }
        return pheromones;
    }

    public Float getIdeal(Float[][] distanceMrx, Float[][] safetyMrx) {
        actionsAmount++;
        Float safety = getMaxSafety(safetyMrx);
        Float length = getMinLength(distanceMrx);
        return safety / length;
    }

    private Float getMaxSafety(Float[][] safetyMrx) {
        List<Float> sortedSafetyMrx = new ArrayList<>();
        for (int i = 0; i < safetyMrx.length; i++)
            for (int j = 0; j < safetyMrx.length; j++){
                actionsAmount++;
                if (i != j)
                    sortedSafetyMrx.add(safetyMrx[i][j]);
            }
        Collections.sort(sortedSafetyMrx);
        Collections.reverse(sortedSafetyMrx);
        int i = 0;
        float safety = 1;
        while (i < safetyMrx.length) {
            actionsAmount++;
            safety *= sortedSafetyMrx.get(i);
            i++;
        }
        return safety;
    }

    private Float getMinLength(Float[][] distanceMrx) {
        float minLength = 0;
        for (int i = 0; i < distanceMrx.length; i++) {
            float min = 9999;
            for (int j = 0; j < distanceMrx.length; j++){
                actionsAmount++;
                if (distanceMrx[i][j] < min)
                    min = distanceMrx[i][j];
            }
            for (int j = 0; j < distanceMrx.length; j++){
                actionsAmount++;
                if (i != j)
                    distanceMrx[i][j] = distanceMrx[i][j] - min;
            }
            minLength += min;
        }
        for (int i = 0; i < distanceMrx.length; i++) {
            float min = 9999;
            for (Float[] mrx : distanceMrx){
                actionsAmount++;
                if (mrx[i] < min)
                    min = mrx[i];
            }
            minLength += min;
        }
        return minLength;
    }

    public int getActionsAmount() {
        return actionsAmount;
    }
}
