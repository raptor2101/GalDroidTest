package de.raptor2101.GalDroid.Testing.ComponentTest.Activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


import de.raptor2101.GalDroid.R;
import de.raptor2101.GalDroid.Activities.GalDroidApp;
import de.raptor2101.GalDroid.Activities.ImageViewActivity;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestGalleryObject;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestWebGallery;
import de.raptor2101.GalDroid.WebGallery.GalleryImageView;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObject;
import de.raptor2101.GalDroid.WebGallery.Tasks.GalleryLoaderTask;
import de.raptor2101.GalDroid.WebGallery.Tasks.ImageLoaderTask;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Gallery;

public class ImageViewActivityTest extends
ActivityInstrumentationTestCase2<ImageViewActivity> {
	
	public ImageViewActivityTest() {
		super("de.raptor2101.GalDroid.Activities.ImageViewActivity",ImageViewActivity.class);
	}

	private final int IMAGE_ID = de.raptor2101.GalDroid.Testing.R.drawable.testpic_1;
	private final int GALLERY_SAMPLE_SIZE = 300;

	private ImageViewActivity mActivity;
	private TestWebGallery mWebGallery;

	private Instrumentation mInstrumentation;

	private TestGalleryObject mCurrentGallery;
	private TestGalleryObject mCurrentVisibleChild;
	
	private Gallery mGalleryFullscreen;
	private Gallery mGalleryThumbnails;
	private View	mImageInformationView;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mInstrumentation = getInstrumentation();
		
		Resources recources = getInstrumentation().getContext().getResources();
		mWebGallery = createTestWebGallery(recources);
		
		
		Intent intent = new Intent();
		intent.putExtra(GalDroidApp.INTENT_EXTRA_DISPLAY_GALLERY,
				mCurrentGallery);
		intent.putExtra(GalDroidApp.INTENT_EXTRA_DISPLAY_OBJECT,
				mCurrentVisibleChild);
		intent.putExtra(GalDroidApp.INTENT_EXTRA_SHOW_IMAGE_INFO, false);
		setActivityIntent(intent);
		
		

		GalDroidApp appContext = (GalDroidApp)this.getInstrumentation().getTargetContext().getApplicationContext();
		appContext.setWebGallery(mWebGallery);
		mActivity = getActivity();
		
		mGalleryFullscreen = (Gallery) mActivity.findViewById(R.id.singleImageGallery);
    	mGalleryThumbnails = (Gallery) mActivity.findViewById(R.id.thumbnailImageGallery);
    	mImageInformationView = (View) mActivity.findViewById(R.id.viewImageInformations);
		

	}

	public void testActivityStart() {
		checkStartUp(View.VISIBLE, View.GONE, View.GONE);
	}

	public void testScrollTrough() {
		checkStartUp(View.VISIBLE, View.GONE, View.GONE);
		
		List<TestGalleryObject> children = mCurrentGallery.getChildren();
		
		// fastForward ... to the last image...
		PointF dragFrom = new PointF(1000, 358);
		PointF dragTo = new PointF(300, 358);
		
		simulateFlingGallery(mGalleryFullscreen, dragFrom, dragTo, 300);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		checkSelectedView(children.get(1));
	}
	
	private void simulateFlingGallery(Gallery gallery, PointF from, PointF to, int time)  {
		long startTime = SystemClock.uptimeMillis()-time;
		MotionEvent startEvent = MotionEvent.obtain(startTime, startTime, MotionEvent.ACTION_DOWN, from.x, from.y, 0);
		MotionEvent stopEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, to.x, to.y, 0);
		float seconds = time/1000f;
		float velocityX = (to.x-from.x)/seconds;
		float velocityY= (to.y-from.y)/seconds;
		gallery.onFling(startEvent, stopEvent, velocityX, velocityY);
	}
	
	private void checkStartUp(int visibilityFullscreenGallery, int visibilityThumbnailGallery, int ImageInformationPanel) {
		assertEquals("The FullscreenGallery has wrong Visibility", visibilityFullscreenGallery, mGalleryFullscreen.getVisibility());
		assertEquals("The ThumbnailGallery has wrong Visibility", visibilityThumbnailGallery, mGalleryThumbnails.getVisibility());
		assertEquals("The ImageInformationPanel has wrong Visibility", ImageInformationPanel, mImageInformationView.getVisibility());
		
		if( !mActivity.areGalleryObjectsAvailable()) {
			GalleryLoaderTask task = mActivity.getDownloadTask();
			assertNotNull("No DownloadTask created although no gallery object could be loaded from the cache",task);
			
			try {
				task.get();
				// give the UIThread time to handle the Finish event...
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			} catch (ExecutionException e) {
				fail(e.getMessage());
			}
		}
		
		GalleryImageView selectedView = checkSelectedView(mCurrentVisibleChild);
				
		checkImageLoaderTask(selectedView);
	}
	
	private GalleryImageView checkSelectedView(GalleryObject galleryObject) {
		GalleryImageView selectedView = (GalleryImageView) mGalleryFullscreen.getSelectedView();

		assertNotNull("No view selected", selectedView);
		assertEquals("The Gallery don't shows the inteted image", galleryObject, selectedView.getGalleryObject());
		return selectedView;
	}
	
	private void checkImageLoaderTask(GalleryImageView galleryImageView) {
		if (!galleryImageView.isLoaded()) {
			ImageLoaderTask imageLoaderTask = galleryImageView
					.getImageLoaderTask();
			assertNotNull(
					String.format(
							"There is no imageLoaderTask initialized for the GalleryImageView %s",
							galleryImageView.getGalleryObject().getObjectId()),
					imageLoaderTask);
			try {
				imageLoaderTask.get();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			} catch (ExecutionException e) {
				fail(e.getMessage());
			}
			assertEquals(
					String.format(
							"The GalleryImageView %s is not loaded but the imageLoaderTask ist finished",
							galleryImageView.getGalleryObject().getObjectId()),
					true, galleryImageView.isLoaded());
		}
	}
	private TestWebGallery createTestWebGallery(Resources resources) {
		TestWebGallery webGallery = new TestWebGallery(resources);
	
		List<TestGalleryObject> children = new ArrayList<TestGalleryObject>(
				GALLERY_SAMPLE_SIZE);
	
		for (int i = 0; i < GALLERY_SAMPLE_SIZE; i++) {
			String objectId = String.format("TestGalObject %s", i);
			Date currentDate = new Date(System.currentTimeMillis());
	
			children.add(new TestGalleryObject(objectId, objectId,
					currentDate, IMAGE_ID, null));
		}
		String objectId = "ParentObject";
		Date currentDate = new Date(System.currentTimeMillis());
		List<TestGalleryObject> parents = new ArrayList<TestGalleryObject>(1);
		parents.add(new TestGalleryObject(objectId, objectId,currentDate, IMAGE_ID, children));
	
		webGallery.setTestGalleryObjects(parents);
		
		mCurrentGallery = parents.get(0);
		mCurrentVisibleChild = children.get(0);
		return webGallery;
	}
}
