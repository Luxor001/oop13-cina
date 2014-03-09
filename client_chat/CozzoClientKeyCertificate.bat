cd E:\java\jdk\jre
(echo Cozzo & echo Cozzo & echo Cozzo & echo  & echo  & echo  & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass xMombQx2 -storepass xMombQx2 -keystore E:\java\workspace\client_chat/CozzoClientKey.jks
keytool -export -alias ClientKey -storepass xMombQx2 -file E:\java\workspace\client_chat/CozzoClientKeyCertificate.cer -keystore E:\java\workspace\client_chat/CozzoClientKeyKey.jks
