package fr.bluepyth.scala.akr

import java.io.InputStream
import java.security.MessageDigest
import java.io.DataInputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.cert.Certificate
import javax.crypto.EncryptedPrivateKeyInfo
import java.security.cert.CertificateFactory
import java.io.ByteArrayInputStream

object JKSUtils {
  def apply(in: InputStream, passwd: Array[Char]) = {
    new JKSUtils(in, passwd)
  }
}
class JKSUtils(in: InputStream, passwd: Array[Char]) {
  
  private val MAGIC = 0xFEEDFEED
  private val PRIVATE_KEY = 1
  private val TRUSTED_CERT = 2

  val md = MessageDigest.getInstance("SHA")
  md.update(listOfCharToArrayOfBytes(passwd))
  md.update("Mighty Aphrodite".getBytes("UTF-8")) // HAR HAR

  val din = new DataInputStream(new DigestInputStream(in, md))
  if (din.readInt != MAGIC) {
    throw new IOException("not a JavaKeyStore")
  }
  din.readInt // version no.
  val n = din.readInt // Number of entries
  if (n < 0) {
    throw new IOException("negative entry count")
  }

  var encoded = Array[Byte]()
  val digestType = din.readInt
  val alias = din.readUTF // Skip Alias
  din.readLong //Skip Date
  digestType match {
    case PRIVATE_KEY =>
      val len = din.readInt
      encoded = new Array[Byte](len)
      din.read(encoded)

      val count = din.readInt();

      val chain = new Array[Certificate](count)
      for (j <- 0 until count)
        chain(j) = readCert(din)
    case TRUSTED_CERT =>
    case x =>
      throw new IOException("malformed key store")
  }

  val encr = new EncryptedPrivateKeyInfo(encoded).getEncryptedData
  val keystream = new Array[Byte](20)
  System.arraycopy(encr, 0, keystream, 0, 20)
  val check = new Array[Byte](20)
  System.arraycopy(encr, encr.length - 20, check, 0, 20)
  val key = new Array[Byte](encr.length - 40)
  val sha = MessageDigest.getInstance("SHA1")

  val hash = new Array[Byte](20)
  din.read(hash)

  if (MessageDigest.isEqual(hash, md.digest())) {
    throw new IOException("signature not verified")
  }

  def keyIsRight(password: Array[Char]): Boolean = {
    try {
      decryptKey(listOfCharToArrayOfBytes(password));
    } catch {
      case x => false
    }
  }

  private def listOfCharToArrayOfBytes(passwd: Array[Char]): Array[Byte] = {

    val buf = new Array[Byte](passwd.size * 2)
    for (i <- 0 until passwd.size by 2) {
      buf(i) = (passwd(i) >>> 8).toByte
      buf(i + 1) = passwd(i).toByte
    }
    buf

  }

  private def decryptKey(passwd: Array[Byte]): Boolean = {
    try {
      System.arraycopy(encr, 0, keystream, 0, 20)
      var count = 0

      while (count < key.length) {
        sha.reset
        sha.update(passwd)
        sha.update(keystream)
        sha.digest(keystream, 0, keystream.length)

        for(i <- 0 until keystream.length) {
          if(count < key.length) {
            key(count) = (keystream(i) ^ encr(count + 20)).toByte
            count = count + 1
          }
        }
      }

      sha.reset
      sha.update(passwd)
      sha.update(key)

      MessageDigest.isEqual(check, sha.digest)
    } catch {
      case x: Exception => println(x); false
    }
  }

  private def readCert(in: DataInputStream): Certificate = {
    val inType = in.readUTF
    val len = in.readInt
    val encoded = new Array[Byte](len)
    in.read(encoded)

    val factory = CertificateFactory.getInstance(inType)

    factory.generateCertificate(new ByteArrayInputStream(encoded))
  }
}