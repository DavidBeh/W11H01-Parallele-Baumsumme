package pgdp.threadswarmup;

import java.util.Optional;

public class SumThread extends Thread {
    private Optional<Integer> sum;

    private Node node;

    private int remainingThreads;

    SumThread leftThread;
    SumThread rightThread;


    public SumThread(Node node, int remainingThreads) {
        // TODO Exercise 2
        this.node = node;
        this.remainingThreads = remainingThreads;
        sum = Optional.empty();
    }

    public Optional<Integer> getSum() {
        return sum;
    }

    public void run() {
        if (remainingThreads == 0) sum = Optional.of(node.sum());
        else {
            startChildThreads();
            try {
                if (leftThread != null && leftThread.isAlive()) leftThread.join();
                if (rightThread != null && rightThread.isAlive()) rightThread.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }

            sum = Optional.of(node.getValue() +
                    (leftThread == null ? 0 : leftThread.sum.get())
                    + (rightThread == null ? 0 : rightThread.sum.get()));
        }
        // TODO Exercise 5
    }

    protected void startChildThreads() {
        var leftTC = leftThreadCount();
        var rightTC = node.getRight().isEmpty() ? 0 : remainingThreads - leftTC;

        if (node.getLeft().isPresent()) {
            leftThread = new SumThread(node.getLeft().get(), leftTC);
            leftThread.start();
        }
        if (node.getRight().isPresent()) {
            rightThread = new SumThread(node.getRight().get(), rightTC);
            rightThread.start();
        }
    }

    protected int leftThreadCount() {
        return node.getLeft().isEmpty() ? 0
                : node.getRight().isEmpty() ? remainingThreads
                : remainingThreads / 2;


    }

}
