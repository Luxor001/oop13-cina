cd E:\java\jdk\jre
(echo cozzo & echo cozzo & echo cozzo & echo & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass changeit -storepass changeit -keystore E:\java\workspace\client_chat/cozzoClientKey.jks
keytool -export -alias ClientKey -storepass changeit -file E:\java\workspace\client_chat/cozzoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/cozzoClientKeyKey.jks
