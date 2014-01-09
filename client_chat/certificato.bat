@echo off
cd D:\Android\Java\jre7
(echo francesco cozzolino & echo cozzo's farm & echo cozzo & echo misano & echo rn & echo it & echo si) | keytool -genkey -alias serverkey -keyalg RSA -keypass password -storepass password -keystore C:\chiave.jks
keytool -export -alias serverkey -storepass password -file C:\certificato.cer -keystore C:\chiave.jks
echo on
