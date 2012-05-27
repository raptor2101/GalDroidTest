package de.raptor2101.GalDroid.Testing.ComponentTest.Activities;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


import de.raptor2101.GalDroid.R;
import de.raptor2101.GalDroid.Activities.GalDroidApp;
import de.raptor2101.GalDroid.Activities.ImageViewActivity;
import de.raptor2101.GalDroid.Activities.Views.ImageInformationView;
import de.raptor2101.GalDroid.Activities.Views.ImageInformationView.ExtractInformationTask;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestDownloadObject;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestGalleryObject;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestGalleryObjectComment;
import de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations.TestWebGallery;
import de.raptor2101.GalDroid.WebGallery.GalleryCache;
import de.raptor2101.GalDroid.WebGallery.GalleryImageAdapter;
import de.raptor2101.GalDroid.WebGallery.GalleryImageView;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryDownloadObject;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObject;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObjectComment;
import de.raptor2101.GalDroid.WebGallery.Tasks.CommentLoaderTask;
import de.raptor2101.GalDroid.WebGallery.Tasks.GalleryLoaderTask;
import de.raptor2101.GalDroid.WebGallery.Tasks.ImageLoaderTask;
import de.raptor2101.GalDroid.WebGallery.Tasks.TagLoaderTask;

