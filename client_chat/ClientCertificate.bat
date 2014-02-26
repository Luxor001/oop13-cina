@echo off
cd D:\Android\Java\jre7
(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano & echo rn & echo it & echo si) | keytool -genkey -alias ClientKey -keyalg RSA -keypass changeit -storepass changeit -keystore I:\java\workspace\client_chat\ClientKey.jks
keytool -export -alias ClientKey -storepass changeit -file I:\java\workspace\client_chat\ClientCertificate.cer -keystore I:\java\workspace\client_chat\ClientKey.jks
echo on
