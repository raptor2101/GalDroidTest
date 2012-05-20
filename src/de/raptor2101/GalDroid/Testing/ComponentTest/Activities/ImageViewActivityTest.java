package de.raptor2101.GalDroid.Testing.ComponentTest.Activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import de.raptor2101.GalDroid.R;
import de.raptor2101.GalDroid.Activities.GalDroidApp;
import de.raptor2101.GalDroid.Activities.ImageViewActivity;
import de.raptor2101.GalDroid.Testing.ComponentTest.Gallery3ImplementationTest;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestGalleryObject;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestWebGallery;
import de.raptor2101.GalDroid.WebGallery.GalleryImageView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.Gallery;

public class ImageViewActivityTest extends
ActivityInstrumentationTestCase2<ImageViewActivity> {
	
	public ImageViewActivityTest() {
		super("de.raptor2101.GalDroid.Activities.ImageViewActivity",ImageViewActivity.class);
	}

	private final int IMAGE_ID = de.raptor2101.GalDroid.test.R.drawable.testpic_1;
	private final int GALLERY_SAMPLE_SIZE = 300;

	private ImageViewActivity mActivity;
	private TestWebGallery mWebGallery;

	private Gallery mGalleryFullscreen;
	private Gallery mGalleryThumbnails;

	private TestGalleryObject mCurrentGallery;
	private TestGalleryObject mCurrentVisibleChild;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
		
		

	}

	private TestWebGallery createTestWebGallery(Resources resources) {
		TestWebGallery webGallery = new TestWebGallery();

		List<TestGalleryObject> children = new ArrayList<TestGalleryObject>(
				GALLERY_SAMPLE_SIZE);

		for (int i = 0; i < GALLERY_SAMPLE_SIZE; i++) {
			String objectId = String.format("TestGalObject %s", i);
			Date currentDate = new Date(System.currentTimeMillis());

			children.add(new TestGalleryObject(objectId, objectId,
					currentDate, resources.getDrawable(IMAGE_ID), null));
		}
		String objectId = "ParentObject";
		Date currentDate = new Date(System.currentTimeMillis());
		List<TestGalleryObject> parents = new ArrayList<TestGalleryObject>(1);
		parents.add(new TestGalleryObject(objectId, objectId,currentDate, resources.getDrawable(IMAGE_ID), children));

		webGallery.setTestGalleryObjects(parents);
		
		mCurrentGallery = parents.get(0);
		mCurrentVisibleChild = children.get(0);
		return webGallery;
	}

	public void testSimpleRun() {
		assertEquals("The FullscreenGallery isn't visible", View.VISIBLE,
				mGalleryFullscreen.getVisibility());
		assertEquals("The ThumbnailGallery is visible", View.GONE,
				mGalleryThumbnails.getVisibility());

		try {
			mWebGallery.waitForGetDisplayObjectsCall();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		GalleryImageView currentView = (GalleryImageView) mGalleryFullscreen
				.getSelectedView();

		assertNotNull("No current view selected", currentView);
		assertEquals("The Gallery don't shows the inteted image",
				mCurrentVisibleChild, currentView.getGalleryObject());
	}
}
