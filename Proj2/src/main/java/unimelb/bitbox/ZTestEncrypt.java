package unimelb.bitbox;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;


import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ZTestEncrypt {

	public static void main(String[] args) {
		String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQC0OUEQPCvlOaw61FZ5Y02NEiCC4MdZ2G4mj"
				+ "XAXFqNiJWZR4KaeoYuVxd75QUCqCd5jMvL6AEo9ArxRc+3Yv3oKcWGHKHrNXB7Vt5boq2P"
				+ "gjfWsq5r2V1bzpLMfDp61R1UEJWnfQ38nn04hfIxqlfkPMRWbd5SbN78TSJyzPEqYeQ== rashan@student.unimeb.edu.au";
		
		
		
		//byte[] data = Base64.getDecoder().decode((key.getBytes()));
		//System.out.println(data);
		
		try {
			RSAPublicKey rsakey = CertificateUtils.parseSSHPublicKey(key);
			
			System.out.println(rsakey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		/*
		X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) fact.generatePublic(spec);
		    System.out.println(pubKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte[] toPKCS8Format(final PrivateKey privateKey) throws IOException
	{
		String keyFormat = privateKey.getFormat();
		System.out.println("Key format" + keyFormat);
		
		if (keyFormat.equals("PKCS#8")) {
            return privateKey.getEncoded();
        }
		if (keyFormat.equals("PKCS#1")) {
			final byte[] encoded = privateKey.getEncoded();
			final PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(encoded);
			final ASN1Encodable asn1Encodable = privateKeyInfo.parsePrivateKey();
			final ASN1Primitive asn1Primitive = asn1Encodable.toASN1Primitive();
			final byte[] privateKeyPKCS8Formatted = asn1Primitive.getEncoded(ASN1Encoding.DER);
			return privateKeyPKCS8Formatted;			
        }
		return privateKey.getEncoded();
	}

}