import android.app.ActionBar;
import android.app.ActionBar.OnMenuVisibilityListener;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Gallery;
import android.widget.TextView;

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
	
	private ImageInformationView	mImageInformationView;
	
	private Gallery mGalleryFullscreen;
	private Gallery mGalleryThumbnails;
	

	@Override
	protected void setUp() throws Exception {
		Log.d("ImageViewActivityTest", "Setup Called");
		super.setUp();
		/*if(mActivity != null) {
			mActivity.finish();
		}*/
		mInstrumentation = getInstrumentation();
		
		GalleryCache galleryCache = new GalleryCache(this.getInstrumentation().getTargetContext());
		for (File file : galleryCache.getCacheDir().listFiles()) {
			try {
				file.delete();
			} catch (Exception e) {
				// if something goes wrong... ignore it
			}
		}
		
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
		galleryCache = appContext.getGalleryCache();
		
		if (galleryCache != null) {
			
			galleryCache.clearCachedBitmaps();
		}
		mActivity = getActivity();
		
		mGalleryFullscreen = (Gallery) mActivity.findViewById(R.id.singleImageGallery);
    	mGalleryThumbnails = (Gallery) mActivity.findViewById(R.id.thumbnailImageGallery);
    	mImageInformationView = (ImageInformationView) mActivity.findViewById(R.id.viewImageInformations);
		

	}

	public void testActivityStart() throws Exception {
		checkStartUp(View.VISIBLE, View.GONE, View.GONE);
		
	}

	public void testScrollTrough() throws Exception{
		checkStartUp(View.VISIBLE, View.GONE, View.GONE);
		
		List<TestGalleryObject> children = mCurrentGallery.getChildren();
		
		// fastForward ... to the last image...
		PointF dragFrom = new PointF(1000, 358);
		PointF dragTo = new PointF(300, 358);
		
		for(int pos=1; pos<5; pos++) {
			int prePos = mGalleryFullscreen.getSelectedItemPosition();
			simulateFlingGallery(mGalleryFullscreen, dragFrom, dragTo, 300);
			
			//waiting till the Gallery received the "switch" event
			long currentTime = System.currentTimeMillis();
			while(mGalleryFullscreen.getSelectedItemPosition() == prePos) {
				Thread.sleep(100);
				long diffTime = System.currentTimeMillis() - currentTime;
				assertTrue("Switching of an image takes to long", 2000 > diffTime);
			}
			
			GalleryImageView imageView = (GalleryImageView) mGalleryFullscreen.getSelectedView();
			checkSelectedView(children.get(pos));
			checkImageLoaderTask(imageView);
			
			
		}
	}
	
	public void testActivityStartWithInformations() throws Exception {
		checkStartUp(View.VISIBLE, View.GONE, View.GONE);
		GalleryImageView imageView = (GalleryImageView) mGalleryFullscreen.getSelectedView();
		TestGalleryObject galleryObject = (TestGalleryObject) imageView.getGalleryObject();
		
		List<GalleryObjectComment> comments = new ArrayList<GalleryObjectComment>(5);
		comments.add(new TestGalleryObjectComment("Some Author","First Comment"));
		comments.add(new TestGalleryObjectComment("Some other Author","Second Comment"));
		comments.add(new TestGalleryObjectComment("Some Author","give some more comment"));
		comments.add(new TestGalleryObjectComment("administraor","shut the fuck up"));
		comments.add(new TestGalleryObjectComment("Test","Test"));
		
		mWebGallery.setupGetDisplayObjectCommentsCall(galleryObject, comments);
		
		List<String> tags = new ArrayList<String>(5);
		tags.add("Some");
		tags.add("realy");
		tags.add("incredible");
		tags.add("genius");
		tags.add("tags");
		
		mWebGallery.setupGetDisplayObjectTagsCall(galleryObject, tags);
		
		TouchUtils.clickView(this, mGalleryFullscreen);
		ActionBar actionBar = mActivity.getActionBar();
		assertEquals("The actionbar don't appear", true, actionBar.isShowing());
		View imageButton = mActivity.findViewById(R.id.item_additional_info_object);
		TouchUtils.clickView(this, imageButton);
		assertEquals("The ImageInformationPanel has wrong Visibility", View.VISIBLE, mImageInformationView.getVisibility());
		
		ExtractInformationTask task = mImageInformationView.getExtractionInfromationTask();
		
		if(task != null) {
			task.get();
			Thread.sleep(500);
		}
		
		
		
		
		
		checkImageInformationIsLoaded(galleryObject);
		checkTagAreLoaded(tags);
		checkCommentsAreLoaded(comments);
	}
	
	private void checkCommentsAreLoaded(List<GalleryObjectComment> comments) throws Exception {
		CommentLoaderTask commentLoaderTask = mImageInformationView.getCommentLoaderTask();
		if(commentLoaderTask != null) {
			commentLoaderTask.get();
			Thread.sleep(500);
		}
		ViewGroup rootView = (ViewGroup)mImageInformationView.findViewById(R.id.layoutComments);
		assertEquals("ImageInformation - Commentscount isn't correct.", comments.size(), rootView.getChildCount());
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.getDefault());
		for(int count = 0; count < rootView.getChildCount(); count++) {
			View commentView = rootView.getChildAt(count);
			GalleryObjectComment comment = comments.get(count);
			
			TextView textAuthor = (TextView) commentView.findViewById(R.id.textCommentAuthor);
			TextView textDate = (TextView) commentView.findViewById(R.id.textCommentPosted);
			TextView textMessage = (TextView) commentView.findViewById(R.id.textCommentMessage);
			
			assertEquals("ImageInformation - Comment-Author is set wrong", comment.getAuthorName(), textAuthor.getText().toString());
			assertEquals("ImageInformation - Comment-Date is set wrong", dateFormat.format(comment.getCreateDate()), textDate.getText().toString());
			assertEquals("ImageInformation - Comment-Message is set wrong", comment.getMessage(), textMessage.getText().toString());
		}
	}

	private void checkImageInformationIsLoaded(TestGalleryObject galleryObject) throws Exception {
		checkEmbededInformationIsLoaded(galleryObject);
		checkExifInformationIsLoaded();
	}
	
	private void checkTagAreLoaded(List<String> tags) throws Exception {
		TagLoaderTask tagLoaderTask = mImageInformationView.getTagLoaderTask();
		if(tagLoaderTask != null) {
			tagLoaderTask.get();
			Thread.sleep(500);
		}
		
		StringBuilder stringBuilder = new StringBuilder(tags.size()*10);
		for(String tag:tags) {
			stringBuilder.append(String.format("%s, ", tag));
		}
		int length = stringBuilder.length() ;
		if(length > 0) {
			stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length());
		}
		
		TextView textView = (TextView) mImageInformationView.findViewById(R.id.textTags);
		assertEquals("ImageInformation - Tags are set wrong", stringBuilder.toString(), textView.getText().toString());
	}

	private void checkEmbededInformationIsLoaded(TestGalleryObject galleryObject) {
		TextView textView = (TextView) mImageInformationView.findViewById(R.id.textTitle);
		assertEquals("ImageInformation - Title is set wrong", galleryObject.getTitle(), textView.getText().toString());
		
		textView = (TextView) mImageInformationView.findViewById(R.id.textUploadDate);
		assertEquals("ImageInformation - UploadDate is set wrong", galleryObject.getDateUploaded().toLocaleString(), textView.getText().toString());
	}
	
	private void checkExifInformationIsLoaded() {
		TextView textField;
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifCreateDate);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifAperture);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifExposure);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifFlash);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifISO);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifModel);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifModel);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifMake);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifFocalLength);
		textField = (TextView) mImageInformationView.findViewById(R.id.textExifWhiteBalance);
		textField = (TextView) mImageInformationView.findViewById(R.id.textGeoLat);
		textField = (TextView) mImageInformationView.findViewById(R.id.textGeoLong);
		textField = (TextView) mImageInformationView.findViewById(R.id.textGeoHeight);
	}
	
	private void checkStartUp(int visibilityFullscreenGallery, int visibilityThumbnailGallery, int ImageInformationPanel) throws Exception {
		assertEquals("The FullscreenGallery has wrong Visibility", visibilityFullscreenGallery, mGalleryFullscreen.getVisibility());
		assertEquals("The ThumbnailGallery has wrong Visibility", visibilityThumbnailGallery, mGalleryThumbnails.getVisibility());
		assertEquals("The ImageInformationPanel has wrong Visibility", ImageInformationPanel, mImageInformationView.getVisibility());
		
		if( !mActivity.areGalleryObjectsAvailable()) {
			GalleryLoaderTask task = mActivity.getDownloadTask();
			assertNotNull("No DownloadTask created although no gallery object could be loaded from the cache",task);
			
			task.get();
		}
		
		// Whait till the adapter gets loaded
		GalleryImageAdapter adapter = (GalleryImageAdapter) mGalleryFullscreen.getAdapter();
		
		long currentTime = System.currentTimeMillis();
		while(!adapter.isLoaded()) {
			Thread.sleep(100);
			long diffTime = System.currentTimeMillis() - currentTime;
			Log.d("ImageViewActivityTest",String.format("Test %d", diffTime));
			assertTrue("Loading of the GalleryImageAdapter takes to long", 10000 > diffTime);
		}
		
		Thread.sleep(1000);
		
		GalleryImageView selectedView = checkSelectedView(mCurrentVisibleChild);
				
		checkImageLoaderTask(selectedView);
		checkPreLoading(selectedView,2);
	}
	
	private GalleryImageView checkSelectedView(GalleryObject galleryObject) {
		GalleryImageView selectedView = (GalleryImageView) mGalleryFullscreen.getSelectedView();

		assertNotNull("No view selected", selectedView);
		assertEquals("The Gallery don't shows the inteted image", galleryObject, selectedView.getGalleryObject());
		return selectedView;
	}
	
	private void checkPreLoading(GalleryImageView currentView, int minimalLookAhead) {
		GalleryObject currentObject =  currentView.getGalleryObject();
		GalleryDownloadObject downloadObject = currentObject.getImage();
		List<TestDownloadObject> requestedObject = mWebGallery.getRequestedDownloadObjects();
		
		int currentIndex = requestedObject.indexOf(downloadObject);
		assertTrue("Current GalleryObject was never requested", currentIndex > -1);
		assertTrue("No preloading requestes", currentIndex>=requestedObject.size()-1);
		assertTrue("Not enough preloading requestes", currentIndex+minimalLookAhead>=requestedObject.size()-1);
	}
	
	private void checkImageLoaderTask(GalleryImageView galleryImageView) throws Exception {
		if (!galleryImageView.isLoaded()) {
			ImageLoaderTask imageLoaderTask = galleryImageView
					.getImageLoaderTask();
			assertNotNull(
					String.format(
							"There is no imageLoaderTask initialized for the GalleryImageView %s",
							galleryImageView.getGalleryObject().getObjectId()),
					imageLoaderTask);
			imageLoaderTask.get();
			//Thread.sleep(1000);
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

	private void simulateDragGallery(PointF from, PointF to)  {
		long startTime = SystemClock.uptimeMillis()-1000;
		MotionEvent startEvent = MotionEvent.obtain(startTime, startTime, MotionEvent.ACTION_DOWN, from.x, from.y, 0);
		MotionEvent stopEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, to.x, to.y, 0);
		mInstrumentation.sendPointerSync(startEvent);
		mInstrumentation.sendPointerSync(stopEvent);
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
}
