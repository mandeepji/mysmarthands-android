package com.common_lib.android.networking;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class HttpConnection {

	// ------------------------- Async
	// -------------------------------------------------------------------------+
	public interface AsyncHttpRequestDelegate {

		void requestReturnedWithResult(String res);

		void requestFailed(Exception e);
	}

	public interface AsyncHttpPostDelegate {

		void postReturnedWithResult(String res);

		void postFailed(Exception e);
	}

	public static void asyncHttpRequestString(final String urlStr,
			final AsyncHttpRequestDelegate delegate) {

		new Thread(new Runnable() {

			public void run() {
				try {
					delegate.requestReturnedWithResult(getHttpRequestString(urlStr));
				} catch (Exception e) {
					delegate.requestFailed(e);
				}
			}
		}).start();
	}

	public static void asyncHttpRequestString(final String urlStr,
			final List<NameValuePair> nameValuePairs,
			final AsyncHttpRequestDelegate delegate) {

		new Thread(new Runnable() {

			public void run() {
				try {
					delegate.requestReturnedWithResult(getHttpRequestString(
							urlStr, nameValuePairs));
				} catch (Exception e) {
					delegate.requestFailed(e);
				}
			}
		}).start();
	}

	public static void asyncHttpPostString(final String urlStr,
			final List<NameValuePair> nameValuePairs,
			final AsyncHttpPostDelegate delegate) {

		new Thread(new Runnable() {

			public void run() {
				try {
					delegate.postReturnedWithResult(getHttpPostResponseString(
							urlStr, nameValuePairs));
				} catch (Exception e) {
					delegate.postFailed(e);
					// Log.e("RBI", "Networking", e);
				}
			}
		}).start();
	}

	// ------------------------- Http Connections
	// ----------------------------------------------------------+
	public static HttpURLConnection getHttpRequest(String urlStr) throws IOException {

		HttpURLConnection con = null;

		URL url = new URL(urlStr);
		// URL url = new URL("http://www.google.com");
		con = (HttpURLConnection) url.openConnection();

		return con;
	}

	public static String getHttpRequestString(String urlStr) throws IOException {

		HttpURLConnection con = getHttpRequest(urlStr);
		String ret = null;
		ret = streamToString(con.getInputStream());

		return ret;
	}

	public static String getHttpRequestString(String urlStr,
			List<NameValuePair> params) throws IOException {

		try {
			urlStr += getQuery(params);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		return getHttpRequestString(urlStr);
	}

	public static HttpResponse getHttpPostResponse(String urlStr,
			List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException {

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlStr);
		HttpResponse response = null;


		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		// Log.d("RBI", httppost.getURI().toString());
		response = httpclient.execute(httppost);


		return response;
	}

	public static String getHttpPostResponseString(String urlStr,
			List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException {

		HttpResponse response = getHttpPostResponse(urlStr, nameValuePairs);
		String ret = null;		
		ret = streamToString(response.getEntity().getContent());

		return ret;
	}

	// ------------------------- Helpers
	// -------------------------------------------------------------------+
	public static String streamToString(InputStream in) throws IOException {

		StringBuilder str = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				str.append(line + "\n");
			}
		} 
		catch (IOException e) {
			throw e;
		} 
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}

		return str.toString();
	}

	public static void printStream(InputStream in) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------+

	private static String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	// -----------------------------------------------------------------------------------------------------+
}