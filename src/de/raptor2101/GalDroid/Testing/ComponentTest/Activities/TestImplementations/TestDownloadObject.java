package de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations;

import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryDownloadObject;

public class TestDownloadObject implements GalleryDownloadObject {
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((mObjectId == null) ? 0 : mObjectId.hashCode());
	result = prime * result + ((mRequestedSize == null) ? 0 : mRequestedSize.hashCode());
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
	TestDownloadObject other = (TestDownloadObject) obj;
	if (mObjectId == null) {
	    if (other.mObjectId != null)
		return false;
	} else if (!mObjectId.equals(other.mObjectId))
	    return false;
	if (mRequestedSize != other.mRequestedSize)
	    return false;
	return true;
    }

    public enum ImageSize {
	Image, Thumbnail
    };

    private ImageSize mRequestedSize;
    private String mObjectId;
    private int mImageRecourceID;

    public TestDownloadObject(String objectId, int imageRecourceID, ImageSize requestedSize) {
	mRequestedSize = requestedSize;
	mObjectId = objectId;
	mImageRecourceID = imageRecourceID;
    }

    @Override
    public String getUniqueId() {
	return String.format("%s-%s", mObjectId, mRequestedSize);
    }

    @Override
    public String toString() {
	return String.format("%s-%s", mObjectId, mRequestedSize);
    }

    public int getResourceId() {
	return mImageRecourceID;
    }

    public ImageSize getRequestedSize() {
	return mRequestedSize;
    }

}
