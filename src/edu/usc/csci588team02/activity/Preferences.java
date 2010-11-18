package edu.usc.csci588team02.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import edu.usc.csci588team02.R;

public class Preferences extends Activity
{
	public class refreshTimeListener implements OnItemSelectedListener
	{
		ArrayAdapter<CharSequence> mLocalAdapter;
		Activity mLocalContext;

		/**
		 * Constructor
		 * 
		 * @param c
		 *            - The activity that displays the Spinner.
		 * @param ad
		 *            - The Adapter view that controls the Spinner. Instantiate
		 *            a new listener object.
		 */
		public refreshTimeListener(final Activity c,
				final ArrayAdapter<CharSequence> ad)
		{
			mLocalContext = c;
			mLocalAdapter = ad;
		}

		/**
		 * When the user selects an item in the spinner, this method is invoked
		 * by the callback chain. Android calls the item selected listener for
		 * the spinner, which invokes the onItemSelected method.
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 * @param parent
		 *            - the AdapterView for this listener
		 * @param v
		 *            - the View for this listener
		 * @param pos
		 *            - the 0-based position of the selection in the
		 *            mLocalAdapter
		 * @param row
		 *            - the 0-based row number of the selection in the View
		 */
		@Override
		public void onItemSelected(final AdapterView<?> parent, final View v,
				final int pos, final long row)
		{
			final SharedPreferences settings = getSharedPreferences(PREF, 0);
			final SharedPreferences.Editor editor = settings.edit();
			final Resources r = getResources();
			final int[] iValues = r.getIntArray(R.array.interval_values);
			int interval = 5;
			if (pos > 0 && pos <= iValues.length)
				interval = iValues[pos];
			editor.putInt("RefreshInterval", interval);
			editor.commit();
			// AppService.this.setIntervalTime(interval);
			if (DEBUG)
			{
				Log.d(TAG, "Clicked on: " + interval);
				Log.d(TAG,
						"Committed: " + settings.getInt("RefreshInterval", 0));
			}
		}

		@Override
		public void onNothingSelected(final AdapterView<?> parent)
		{
		}
	}

	protected static final String PREF = "MyPrefs";
	private static final String TAG = "PreferencesActivity";
	protected final boolean DEBUG = false;
	protected ArrayAdapter<CharSequence> mAdapter;
	protected int mPos;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		final Resources r = getResources();
		final int[] iValues = r.getIntArray(R.array.interval_values);
		// Get current preferences
		final SharedPreferences prefs = getSharedPreferences(PREF, 0);
		final int interval = prefs.getInt("RefreshInterval", 5);
		final String actionBarPref = prefs.getString("ActionBarPreference",
				"EventDetails");
		// Setup spinner data and callback
		final Spinner refreshTime = (Spinner) findViewById(R.id.spinnerRefreshTime);
		mAdapter = ArrayAdapter.createFromResource(this, R.array.intervals,
				android.R.layout.simple_spinner_item);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		refreshTime.setAdapter(mAdapter);
		final OnItemSelectedListener spinnerListener = new refreshTimeListener(
				this, mAdapter);
		refreshTime.setOnItemSelectedListener(spinnerListener);
		// set initial value to current preference for spinner
		refreshTime.setSelection(
				java.util.Arrays.binarySearch(iValues, interval), false);
		// Setup radio button data and callbacks
		final RadioButton r1 = (RadioButton) findViewById(R.id.rbActionButtonPrefDetails);
		r1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				final SharedPreferences settings = getSharedPreferences(PREF, 0);
				final SharedPreferences.Editor editor = settings.edit();
				editor.putString("ActionBarPreference", "EventDetails");
				editor.commit();
				if (DEBUG)
					Log.d(TAG,
							"Should have commit r1: "
									+ settings.getString("ActionBarPreference",
											"EventDetails"));
			}
		});
		final RadioButton r2 = (RadioButton) findViewById(R.id.rbActionButtonPrefMap);
		r2.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				final SharedPreferences settings = getSharedPreferences(PREF, 0);
				final SharedPreferences.Editor editor = settings.edit();
				editor.putString("ActionBarPreference", "Map");
				editor.commit();
				if (DEBUG)
					Log.d(TAG,
							"Should have commit r2: "
									+ settings.getString("ActionBarPreference",
											"Map"));
			}
		});
		final RadioButton r3 = (RadioButton) findViewById(R.id.rbActionButtonPrefNav);
		r3.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				final SharedPreferences settings = getSharedPreferences(PREF, 0);
				final SharedPreferences.Editor editor = settings.edit();
				editor.putString("ActionBarPreference", "Navigate");
				editor.commit();
				if (DEBUG)
					Log.d(TAG,
							"Should have commit r3: "
									+ settings.getString("ActionBarPreference",
											"Navigate"));
			}
		});
		if (DEBUG)
		{
			Log.d(TAG, "Creating Preferences Activity, and interval is: "
					+ interval);
			Log.d(TAG, "Creating Preferences Activity, and pref is: "
					+ actionBarPref);
		}
		// Setup radio button initial configuration
		if (actionBarPref.equals("EventDetails"))
			r1.setChecked(true);
		else if (actionBarPref.equals("Map"))
			r2.setChecked(true);
		else if (actionBarPref.equals("Navigate"))
			r3.setChecked(true);
		else
			r1.setChecked(true);
	}
}