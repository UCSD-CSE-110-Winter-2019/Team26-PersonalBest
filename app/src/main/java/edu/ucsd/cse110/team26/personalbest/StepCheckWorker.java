package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class StepCheckWorker extends Worker {

    Context context;

    public StepCheckWorker(Context context, WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        TimeStamper timeStamper = new ConcreteTimeStamper();
        FitnessService fitnessService = new GoogleFitAdapter(context);
        IDataAdapter dataAdapter = new FirestoreAdapter(context, timeStamper);
        Settings settings = new Settings(context, timeStamper);
        int goal = settings.getGoal();

        fitnessService.updateStepCount(steps -> {

            // Only run if steps were successfully gotten
            if(steps <= 0) return;

            // Check if goal accomplished, and launch notification
            Log.d(getClass().getSimpleName(), "Worker checked steps and got " + steps);
            if(steps > goal) {
                GoalNotifications n = new GoalNotifications(context);
                n.createNotificationChannel();
                n.showNotification();
            }

            // Update Firebase with day data
            List<Walk> walkList = new ArrayList<>();
            try {
                fitnessService.getWalks(timeStamper.startOfDay(timeStamper.now()), timeStamper.endOfDay(timeStamper.now()), walkList);
                Thread.sleep(10000);
                int walkSteps = 0;
                for(Walk w : walkList) {
                    walkSteps += w.getSteps();
                }
                Day today = new Day(goal, 0, walkSteps, timeStamper.now());
                today.totalSteps = steps;
                List<Day> dayList = new ArrayList<>();
                dayList.add(today);
                dataAdapter.updateDays(dayList, (success) -> {
                    Log.d(getClass().getSimpleName(), "Attempted to update Firebase with " + today + " with success: " + success);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        return Result.success();
    }
}
