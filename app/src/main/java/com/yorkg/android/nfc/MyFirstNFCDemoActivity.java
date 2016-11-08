package com.yorkg.android.nfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yorkg.android.nfc.dataobject.mifare.MifareBlock;
import com.yorkg.android.nfc.dataobject.mifare.MifareClassCard;
import com.yorkg.android.nfc.dataobject.mifare.MifareSector;
import com.yorkg.android.nfc.util.Converter;

public class MyFirstNFCDemoActivity extends ListActivity {
	/** Called when the activity is first created. */

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private TextView mText;
	private int mCount = 0;
	// private Intent intent_1;
	long long_time;
	byte[] testByte = { 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2 };

	String time_test;

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int NETWORK = 4;
	private static final String TAG = "mifare";

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.main);
		// Intent intent_1 = getIntent();

		// setContentView(R.layout.tag_viewer);
		// mText = (TextView) findViewById(R.id.text);
		// mText = (TextView) findViewById(R.id.myTextView);
		// mText.setText("Scan a tag"+getIntent());

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		// myResolveIntent(getIntent());

		// this activity.
		// intent_1 = new
		// Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Setup an intent filter for all MIME based dispatches
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };
		//
		// // Setup a tech list for all MifareClassic tags
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };

		// mText.setText("第" + ++mCount + "次intent请求，请求信息：" + intent);
	}

	// public void getIntent(Intent intent) {
	//
	// }

	public void getCardID(Intent intent) {
		String action = intent.getAction();
		String str2 = "NFCCard UID:";
		byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

		mText = (TextView) findViewById(R.id.myTextView);
		mText.setText(str2 + Converter.getHexString(myNFCID, myNFCID.length));

		// Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		// MifareClassic mfc = MifareClassic.get(tagFromIntent);
		// if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
		// Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		// MifareClassic mfc = MifareClassic.get(tagFromIntent);
		// Tag myTag = mfc.getTag();
		// byte[] myByte = tagFromIntent.getId();
		// int cardID = tagFromIntent.describeContents();
		// String str = myByte.toString();
		// mText = (TextView) findViewById(R.id.myTextView);
		// mText.setText("NFCCard
		// ID:"+str+"--"+cardID+"--"+tagFromIntent.getTechList());
		// }
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}

	@Override
	public void onNewIntent(Intent intent) {
		// Log.i("Foreground dispatch", "Discovered tag with intent: " +
		// intent);
		// getCardID(intent);
		resolveIntent(intent);
		Button myButton = (Button) findViewById(R.id.clear_but);
		myButton.setText(time_test);
		// mText.setText("第" + ++mCount + "次intent请求，请求信息：" + intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}

	void myResolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			// When a tag is discovered we send it to the service to be save. We
			// include a PendingIntent for the service to call back onto. This
			// will cause this activity to be restarted with onNewIntent(). At
			// that time we read it from the database and view it.
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
			// Setup the views
			mText.setText("Scan a tag" + msgs);
		} else {
			Log.e(TAG, "Unknown intent " + intent);
			finish();
			return;
		}
	}

	// 读卡
	void resolveIntent(Intent intent) {
		Date date = new Date();
		long time_old;
		long time_new;
		String action = intent.getAction();

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			time_old = date.getTime();
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			date = new Date();
			time_new = date.getTime();

			long_time = time_new - time_old;
			time_old = time_new;
			time_test = "初始化NFC卡时间:" + Long.toString(long_time) + ";";
			MifareClassCard mifareClassCard = null;

			try {
				mfc.connect();
				date = new Date();
				time_new = date.getTime();

				long_time = time_new - time_old;
				time_old = time_new;
				time_test += "连接NFC卡时间:" + Long.toString(long_time) + ";";
				boolean auth = false;
				int secCount = mfc.getSectorCount();
				mifareClassCard = new MifareClassCard(secCount);
				int bCount = 0;
				int bIndex = 0;
				long_time = date.getTime() - long_time;
				time_test += "连接NFC卡后到开始循环读取扇区:" + Long.toString(long_time) + ";";
				for (int j = 0; j < secCount; j++) {
					MifareSector mifareSector = new MifareSector();
					mifareSector.sectorIndex = j;
					auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
					mifareSector.authorized = auth;
					if (auth) {
						bCount = mfc.getBlockCountInSector(j);
						bCount = Math.min(bCount, MifareSector.BLOCKCOUNT);
						bIndex = mfc.sectorToBlock(j);
						for (int i = 0; i < bCount; i++) {
							byte[] data = mfc.readBlock(bIndex);
							if (j == 12 && i == 2) {
								try {
									mfc.writeBlock(bIndex, testByte);
								} catch (IOException e) {
									time_test += "写错误信息" + e.toString() + ";";
								} finally {
									showAlert(3, "666");
								}

							}
							// showAlert(3,Integer.toString(j)+i);
							MifareBlock mifareBlock = new MifareBlock(data);
							mifareBlock.blockIndex = bIndex;
							bIndex++;
							mifareSector.blocks[i] = mifareBlock;

						}
						mifareClassCard.setSector(mifareSector.sectorIndex, mifareSector);
					} else {

					}
					date = new Date();
					time_new = date.getTime();

					long_time = time_new - time_old;
					time_old = time_new;
					time_test += "第" + (j + 1) + "个扇区读取时间:" + Long.toString(long_time) + ";";
				}
				ArrayList<String> blockData = new ArrayList<String>();
				int blockIndex = 0;
				for (int i = 0; i < secCount; i++) {

					MifareSector mifareSector = mifareClassCard.getSector(i);
					for (int j = 0; j < MifareSector.BLOCKCOUNT; j++) {
						MifareBlock mifareBlock = mifareSector.blocks[j];
						byte[] data = mifareBlock.getData();
						blockData.add("Block " + blockIndex++ + " : " + Converter.getHexString(data, data.length));
					}
				}
				String[] contents = new String[blockData.size()];
				blockData.toArray(contents);
				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contents));
				getListView().setTextFilterEnabled(true);
				date = new Date();
				time_new = date.getTime();

				long_time = time_new - time_old;
				time_old = time_new;
				time_test += "将数据装入页面:" + Long.toString(long_time) + ";";

			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				showAlert(3, e.toString());
			} finally {

				if (mifareClassCard != null) {
					mifareClassCard.debugPrint();
				}
			}
		} // End of method

	}

	private void showAlert(int alertCase, String str) {
		// prepare the alert box
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		switch (alertCase) {

		case AUTH:// Card Authentication Error
			alertbox.setMessage("Authentication Failed ");
			break;
		case EMPTY_BLOCK_0: // Block 0 Empty
			alertbox.setMessage("Failed reading ");
			break;
		case EMPTY_BLOCK_1:// Block 1 Empty
			alertbox.setMessage(str);
			break;
		case NETWORK: // Communication Error
			alertbox.setMessage("Tag reading error");

			break;
		}
		// set a positive/yes button and create a listener
		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			// Save the data from the UI to the database - already done
			public void onClick(DialogInterface arg0, int arg1) {
				clearFields();
			}
		});
		// display box
		alertbox.show();

	}

	private void clearFields() {

	}
}