package de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations;

import android.graphics.drawable.Drawable;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryDownloadObject;

public class TestDownloadObject implements GalleryDownloadObject {
	public enum ImageSize {
		Image, Thumbnail
	};

	private ImageSize mRequestedSize;
	private String mObjectId;
	private Drawable mDrawable;

	public TestDownloadObject(String objectId, Drawable drawable,
			ImageSize requestedSize) {
		mRequestedSize = requestedSize;
		mObjectId = objectId;
		mDrawable = drawable;
	}

	@Override
	public String getUniqueId() {
		return String.format("%s-%s", mObjectId, mRequestedSize);
	}

}
