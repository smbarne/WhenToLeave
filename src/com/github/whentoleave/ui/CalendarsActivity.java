package com.github.whentoleave.ui;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Retrieves a simple list of all of the user's calendars
 */
public final class CalendarsActivity extends ListActivity implements
		LoaderCallbacks<Cursor>
{
	/**
	 * Adapter to display the list's data
	 */
	
    private SimpleCursorAdapter mAdapter;
    
	/**
	 * Preferences name to load settings from
	 */
	private static final String PREF = "MyPrefs";
	
	/**
	 * Logging tag
	 */
	private static final String TAG = "CalendarsActivity";

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getListView().setTextFilterEnabled(true);
		getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_activated_1, null,
				new String[] { CalendarContract.Calendars.CALENDAR_DISPLAY_NAME },
				new int[] { android.R.id.text1 }, 0);
		setListAdapter(mAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	protected void onPause() {
	    SaveSelections();
	    super.onPause();
	}
	
	private void LoadSelections() {
        SharedPreferences settings = getSharedPreferences(CalendarsActivity.PREF, 0);
        String selectedURIs = settings.getString("selectedCalendarURIS", "-1");
		StringTokenizer st = new StringTokenizer(selectedURIs, ",");
		ArrayList<Long> calendarURIs = new ArrayList<Long>();
		
        while (st.hasMoreTokens()) {
        	calendarURIs.add(Long.parseLong(st.nextToken()));
        }
		 
        for (int i=0; i< getListView().getAdapter().getCount(); i++)
        {
        	if (calendarURIs.contains(getListView().getAdapter().getItemId(i)))
        		getListView().setItemChecked(i, true);
        }
    }
	
	private void SaveSelections()
	{
		final SharedPreferences settings = getSharedPreferences(
				CalendarsActivity.PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
		
		long[] currentSelectedCalendarURIS = getListView().getCheckedItemIds();
		StringBuilder uriStorage = new StringBuilder();
		for (int i = 0; i < currentSelectedCalendarURIS.length; i++) {
			uriStorage.append(currentSelectedCalendarURIS[i]).append(",");
		}
		
		Log.v(CalendarsActivity.TAG, "Processed: " + uriStorage.toString());
		
		editor.putString("selectedCalendarURIS", uriStorage.toString());
		editor.commit();
		
		Log.v(CalendarsActivity.TAG, "Saved: " + settings.getString("selectedCalendarURIS", ""));
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int arg0, final Bundle arg1)
	{
		final String[] projection = { CalendarContract.Calendars._ID,
			    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
			    CalendarContract.Calendars.ACCOUNT_NAME
				};
		
		return new CursorLoader(this, CalendarContract.Calendars.CONTENT_URI,
				projection, null, null, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
	}
	
	@Override
	public void onListItemClick(final ListView l, final View v,
			final int position, final long id)
	{	
		SaveSelections();
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader)
	{
		mAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor data)
	{
		mAdapter.swapCursor(data);
		LoadSelections();
	}
}
