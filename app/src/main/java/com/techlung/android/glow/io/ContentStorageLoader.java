package com.techlung.android.glow.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Common;
import com.techlung.android.glow.utils.DialogHelper;

public class ContentStorageLoader {

	Activity a;
	
	private String dataDir;
	private String dataFilePath;

	public ContentStorageLoader(Activity a) {
		this.a = a;
		//this.dataDir = a.getExternalFilesDir(null).toString() + "/";
		//this.dataFilePath = this.dataDir + "de.zip";
	}

	/*
	public void unpackAsset(final OnGlowDataLoadedListener listener) {
		DialogHelper.showProgressDialog();

		AsyncTask<Void, Void, Void> unzipTask = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {

				clearDirectory(a.getExternalFilesDir(null));
				copyAssetToIntenal();
				unzip();

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);

				listener.onGlowDataLoaded();

				DialogHelper.hideProgressDialog();
			}
		};
		unzipTask.execute();

	}*/

	/*
	private boolean clearDirectory(File dir) {
		boolean success = true;

		if (dir.exists()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					clearDirectory(file);
				}
				success &= file.delete();
			}
		}
		return success;
	}

	private void copyAssetToIntenal() {
		String filename = "de.zip";
		AssetManager assetManager = a.getResources().getAssets();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			String dataStorageRoot = a.getExternalFilesDir(null).toString();
			out = new FileOutputStream(dataStorageRoot + "/" + filename);

			copyFile(in, out);
		} catch (IOException e) {
			Log.e(ContentStorageLoader.class.getNameResource(), "failed to copy asset file " + filename, e);
		} finally {
			closeStreams(in, out);
		}
	}

	private void closeStreams(final InputStream in, final OutputStream out) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyFile(final InputStream in, final OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
	
	private boolean unzip() {
		
		// validate
		boolean success = true;
		Decompress d = new Decompress(this.dataDir, this.dataFilePath);
		if (d.unzip()) {
			Log.d("Glow", "successfuly unzipped file...");
		} else {
			Log.d("Glow", "Error: could not unzip Data...");
			success = false;
		}

		File f = new File(this.dataFilePath);
		if (f.exists()) {
			f.delete();
		}

		return success;
	}

	public class Decompress {
		private String _zipFile;
		private String _zipPath;

		public Decompress(String zipPath, String zipFile) {
			_zipPath = zipPath;
			_zipFile = zipFile;
		}

		public boolean unzip() {
			try {
				FileInputStream fin = new FileInputStream(_zipFile);
				ZipInputStream zin = new ZipInputStream(fin);
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					Log.v("Glow", "Unzipping " + ze.getNameResource() + " to " +_zipPath);

					if (ze.isDirectory()) {
						_dirChecker(_zipPath + ze.getNameResource());
					} else {

						File f = new File(_zipPath + ze.getNameResource());
						File dir = f.getParentFile();
						if (dir != null && !dir.exists()) {
							dir.mkdirs();
						}
						if (!f.exists()) {
							f.createNewFile();
						}

						FileOutputStream fout = new FileOutputStream(f);

						streamCopy(zin, fout);

						zin.closeEntry();
						fout.close();
					}

				}
				zin.close();
			} catch (Exception e) {
				Log.e("Glow", "unzip", e);
				return false;
			}

			return true;

		}
		
		private void streamCopy(InputStream in, OutputStream out) throws IOException {
		    byte[] buffer = new byte[32 * 1024]; // play with sizes..
		    int readCount;
		    while ((readCount = in.read(buffer)) != -1) {
		        out.write(buffer, 0, readCount);
		    }
		}

		private void _dirChecker(String dir) {
			File f = new File(dir);

			if (!f.exists()) {
				f.mkdirs();
				Log.v("Glow", "Created Directory " + f.getAbsolutePath());
			}
		}
	}*/

	public void load() {
		// validate
		try {
			AssetManager assetManager = a.getResources().getAssets();

			String lang = "de";

			String[] pamphlets = assetManager.list(lang);

			// Reset Content
			GlowData.getInstance().clear();

			// Create Pamphlets and add to Content
			for (String pamphletDir: pamphlets) {


				if (pamphletDir.equals(Common.FILE_CONTACT)) {
					GlowData.getInstance().loadContact(assetManager.open(lang + "/" + pamphletDir));
				} else if (pamphletDir.equals(Common.FILE_INFO)) {
					GlowData.getInstance().loadInfo(assetManager.open(lang + "/" + pamphletDir));
				} else {
					Tract newPamphlet = new Tract(lang + "/" + pamphletDir);

					newPamphlet.loadMeta(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_META));
					newPamphlet.loadHtmlContent(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_CONTENT));
					newPamphlet.loadHtmlAdditional(assetManager.open(lang + "/" + pamphletDir + "/" + Common.FILE_ADDITIONAL));
					newPamphlet.setCoverPath(lang + "/" + pamphletDir + "/" + Common.FILE_COVER);

					GlowData.getInstance().addPamphlet(newPamphlet);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
