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
package fr.bluepyth.scala.akr;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.EncryptedPrivateKeyInfo;

public class JKSJavaUtils {
	private static final int MAGIC = 0xFEEDFEED;
	static byte[] encoded;
	static Certificate[] chain;
	static MessageDigest sha;
	static byte[] encr;
	static byte[] check;
	private static final int PRIVATE_KEY = 1;
	private static final int TRUSTED_CERT = 2;
	
	public static void engineLoad(InputStream in, char[] passwd) {
		try{
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(charsToBytes(passwd));
			md.update("Mighty Aphrodite".getBytes("UTF-8")); // HAR HAR
	
			DataInputStream din = new DataInputStream(new DigestInputStream(in, md));
	
			if (din.readInt() != MAGIC) {
				throw new IOException("not a JavaKeyStore");
			}
	
			din.readInt(); // version no.
	
			final int n = din.readInt();
	
			if (n < 0) {
				throw new IOException("negative entry count");
			}
	
			int type = din.readInt();
			din.readUTF();
			din.readLong(); // Skip Date
	
			switch (type) {
			case PRIVATE_KEY:
	
				int len = din.readInt();
				encoded = new byte[len];
				din.read(encoded);
	
				// privateKeys.put(alias, encoded);
				int count = din.readInt();
				chain = new Certificate[count];
	
				for (int j = 0; j < count; j++)
					chain[j] = readCert(din);
	
				// certChains.put(alias, chain);
				break;
	
			case TRUSTED_CERT:
	
				// trustedCerts.put(alias, readCert(din));
				break;
	
			default:
				throw new IOException("malformed key store");
			}
	
			encr = new EncryptedPrivateKeyInfo(encoded).getEncryptedData();
			check = new byte[20];
			System.arraycopy(encr, encr.length - 20, check, 0, 20);
			
			sha = MessageDigest.getInstance("SHA1");
	
			byte[] hash = new byte[20];
			din.read(hash);
	
			if (MessageDigest.isEqual(hash, md.digest())) {
				throw new IOException("signature not verified");
			}
		} catch(IOException | NoSuchAlgorithmException | CertificateException e) {
			System.out.println(e.getMessage());
		}
	}

	public static boolean keyIsRight(char[] password) {
		try {
			return decryptKey(charsToBytes(password));
		} catch (Exception x) {
			return false;
		}
	}

	private static byte[] charsToBytes(char[] passwd) {
		byte[] buf = new byte[passwd.length * 2];

		for (int i = 0, j = 0; i < passwd.length; i++) {
			buf[j++] = (byte) (passwd[i] >>> 8);
			buf[j++] = (byte) passwd[i];
		}

		return buf;
	}

	private static boolean decryptKey(byte[] passwd) {
		try {
			byte[] key = new byte[encr.length - 40];
			byte[] keystream = new byte[20];
			System.arraycopy(encr, 0, keystream, 0, 20);

			int count = 0;

			while (count < key.length) {
				sha.reset();
				sha.update(passwd);
				sha.update(keystream);
				sha.digest(keystream, 0, keystream.length);

				for (int i = 0; (i < keystream.length) && (count < key.length); i++) {
					key[count] = (byte) (keystream[i] ^ encr[count + 20]);
					count++;
				}
			}

			sha.reset();
			sha.update(passwd);
			sha.update(key);

			if (MessageDigest.isEqual(check, sha.digest())) {
				return true;
			}

			return false;
		} catch (Exception x) {
			return false;
		}
	}

	private static Certificate readCert(DataInputStream in) throws IOException, CertificateException, NoSuchAlgorithmException {
		String type = in.readUTF();
		int len = in.readInt();
		byte[] encoded = new byte[len];
		in.read(encoded);

		CertificateFactory factory = CertificateFactory.getInstance(type);

		return factory.generateCertificate(new ByteArrayInputStream(encoded));
	}
}
