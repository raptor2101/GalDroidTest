package de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import junit.framework.Assert;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.json.JSONException;

import de.raptor2101.GalDroid.WebGallery.GalleryStream;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryDownloadObject;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObject;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObjectComment;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryProgressListener;
import de.raptor2101.GalDroid.WebGallery.Interfaces.WebGallery;

public class TestWebGallery implements WebGallery {
	private final int PERMITS = 0; 
	private List<TestGalleryObject> mTestGalleryObject;

	private final Semaphore mSemaphore_GetDisplayObjects;
	
	public TestWebGallery() {
		mSemaphore_GetDisplayObjects = new Semaphore(PERMITS, true);
	}
	
	public void waitForGetDisplayObjectsCall() throws InterruptedException {
		mSemaphore_GetDisplayObjects.acquire();
	}
	
	@Override
	public GalleryObject getDisplayObject(String path)
			throws ClientProtocolException, IOException, JSONException {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObject> getDisplayObjects() {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObject> getDisplayObjects(
			GalleryProgressListener progressListener) {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObject> getDisplayObjects(String path) {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObject> getDisplayObjects(String path,
			GalleryProgressListener progressListener) {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObject> getDisplayObjects(GalleryObject galleryObject) {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObject> getDisplayObjects(GalleryObject galleryObject,
			GalleryProgressListener progressListener) {
		if(galleryObject instanceof TestGalleryObject) {
			TestGalleryObject testGalleryObject = (TestGalleryObject) galleryObject;
			
			Assert.assertTrue("The requestes GalleryObject has no childen",testGalleryObject.hasChildren());
			
			List<TestGalleryObject> children = testGalleryObject.getChildren();
			if(progressListener != null){
				
				progressListener.handleProgress(children.size(), children.size());
			}
			List<GalleryObject> castedChildren = new ArrayList<GalleryObject>(children.size());
			for(TestGalleryObject child:children) {
				castedChildren.add(child);
			}
			mSemaphore_GetDisplayObjects.release();
			return castedChildren;
			
		} else {
			Assert.fail("Some tries to use non-testing GalleryObjects on testing WebGallery");
			return new ArrayList<GalleryObject>(0);
		}
	}

	@Override
	public List<String> getDisplayObjectTags(GalleryObject galleryObject,
			GalleryProgressListener progressListener) throws IOException {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public List<GalleryObjectComment> getDisplayObjectComments(
			GalleryObject galleryObject,
			GalleryProgressListener progressListener) throws IOException,
			ClientProtocolException, JSONException {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public void setPreferedDimensions(int height, int width) {
		// TODO currently not tracked for testing
	}

	@Override
	public String getSecurityToken(String user, String password)
			throws SecurityException {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public GalleryStream getFileStream(GalleryDownloadObject downloadObject)
			throws IOException, ClientProtocolException {
		Assert.fail("Call not implemented TestMethod");
		return null;
	}

	@Override
	public void setSecurityToken(String token) {
		Assert.fail("Call not implemented TestMethod");
	}

	@Override
	public void setHttpClient(HttpClient httpClient) {
		Assert.fail("Call not implemented TestMethod");
	}

	public void setTestGalleryObjects(List<TestGalleryObject> testGalleryObjects) {
		mTestGalleryObject = testGalleryObjects;
	}
	
	public List<TestGalleryObject> getTestGalleryObjects() {
		return mTestGalleryObject;
	}

}
