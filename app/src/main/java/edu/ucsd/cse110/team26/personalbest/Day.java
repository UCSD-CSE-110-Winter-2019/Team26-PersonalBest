package edu.ucsd.cse110.team26.personalbest;

class Day {
    int goal;
    int totalSteps;
    int walkSteps;
    long timeStamp;

    public Day(int goal, int totalStep, int walkSteps, long timeStamp) {
        this.goal = goal;
        this.totalSteps = totalStep;
        this.walkSteps = walkSteps;
        this.timeStamp = timeStamp;
    }
}