package de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations;

import java.util.Date;
import java.util.List;

import android.graphics.drawable.Drawable;

import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryDownloadObject;
import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObject;

public class TestGalleryObject implements GalleryObject {

	private static final long serialVersionUID = -8025907242127852748L;
	private final String mTitle;
	private final Date mDateUploaded;
	private final String mObjectId;
	private final int mImageRecourceID;
	private final List<TestGalleryObject> mChildren;
	
	public TestGalleryObject(String objectId, String title, Date dateUploaded, int imageRecourceID, List<TestGalleryObject> children) {
		mTitle = title;
		mDateUploaded = dateUploaded;
		mObjectId = objectId;
		mChildren = children;
		mImageRecourceID = imageRecourceID;
	}
	
	@Override
	public String getTitle() {
		return mTitle;
	}

	@Override
	public Date getDateUploaded() {
		return mDateUploaded;
	}

	@Override
	public boolean hasChildren() {
		return mChildren != null;
	}

	@Override
	public String getObjectId() {
		return mObjectId;
	}

	@Override
	public GalleryDownloadObject getImage() {
		return new TestDownloadObject(mObjectId, mImageRecourceID, TestDownloadObject.ImageSize.Image);
	}

	@Override
	public GalleryDownloadObject getThumbnail() {
		return new TestDownloadObject(mObjectId, mImageRecourceID, TestDownloadObject.ImageSize.Thumbnail);
	}
	
	@Override
	public String toString() {
		return mObjectId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mObjectId == null) ? 0 : mObjectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestGalleryObject other = (TestGalleryObject) obj;
		if (mObjectId == null) {
			if (other.mObjectId != null)
				return false;
		} else if (!mObjectId.equals(other.mObjectId))
			return false;
		return true;
	}

	public List<TestGalleryObject> getChildren() {
		return mChildren;
	}

}
