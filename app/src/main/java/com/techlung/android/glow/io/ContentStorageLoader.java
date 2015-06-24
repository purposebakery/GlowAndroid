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
		this.dataDir = a.getExternalFilesDir(null).toString() + "/";
		this.dataFilePath = this.dataDir + "de.zip";
	}
	
	public void unpackAsset() {
		DialogHelper.showProgressDialog();

		copyAssetToIntenal();
		unzip();

		DialogHelper.hideProgressDialog();
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
			Log.e(ContentStorageLoader.class.getName(), "failed to copy asset file " + filename, e);
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
					Log.v("Glow", "Unzipping " + ze.getName() + " to " +_zipPath);

					if (ze.isDirectory()) {
						_dirChecker(_zipPath + ze.getName());
					} else {

						File f = new File(_zipPath + ze.getName());
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
	}

	public void load() {
        DialogHelper.showProgressDialog();
		// validate
		boolean noData = false;

		File dataDir = new File(this.dataDir + "/de");

		if (!dataDir.exists() || !dataDir.isDirectory()) {
			noData = true;
		}

		File[] pamphlets;
		if (!noData) {
			// get Flyer Dirs
			pamphlets = dataDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return new File(dir, name).isDirectory();
				}
			});

			if (pamphlets != null && pamphlets.length != 0) {

				// Reset Content
				GlowData.getInstance().clear();
				
				// Create Pamphlets and add to Content
				for (int i = 0; i < pamphlets.length; i++) {
					File pamphletDir = pamphlets[i];
					String id = dataDir.getName() + "-" + pamphletDir.getName();

					File metaFile = new File(pamphletDir.getAbsolutePath() + "/" + Common.FILE_META);
					File contentFile = new File(pamphletDir.getAbsolutePath() + "/" + Common.FILE_CONTENT);
					File additionalFile = new File(pamphletDir.getAbsolutePath() + "/" + Common.FILE_ADDITIONAL);
					File coverFile = new File(pamphletDir.getAbsolutePath() + "/" + Common.FILE_COVER);

					File[] imagesFiles = pamphletDir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return !name.contains(Common.FILE_COVER) && (name.endsWith(".jpg") || name.endsWith(".png"));
						}
					});
					
					// Add and load Pamphlet
					Tract newPamphlet = new Tract(id);
					
					if (metaFile.exists()) newPamphlet.loadMeta(metaFile);
					if (contentFile.exists()) newPamphlet.loadHtmlContent(contentFile);
					if (additionalFile.exists()) newPamphlet.loadHtmlAdditional(additionalFile);
					if (coverFile.exists()) {
                        //newPamphlet.loadCover(coverFile);
                        newPamphlet.setCoverPath(coverFile.getAbsolutePath());
                    }
					if (imagesFiles != null && imagesFiles.length != 0) newPamphlet.loadImages(imagesFiles);

					GlowData.getInstance().addPamphlet(newPamphlet);
				}
				
				// Load Contact
				File contactFile = new File(dataDir.getAbsolutePath() + "/" + Common.FILE_CONTACT);
				if (contactFile != null && contactFile.exists() && contactFile.isFile()) {
					GlowData.getInstance().loadContact(contactFile);
				}

			} else {
				noData = true;
			}
		} else {
			// Reset Content
			GlowData.getInstance().clear();
		}

        DialogHelper.hideProgressDialog();

		/*
		if (noData) {
			UiThreadMessenger.sendMessage("No Data on Phone...");//Toast.makeText(a, "", Toast.LENGTH_LONG).show();
		}*/
	}
}
