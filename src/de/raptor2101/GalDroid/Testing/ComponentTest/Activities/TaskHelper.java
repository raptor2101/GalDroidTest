package de.raptor2101.GalDroid.Testing.ComponentTest.Activities;

import junit.framework.Assert;

public abstract class TaskHelper {
  public static final int MAX_WAIT_TIME = 10000;

  public void waitForExecution(String assertMessage) throws InterruptedException {
    long startTime = System.currentTimeMillis();
    long timeElapsed = 0;
    do {
      if (checkCondition(timeElapsed)) {
        return;
      }

      Thread.sleep(100);
      timeElapsed = System.currentTimeMillis() - startTime;
    } while (timeElapsed < MAX_WAIT_TIME);

    Assert.fail(assertMessage);
  }

  protected abstract boolean checkCondition(long timeElapsed);
}
