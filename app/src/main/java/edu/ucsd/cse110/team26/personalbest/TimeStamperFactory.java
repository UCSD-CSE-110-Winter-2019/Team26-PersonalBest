package edu.ucsd.cse110.team26.personalbest;

class TimeStamperFactory {

    static TimeStamper create(boolean debug) {
        if (debug) {
            return new MockTimeStamper();
        } else {
            return new ConcreteTimeStamper();
        }
    }

}