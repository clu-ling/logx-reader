package org.parsertongue.mr

import java.io.File

object TestUtils {

  def fileFromResources(path: String): File = {
    val url = getClass.getClassLoader.getResource(path)
    new File(url.toURI)
  }
}