package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext;
import javaposse.jobdsl.dsl.JobManagement;

public class ImageGalleryContext extends AbstractContext {

  String imageGalleryTitle
  String imageGalleryPath
  String imageWidthText = ''
  boolean markBuildAsUnstableIfNoArchivesFound = false

  ImageGalleryContext(JobManagement jobManagement, String galleryTitle, String galleryPath) {
    super(jobManagement)
    imageGalleryTitle = galleryTitle
    imageGalleryPath = galleryPath
  }

  void imageWidthText(String imageWidthText) {
    this.imageWidthText = imageWidthText
  }

  void markBuildAsUnstableIfNoArchivesFound(boolean markBuildAsUnstableIfNoArchivesFound) {
    this.markBuildAsUnstableIfNoArchivesFound = markBuildAsUnstableIfNoArchivesFound
  }

}
