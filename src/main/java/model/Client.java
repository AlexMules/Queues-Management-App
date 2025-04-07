package model;

public class Client {
    private final int id;
    private final int arrivalTime;
    private final int serviceTime;
    private int remainingServiceTime;
    //private int startProcessingTime = -1;

    public Client(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.remainingServiceTime = serviceTime;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + arrivalTime + ", " + remainingServiceTime + ")";
    }

    /* public void setStartProcessingTime(int time) {
        this.startProcessingTime = time;
    }

    public int getStartProcessingTime() {
        return startProcessingTime;
    } */

    public void decrementRemainingServiceTime() {
        if (remainingServiceTime > 0) {
            remainingServiceTime--;
        }
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getRemainingServiceTime() {
        return remainingServiceTime;
    }
}