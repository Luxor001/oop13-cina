@echo off
cd D:\Android\Java\jre7
(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano & echo rn & echo it & echo si) | keytool -genkey -alias ServerKey -keyalg RSA -keypass password -storepass password -keystore I:\java\workspace\client_chat\ServerKey.jks
keytool -export -alias ServerKey -storepass password -file I:\java\workspace\client_chat\ServerCertificate.cer -keystore I:\java\workspace\client_chat\ServerKey.jks
echo on
