package fr.damienbrun.drinkmehot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

public class FragmentCity extends Fragment {

	private onPostExecutedListened mCallback;

	public interface onPostExecutedListened {
		public void onPostExecuted(boolean b);
	}

	public void onChoosed() {
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setmCallback((onPostExecutedListened) activity);
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.v("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	/*****
	 * RECUPERER LES ERRORS LORS D'UNE ERROR 500 du SERVEUR PAR EXEMPLE ET FAIRE
	 * EN SORTE QUE CELA FONCTIONNE
	 ****/

	public onPostExecutedListened getmCallback() {
		return mCallback;
	}

	public void setmCallback(onPostExecutedListened mCallback) {
		this.mCallback = mCallback;
	}

}
