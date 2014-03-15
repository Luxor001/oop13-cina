cd C:\Program Files\Java\jre7
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass myOU@jxZ -storepass myOU@jxZ -keystore C:\Users\Francesco\Desktop\workspace\ClientChat/CozzoServerKey.jks
keytool -export -alias ServerKey -storepass myOU@jxZ -file C:\Users\Francesco\Desktop\workspace\ClientChat/CozzoServerKeyCertificate.cer -keystore C:\Users\Francesco\Desktop\workspace\ClientChat/CozzoServerKeyKey.jks
