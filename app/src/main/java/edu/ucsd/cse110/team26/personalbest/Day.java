package edu.ucsd.cse110.team26.personalbest;

import java.util.Date;

public class Day {
    private int goal;
    private int totalStep;
    private int totalWalk;
    private Date date;

    public Day(int goal, int totalStep, int totalWalk, Date date)
    {
        this.goal = goal;
        this.totalStep = totalStep;
        this.totalWalk = totalWalk;
        this.date = date;
    }

    public int getTotalStep()
    {
        return totalStep;
    }

    public void setTotalStep(int totalStep)
    {
        this.totalStep = totalStep;
    }

    public int getTotalWalk()
    {
        return totalWalk;
    }
    public void setTotalWalk(int totalWalk)
    {
        this.totalWalk = totalWalk;
    }

    public void setGoal(int goal)
    {
        this.goal = goal;
    }

    public int getGoal()
    {
        return goal;
    }

    public Date getDay()
    {
        return date;
    }

    public void setDay()
    {

    }