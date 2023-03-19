import groovy.io.FileType
import java.io.File
import java.io.InputStream
import java.util.jar.JarFile
import java.util.TreeSet
import java.util.zip.ZipEntry
import org.apache.maven.artifact.versioning.ComparableVersion

def fileToVersion = [:]
def versionToFile = [:]
def versions = new TreeSet<ComparableVersion>()
def versionsDir = new File("${project['build']['directory']}/versions")
versionsDir.eachFile(FileType.FILES) { file ->
  def matcher = file.name =~ /^job-dsl-core-(.*)\.jar$/
  if (matcher.matches()) {
    def version = new ComparableVersion(matcher.group(1))
    fileToVersion[file] = version
    versionToFile[version] = file
    versions << version
  }
}

def jars = versions.toList().reverse().collect({ versionToFile[it] })
jars.eachWithIndex { File jar, int index ->
  JarFile jarFile = new JarFile(jar)
  try {
    ZipEntry entry = jarFile.getEntry('javaposse/jobdsl/dsl/dsl.json')
    if (entry) {
      InputStream is = jarFile.getInputStream(entry)
      try {
        def filename = index == 0 ? 'dsl.json' : "job-dsl-core-${fileToVersion[jar]}-apidoc.json"
        def dest = new File("${project['build']['directory']}/versions/${filename}")
        log.info("Writing ${dest}")
        dest.delete()
        dest.append(is)
      } finally {
        is.close()
      }
    }
  } finally {
    jarFile.close()
  }
}
