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
        if (remainingThreads < 2) sum = Optional.of(node.sum());
        else {
            startChildThreads();
            try {
                if (leftThread != null && leftThread.isAlive()) leftThread.join();
                if (rightThread != null && rightThread.isAlive()) rightThread.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }

            sum = Optional.of(node.getValue() +
                    (leftThread == null ? node.getLeft().map(Node::sum).orElse(0) : leftThread.sum.get())
                    + (rightThread == null ? node.getRight().map(Node::sum).orElse(0) : rightThread.sum.get()));
        }
        // TODO Exercise 5
    }

    protected void startChildThreads() {
        var leftTC = leftThreadCount();
        var rightTC = node.getRight().isEmpty() ? 0 : (remainingThreads - 1) - leftTC;

        if (node.getLeft().isPresent() && leftTC > 0) {
            leftThread = new SumThread(node.getLeft().get(), leftTC);
            leftThread.start();
        }
        if (node.getRight().isPresent() && rightTC > 0) {
            rightThread = new SumThread(node.getRight().get(), rightTC);
            rightThread.start();
        }
    }

    protected int leftThreadCount() {
        return node.getLeft().isEmpty() ? 0
                : node.getRight().isEmpty() ? (remainingThreads - 1)
                : (remainingThreads - 1) / 2;


    }

}
