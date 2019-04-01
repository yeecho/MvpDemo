
@ECHO OFF

java -jar signapk.jar platform.x509.pem platform.pk8 a2150_test-release.apk a2150_test-release-signed.apk

Echo Signing Complete 

pause