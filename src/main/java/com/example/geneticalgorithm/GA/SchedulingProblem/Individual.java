package com.company.SchedulingProblem;

public class Individual {

    private static final int DAYS = 5;
    private static final int SHIFTS_PER_DAYS = 3;
    private static final int EMPLOYEES = 5;

    private int[][] schedule;

    public Individual(int[][] sch){
        schedule = sch;
    }

    public int[][] getSchedule(){
        return schedule;
    }

    public static int getNumberOfGenesPerEmployee(){
        return DAYS*SHIFTS_PER_DAYS;
    }

    public static int getShiftsPerDays() {
        return SHIFTS_PER_DAYS;
    }

    public static int getEMPLOYEES() {
        return EMPLOYEES;
    }
}
