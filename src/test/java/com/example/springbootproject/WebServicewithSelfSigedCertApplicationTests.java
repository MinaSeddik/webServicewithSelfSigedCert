package com.example.springbootproject;

import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootTest
class WebServicewithSelfSigedCertApplicationTests {

	public static final String url = "https://localhost:8443/data";

//	@Test
	void contextLoads() {

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);


			System.out.println(connection.getResponseCode());

			InputStream inputStream = connection.getInputStream();

			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			char[] buffer = new char[4096];
			StringBuilder builder = new StringBuilder();
			int len;
			while ((len = reader.read(buffer)) > 0) {
				builder.append(buffer, 0, len);
			}
			System.out.println(builder.toString());
		} catch (javax.net.ssl.SSLHandshakeException e) {
			System.out.println("SSL exception.");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
