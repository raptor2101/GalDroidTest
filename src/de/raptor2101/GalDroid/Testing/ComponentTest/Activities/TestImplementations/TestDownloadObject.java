package de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations;

import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryDownloadObject;

public class TestDownloadObject implements GalleryDownloadObject {
	public enum ImageSize {
		Image, Thumbnail
	};

	private ImageSize mRequestedSize;
	private String mObjectId;
	private int mImageRecourceID;

	public TestDownloadObject(String objectId, int imageRecourceID,
			ImageSize requestedSize) {
		mRequestedSize = requestedSize;
		mObjectId = objectId;
		mImageRecourceID = imageRecourceID;
	}

	@Override
	public String getUniqueId() {
		return String.format("%s-%s", mObjectId, mRequestedSize);
	}
	
	public int getResourceId() {
		return mImageRecourceID;
	}

	public ImageSize getRequestedSize() {
		return mRequestedSize;
	}

}
