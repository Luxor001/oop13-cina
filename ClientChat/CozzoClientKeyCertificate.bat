cd C:\Program Files\Java\jre7
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass IjyLDTgy -storepass IjyLDTgy -keystore C:\Users\Francesco\Desktop\workspace\ClientChat/CozzoClientKey.jks
keytool -export -alias ClientKey -storepass IjyLDTgy -file C:\Users\Francesco\Desktop\workspace\ClientChat/CozzoClientKeyCertificate.cer -keystore C:\Users\Francesco\Desktop\workspace\ClientChat/CozzoClientKeyKey.jks
