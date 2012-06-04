package de.raptor2101.GalDroid.Testing.ComponentTest.Stubs.Listener;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

public class GalleryProgressListener implements de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryProgressListener {
  public class HandleProgressCall {
    public final int curValue;
    public final int maxValue;

    public HandleProgressCall(int curValue, int maxValue) {
      this.curValue = curValue;
      this.maxValue = maxValue;
    }
  };

  private ArrayList<HandleProgressCall> calls = new ArrayList<GalleryProgressListener.HandleProgressCall>(10);

  public List<HandleProgressCall> getHandleProgressCalls() {
    return calls;
  }

  public void reset() {
    calls.clear();
  }

  @Override
  public void handleProgress(int curValue, int maxValue) {
    Assert.assertTrue("CurValue greater MaxValue", curValue <= maxValue);
    calls.add(new HandleProgressCall(curValue, maxValue));
  }

}
