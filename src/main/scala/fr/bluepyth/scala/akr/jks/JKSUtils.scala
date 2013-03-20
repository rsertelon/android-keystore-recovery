/**
 * Android Keystore Recovery
 * Copyright (C) 2013 Romain Sertelon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This work is based upon Casey Marshall's reverse engineering of JKS <rsdio@metastatic.org>
 * You can find the original work at http://metastatic.org/source/JKS.html
 */
package fr.bluepyth.scala.akr.jks

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory

import javax.crypto.EncryptedPrivateKeyInfo

class JKSUtils(in: InputStream, passwd: Array[Char]) {
  private val MAGIC = 0xFEEDFEED
  var encoded = Array[Byte]()

  private val PRIVATE_KEY = 1
  private val TRUSTED_CERT = 2

  private val md = MessageDigest.getInstance("SHA")
  md.update(charsToBytes(passwd))
  md.update("Mighty Aphrodite".getBytes("UTF-8"))

  val din = new DataInputStream(new DigestInputStream(in, md))

  if (din.readInt != MAGIC) {
    throw new IOException("not a JavaKeyStore")
  }

  din.readInt // version no.

  val n = din.readInt

  if (n < 0) {
    throw new IOException("negative entry count")
  }

  val certType = din.readInt
  din.readUTF
  din.readLong // Skip Date

  certType match {
    case PRIVATE_KEY =>
      val len = din.readInt
      encoded = new Array[Byte](len)
      din.read(encoded)

      val count = din.readInt

      for {
        j <- 0 until count
      } yield {
        readCert(din)
      }

    case TRUSTED_CERT =>
    case _ =>
      throw new IOException("malformed key store")
  }

  val encr = new EncryptedPrivateKeyInfo(encoded).getEncryptedData()

  val check = new Array[Byte](20)
  Array.copy(encr, encr.length - 20, check, 0, 20)


  val hash = new Array[Byte](20)
  din.read(hash)

  if (MessageDigest.isEqual(hash, md.digest())) {
    throw new IOException("signature not verified")
  }
  
  def keyIsRight(password: Array[Char]): Boolean = {
    try {
      decryptKey(charsToBytes(password))
    } catch {
      case e: Exception => false
    }
  }

  private def charsToBytes(passwd: Array[Char]): Array[Byte] = {
    val buf = new Array[Byte](passwd.length * 2)

    var i = 0
    var j = 0
    while (i < passwd.length) {
      buf(j) = (passwd(i) >>> 8).toByte; j += 1
      buf(j) = passwd(i).toByte; j += 1
      i += 1
    }

    buf
  }

  private def decryptKey(passwd: Array[Byte]): Boolean = {
    try {
      val key = new Array[Byte](encr.length - 40)
      val keystream = new Array[Byte](20)
      Array.copy(encr, 0, keystream, 0, 20)

      val sha = MessageDigest.getInstance("SHA1")
      var count = 0;

      while (count < key.length) {
        sha.reset
        sha.update(passwd)
        sha.update(keystream)
        sha.digest(keystream, 0, keystream.length)

        var i = 0
        while (i < keystream.length && count < key.length) {
          key(count) = (keystream(i) ^ encr(count + 20)).toByte
          count += 1
          i += 1
        }
      }

      sha.reset
      sha.update(passwd)
      sha.update(key)

      MessageDigest.isEqual(check, sha.digest)
    } catch {
      case e: Exception => println("exception " +e); false
    }
  }

  private def readCert(in: DataInputStream): Certificate = {
    val certType = in.readUTF
    val len = in.readInt
    val encoded = new Array[Byte](len)
    in.read(encoded)

    val factory = CertificateFactory.getInstance(certType)

    factory.generateCertificate(new ByteArrayInputStream(encoded))
  }
}
