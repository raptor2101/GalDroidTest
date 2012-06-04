package de.raptor2101.GalDroid.Testing.ComponentTest.Activities.TestImplementations;

import java.util.Date;

import de.raptor2101.GalDroid.WebGallery.Interfaces.GalleryObjectComment;

public class TestGalleryObjectComment implements GalleryObjectComment {

  private final Date mCreateDate;
  private final String mMessage;
  private final String mAuthor;

  public TestGalleryObjectComment(String author, String message) {
    mCreateDate = new Date(System.currentTimeMillis());
    mMessage = message;
    mAuthor = author;
  }

  @Override
  public Date getCreateDate() {
    return mCreateDate;
  }

  @Override
  public Date getUpdateDate() {
    return mCreateDate;
  }

  @Override
  public String getMessage() {
    return mMessage;
  }

  @Override
  public String getAuthorEmail() {
    return mAuthor;
  }

  @Override
  public String getAuthorName() {
    return "";
  }

  @Override
  public String getAuthorUrl() {
    return "";
  }

}
