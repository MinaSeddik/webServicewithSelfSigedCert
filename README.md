
# Simple tomcat Web Server with self-signed Certificate - using spring-boot

## Server-side Certificate

To implement the server-side X.509 authentication in our Spring Boot application, we first need to create a server-side certificate.

Let's start with creating a so-called certificate signing request (CSR):

```bash
openssl req -new -newkey rsa:4096 -keyout localhost.key –out localhost.csr
```

Similarly, as for the CA certificate, we have to provide the password for the private key. Additionally, let's use localhost as a common name (CN).

Before we proceed, we need to create a configuration file – localhost.ext. It'll store some additional parameters needed during signing the certificate.

```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
```

A ready to use file is also available here.

Now, it's time to sign the request with our rootCA.crt certificate and its private key:

```bash
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in localhost.csr -out localhost.crt -days 365 -CAcreateserial -extfile localhost.ext
```
Note that we have to provide the same password we used when we created our CA certificate.

At this stage, we finally have a ready to use localhost.crt certificate signed by our own certificate authority.

To print our certificate's details in a human-readable form we can use the following command:

```bash
openssl x509 -in localhost.crt -text
```

### Import to the Keystore

we'll see how to import the signed certificate and the corresponding private key to the keystore.jks file.

We'll use the PKCS 12 archive, to package our server's private key together with the signed certificate. Then we'll import it to the newly created keystore.jks.

We can use the following command to create a .p12 file:

```bash
openssl pkcs12 -export -out localhost.p12 -name "localhost" -inkey localhost.key -in localhost.crt
```
So we now have the localhost.key and the localhost.crt bundled in the single localhost.p12 file.

Let's now use keytool to create a keystore.jks repository and import the localhost.p12 file with a single command:

```bash
keytool -importkeystore -srckeystore localhost.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype JKS
```
At this stage, we have everything in place for the server authentication part. Let's proceed with our Spring Boot application configuration.

in **application.properties**
```
server.ssl.key-store=../store/keystore.jks
server.ssl.key-store-password=${PASSWORD}
server.ssl.key-alias=localhost
server.ssl.key-password=${PASSWORD}
server.ssl.enabled=true
server.port=8443
```

### Creating a .pem with the Entire SSL Certificate Trust Chain

Open a text editor (such as wordpad) and paste the entire body of each certificate into one text file in the following order:

1. The Primary Certificate - your_domain_name.crt
2. The Intermediate Certificate - DigiCertCA.crt
3. The Root Certificate - TrustedRoot.crt

```
-----BEGIN CERTIFICATE-----
(Your Primary SSL certificate: your_domain_name.crt)
-----END CERTIFICATE-----
-----BEGIN CERTIFICATE-----
(Your Intermediate certificate: DigiCertCA.crt)
-----END CERTIFICATE-----
-----BEGIN CERTIFICATE-----
(Your Root certificate: TrustedRoot.crt)
-----END CERTIFICATE-----
```

Save the combined file as your_domain_name.pem **local.pem**. The .pem file is now ready to use.

example:
```bash
cat server.crt intermidiateCA.crt rootCA.crt > local.pem
```

```bash
openssl pkcs12 -export -out localhost.p12 -name "localhost" -inkey rootCA.key -in local.pem
```

```bash
keytool -importkeystore -srckeystore localhost.p12 -srcstoretype PKCS12 -destkeystore keystore2.jks -deststoretype JKS
```
