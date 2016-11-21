package logic;


import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.interfaces.*;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;


public class ClientSignatureGen{
	DHParameterSpec dhSkipParamSpec;
	DHParameterSpec dhParamSpec;
	KeyPair myKeyPair; 
	KeyAgreement myAgreement; 
	byte[] mySecret;
	boolean isInit = false;
	
	
	
	public ClientSignatureGen(){}
	
	public void init() throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException{
		AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
		paramGen.init(512);
		AlgorithmParameters params = paramGen.generateParameters();
		dhSkipParamSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
		KeyPairGenerator myKeypairGen = KeyPairGenerator.getInstance("DH");
		myKeypairGen.initialize(dhSkipParamSpec);
		myKeyPair = myKeypairGen.generateKeyPair();
		myAgreement = KeyAgreement.getInstance("DH");
		myAgreement.init(myKeyPair.getPrivate());
		isInit = true;
	}
	
	
	public void decodeKey(byte[] enckey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalStateException{
		KeyFactory myKeyFac = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(enckey);
		PublicKey receivedPubKey = myKeyFac.generatePublic(x509KeySpec);
		myAgreement.doPhase(receivedPubKey, true);
		mySecret = myAgreement.generateSecret();
	}
	
	public byte[] getSecret(){
		return mySecret; 
	}
	public int getLen(){
		return mySecret.length;
	}
	
	
	public byte[] getPublicKey(){
		if(isInit == false){
			System.out.println("Generator has not been initiated yet.");
			return null;
		}
		return myKeyPair.getPublic().getEncoded();
	}
	
	public byte[] getPrivateKey(){
		if(isInit = false){
			System.out.println("Generator has not been initiated yet.");
			return null;
		}
		return myKeyPair.getPrivate().getEncoded();
	}

}
