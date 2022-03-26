package me.nov.cafecompare.thread;

public class ThreadKiller extends Thread {
    private final Thread target;
    private final long max;

    public ThreadKiller(Thread target, long max) {
        this.target = target;
        this.max = max;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        try {
            Thread.sleep(max);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (target.isAlive())
            target.stop();
    }
}
