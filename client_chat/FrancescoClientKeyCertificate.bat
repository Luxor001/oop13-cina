cd E:\java\jdk\jre
(echo Francesco & echo Francesco & echo Francesco & echo & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass changeit -storepass changeit -keystore E:\java\workspace\client_chat/FrancescoClientKey.jks
keytool -export -alias ClientKey -storepass changeit -file E:\java\workspace\client_chat/FrancescoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/FrancescoClientKeyKey.jks
