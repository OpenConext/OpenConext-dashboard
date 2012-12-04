package nl.surfnet.coin.selfservice.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class ConcurrentRunnerContext<T> {

  private final int threads;

  public ConcurrentRunnerContext(int threads) {
    this.threads = threads;
  }

  public List<T> run(final ConcurrentRunner<T> runner) {

    final List<T> results = new CopyOnWriteArrayList<T>();

    final CountDownLatch startGate = new CountDownLatch(threads + 1);
    final CountDownLatch endGate = new CountDownLatch(threads);
    for (int j = 0; j < threads; j++) {
      new Thread(new Runnable() {
        public void run() {
          try {
            startGate.countDown();
            startGate.await();
            results.add(runner.run());
          } catch (InterruptedException e1) {
            System.err.println("Exception occurred: " + e1.getMessage());
          } finally {
            endGate.countDown();
          }
        }
      }).start();
    }
    startGate.countDown();
    try {
      endGate.await();
    } catch (InterruptedException e) {
      System.err.println("Exception occurred: " + e.getMessage());
    }
    return results;
  }
}
