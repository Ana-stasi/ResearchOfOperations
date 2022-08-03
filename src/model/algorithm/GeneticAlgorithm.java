package model.algorithm;

import model.service.PrepareDataService;
import model.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GeneticAlgorithm {
    int actionsAmount = 0;


    public Route findSolution(Float[][] distanceMrx, Float[][] safetyMrx, int iterations, int startCity,
                                     int populationSize, int parentsAmount, double mutationChance) {

        List<Route> population = createPopulation(distanceMrx, safetyMrx, startCity, populationSize);
        Route bestRoute = PrepareDataService.getBestPerson(population);
        int i = 0;
        while (i < iterations) {
            actionsAmount++;
            List<Route> parents = tournamentParentSelection(population, parentsAmount);
            List<Route> children = getChildren(parents, distanceMrx, safetyMrx);
            List<Route> childrenMuted = muteChildren(children, mutationChance, distanceMrx, safetyMrx);
            List<Route> betterChildren = localImprovement(childrenMuted, distanceMrx, safetyMrx);
            population = new ArrayList<>(updatePopulation(population, betterChildren));
            bestRoute = PrepareDataService.getBestPerson(population);
            i++;
        }
        return PrepareDataService.getNormalRoute(bestRoute);
    }

    private List<Route> createPopulation(Float[][] distanceMrx, Float[][] safetyMrx, int startCity, int size) {
        List<Route> population = new ArrayList<>(size);
        do {
            actionsAmount++;
            Route route = createRoute(distanceMrx, safetyMrx, startCity);
            if (!population.contains(route))
                population.add(route);

        } while (population.size() < size);
        return population;
    }

    private Route createRoute(Float[][] distanceMrx, Float[][] safetyMrx, int startCity) {
        List<Integer> route = new ArrayList<>();
        float length = 0;
        float safety = 1;
        int currentCity = startCity - 1;
        route.add(currentCity);
        do {
            while (true) {
                actionsAmount++;
                int city = (int) (0 + Math.random() * (distanceMrx.length));
                if (!route.contains(city)) {
                    length += distanceMrx[currentCity][city];
                    safety *= safetyMrx[currentCity][city];
                    currentCity = city;
                    break;
                }
            }
            route.add(currentCity);
        } while (route.size() < distanceMrx.length);
        route.add(startCity - 1);
        return new Route(route, length, safety);
    }

    private List<Route> tournamentParentSelection(List<Route> population, int parentsAmount) {
        List<Route> parents = new ArrayList<>();
        List<Route> tempPop = new ArrayList<>(List.copyOf(population));

        while (parents.size() < parentsAmount) {
            actionsAmount++;
            List<Route> firstGroup = getFirstGroup(tempPop);
            List<Route> secondGroup = getSecondGroup(tempPop, firstGroup);
            Route firstBest = PrepareDataService.getBestPerson(firstGroup);
            Route secondBest = PrepareDataService.getBestPerson(secondGroup);
            if (parentsAmount - parents.size() == 1) {
                parents.add(getBestFromBest(firstBest, secondBest));
            } else {
                parents.add(firstBest);
                parents.add(secondBest);
            }
            tempPop.remove(firstBest);
            tempPop.remove(secondBest);
        }
        return parents;
    }

    private Route getBestFromBest(Route firstBest, Route secondBest) {
        actionsAmount++;
        if (firstBest.getSafety() / firstBest.getLength() > secondBest.getSafety() / secondBest.getLength())
            return firstBest;
        else return secondBest;
    }

    private List<Route> getFirstGroup(List<Route> population) {
        List<Route> group = new ArrayList<>();
        group.add(population.get((int) (0 + Math.random() * (population.size()))));
        while (group.size() < population.size() / 2) {
            int city = (int) (0 + Math.random() * (population.size()));
            Route route = population.get(city);
            actionsAmount++;
            if (!group.contains(route))
                group.add(route);
        }
        return group;
    }

    private List<Route> getSecondGroup(List<Route> population, List<Route> firstGroup) {
        List<Route> group = new ArrayList<>();
        for (Route route : population) {
            actionsAmount++;
            if (!firstGroup.contains(route))
                group.add(route);
        }
        return group;
    }

    private List<Route> getChildren(List<Route> parents, Float[][] distanceMrx, Float[][] safetyMrx) {
        List<Route> children = new ArrayList<>();
        for (Route parent : parents) {
            actionsAmount++;
            Integer[] randomPoints = getRandomPoints(parent.getCityRoute(), parent.getCityRoute().size() - 1);
            List<Integer> route = inverseRoute(parent, randomPoints);
            Route child = PrepareDataService.calculateLengthAndSafety(route, distanceMrx, safetyMrx);
            children.add(child);
        }
        return children;
    }

    private Integer[] getRandomPoints(List<Integer> route, int bound) {
        Integer[] randomPoints = new Integer[2];
        int amount = 1;
        randomPoints[0] = generatePoint(route, bound);
        while (amount < 2) {
            int secondPoint = generatePoint(route, bound);
            actionsAmount++;
            if (secondPoint != randomPoints[0]) {
                randomPoints[1] = secondPoint;
                amount++;
            }
        }
        return randomPoints;
    }

    private int generatePoint(List<Integer> route, int bound) {
        int point = -1;
        while (point == -1) {
            int randomPoint = (int) (0 + Math.random() * (bound));
            int repeatAmount = 0;
            for (Integer city : route){
                actionsAmount++;
                if (city.equals(randomPoint))
                    repeatAmount++;
            }
            if (repeatAmount != 2)
                point = randomPoint;
        }
        return point;
    }

    private List<Integer> inverseRoute(Route parent, Integer[] randomPoints) {
        actionsAmount++;
        List<Integer> position = getPosition(parent, randomPoints);
        Integer[] route = parent.getCityRoute().toArray(new Integer[0]);
        Integer[] start = Arrays.copyOfRange(route, 0, position.get(0));
        Integer[] middle = Arrays.copyOfRange(route, position.get(0), position.get(1) + 1);
        Integer[] end = Arrays.copyOfRange(route, position.get(1) + 1, parent.getCityRoute().size());
        Collections.reverse(Arrays.asList(middle));
        Integer[] both = Stream.concat(Arrays.stream(start), Arrays.stream(middle)).toArray(Integer[]::new);
        Integer[] child = Stream.concat(Arrays.stream(both), Arrays.stream(end)).toArray(Integer[]::new);
        return Arrays.asList(child);
    }

    private List<Integer> getPosition(Route route, Integer[] randomPoints) {
        List<Integer> position = new ArrayList<>();
        for (int i = 0; i < route.getCityRoute().size(); i++) {
            for (int randomPoint : randomPoints) {
                actionsAmount++;
                if (route.getCityRoute().get(i) == randomPoint)
                    position.add(i);
            }
        }
        return position;
    }

    private List<Route> muteChildren(List<Route> children, double mutationChance, Float[][] distanceMrx, Float[][] safetyMrx) {
        List<Route> mutedChildren = new ArrayList<>();
        for (Route child : children) {
            double randomNumber = 0 + Math.random() * 1;
            actionsAmount++;
            if (randomNumber < mutationChance)
                mutedChildren.add(muteChild(child, distanceMrx, safetyMrx));
            else mutedChildren.add(child);
        }
        return mutedChildren;
    }

    private Route muteChild(Route child, Float[][] distanceMrx, Float[][] safetyMrx) {
        actionsAmount++;
        Integer[] randomPoints = getRandomPoints(child.getCityRoute(), child.getCityRoute().size() - 1);
        List<Integer> route = new ArrayList(child.getCityRoute());
        route.remove(randomPoints[1]);
        route.add(route.indexOf(randomPoints[0]), randomPoints[1]);
        return PrepareDataService.calculateLengthAndSafety(route, distanceMrx, safetyMrx);
    }

    private List<Route> localImprovement(List<Route> children, Float[][] distanceMrx, Float[][] safetyMrx) {
        List<Route> betterChildren = new ArrayList<>(children);
        Route bestChild = PrepareDataService.getBestPerson(children);
        List<Integer> bestRoute = new ArrayList<>(bestChild.getCityRoute());
        List<Route> localChildren = new ArrayList<>();
        for (int i = 0; i < bestRoute.size() - 1; i++) {
            actionsAmount++;
            Collections.swap(bestRoute, i, i + 1);
            Route localChild = PrepareDataService.calculateLengthAndSafety(bestRoute, distanceMrx, safetyMrx);
            localChildren.add(localChild);
            Collections.swap(bestRoute, i + 1, i);
        }
        Route bestLocalChild = PrepareDataService.getBestPerson(localChildren);
        if (bestChild.getSafety() / bestChild.getLength() < bestLocalChild.getSafety() / bestLocalChild.getLength())
            betterChildren.set(betterChildren.indexOf(bestChild), bestLocalChild);
        return betterChildren;
    }

    private Route getWorstPerson(List<Route> children) {
        Route worstPerson = children.get(0);
        for (Route route : children) {
            actionsAmount++;
            if (route.getSafety() / route.getLength() < worstPerson.getSafety() / worstPerson.getLength())
                worstPerson = route;
        }
        return worstPerson;
    }

    private List<Route> updatePopulation(List<Route> population, List<Route> children) {
        while (children.size() != 0) {
            actionsAmount++;
            Route bestChild = PrepareDataService.getBestPerson(children);
            children.remove(bestChild);
            Route worstFromPopulation = getWorstPerson(population);
            if (bestChild.getSafety() / bestChild.getLength() > worstFromPopulation.getSafety() / worstFromPopulation.getLength()) {
                if (!population.contains(bestChild))
                    population.set(population.indexOf(worstFromPopulation), bestChild);
            } else break;
        }
        return population;
    }

    public int getActionsAmount() {
        return actionsAmount;
    }
}
