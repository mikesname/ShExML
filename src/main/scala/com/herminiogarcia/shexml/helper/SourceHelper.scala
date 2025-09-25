package com.herminiogarcia.shexml.helper



import java.io.{FileInputStream, InputStream}
import java.nio.charset.StandardCharsets
import scala.io.Codec

object LoadedSource {

  import java.security.DigestInputStream

  def apply(fileContent: String, filepath: String): LoadedSource = {
    val baos = new java.io.ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))
    try {
      apply(baos, filepath)
    } finally baos.close()
  }

  def apply(inputStream: InputStream, filepath: String): LoadedSource = {
    val digestType = java.security.MessageDigest.getInstance("MD5")
    val dis = new DigestInputStream(inputStream, digestType)
    val content = scala.io.Source.fromInputStream(dis)(Codec.UTF8).mkString
    val digest = digestType.digest().map("%02x".format(_)).mkString
    new LoadedSource(content, filepath, digest)
  }
}

case class LoadedSource(fileContent: String, filepath: String, digest: String)

/**
  * Created by herminio on 21/2/18.
  */
class SourceHelper {

  def getURLContent(url: String): LoadedSource = LoadedSource(new java.net.URL(url).openStream(), url)

  def getContentFromRelativePath(path: String): LoadedSource = LoadedSource(new FileInputStream(path), path)

  def getStdinContents(): LoadedSource = LoadedSource(System.in, "-")
}
