job('example-1') {
  publishers {
    publishImageGallery('some title', './some/path/with/regex/*.png')
  }
}

job('example-2') {
  publishers {
    publishImageGallery('some title', './some/path/with/regex/*.png') {
      imageWidthText '15'
      markBuildAsUnstableIfNoArchivesFound true
    }
  }
}
