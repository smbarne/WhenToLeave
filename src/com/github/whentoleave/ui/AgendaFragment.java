package com.github.whentoleave.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.whentoleave.R;

/**
 * Fragment which shows a list of all events in the next two weeks.
 */
public class AgendaFragment extends ListFragment implements
		LoaderCallbacks<Cursor>
{
	/**
	 * Cursor Adapter for creating and binding agenda view items
	 */
	private class AgendaCursorAdapter extends CursorAdapter
	{
		/**
		 * Local reference to the layout inflater service
		 */
		private final LayoutInflater inflater;

		/**
		 * @param context
		 *            The context where the ListView associated with this
		 *            SimpleListItemFactory is running
		 * @param c
		 *            The database cursor. Can be null if the cursor is not
		 *            available yet.
		 * @param flags
		 *            Flags used to determine the behavior of the adapter, as
		 *            per
		 *            {@link CursorAdapter#CursorAdapter(Context, Cursor, int)}.
		 */
		public AgendaCursorAdapter(final Context context, final Cursor c,
				final int flags)
		{
			super(context, c, flags);
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void bindView(final View view, final Context context,
				final Cursor cursor)
		{
			// Set the event title
			final int titleColumnIndex = cursor
					.getColumnIndex(CalendarContract.Events.TITLE);
			final String title = cursor.getString(titleColumnIndex);
			final TextView titleView = (TextView) view
					.findViewById(R.id.agendaItemTitle);
			if (title != null)
				titleView.setText(title);
			else
				titleView.setText("None");
			
			// Set the event start time
			final int startTimeColumnIndex = cursor
					.getColumnIndex(CalendarContract.Events.DTSTART);
			final long startTime = cursor.getLong(startTimeColumnIndex);
			final TextView startTimeView = (TextView) view
					.findViewById(R.id.agendaItemWhen);
			startTimeView.setText(DateFormat.format("hh:mma 'on' EEEE, MMM dd",
					new Date(startTime)));
			
			
			// Set the event location
			final int locationColumnIndex = cursor
					.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
			final String location = cursor.getString(locationColumnIndex);
			final TextView locationView = (TextView) view
					.findViewById(R.id.agendaItemWhere);
			if (location==null || location.equals(""))
				locationView.setText(getText(R.string.event_no_location));
			else
				locationView.setText(location);
		}

		@Override
		public View newView(final Context context, final Cursor cursor,
				final ViewGroup parent)
		{
			View root = inflater.inflate(R.layout.agenda_item, parent, false);
			// TODO -SB
			//setEmptyText(getText(R.string.agenda_loading));
			return root;
		}
	}

	/**
	 * Adapter to display the list's data
	 */
	private CursorAdapter adapter;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// TODO -SB
		//setEmptyText(getText(R.string.agenda_loading));
		
		adapter = new AgendaCursorAdapter(getActivity(), null, 0);
		setListAdapter(adapter);
		getListView().setChoiceMode(AbsListView.CHOICE_MODE_NONE);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int arg0, final Bundle arg1)
	{
		// TODO: what if we want to default to just all calendars??
		
		final Calendar twoWeeksFromNow = Calendar.getInstance();
		twoWeeksFromNow.add(Calendar.DATE, 14);
		
		SharedPreferences settings = getActivity().getSharedPreferences("MyPrefs", 0);
		String selectedURIs = settings.getString("selectedCalendarURIS", "-1");
		StringTokenizer st = new StringTokenizer(selectedURIs, ",");
		ArrayList<String> calendarURIs = new ArrayList<String>();
		
        while (st.hasMoreTokens()) {
        	calendarURIs.add(st.nextToken());
        }
		
		String calIDSelect = "( " + CalendarContract.Events.CALENDAR_ID + " =? ";
		for (int i=1; i<calendarURIs.size(); i++)
			calIDSelect += " OR " + CalendarContract.Events.CALENDAR_ID + " =? ";
		calIDSelect += " ) AND ";
		
		// TODO: limit the search distance again.  Perhaps use a preference as well.

		final String selection = calIDSelect +  
				CalendarContract.Events.DTSTART + ">=? AND " +
				//CalendarContract.Events.DTEND + "<? AND " + 
				CalendarContract.Events.ALL_DAY + " IS 0";
		
		List<String> selectionArguments = new ArrayList<String>(calendarURIs);
		selectionArguments.add(Long.toString(Calendar.getInstance().getTimeInMillis()));
		//selectionArguments.add(Long.toString(twoWeeksFromNow.getTimeInMillis()));
		
		final String[] projection = { BaseColumns._ID,
				CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART,
				CalendarContract.Events.EVENT_LOCATION };
		
		return new CursorLoader(getActivity(),
		CalendarContract.Events.CONTENT_URI, projection, selection,
		selectionArguments.toArray(new String[0]), CalendarContract.Events.DTSTART);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.agenda, container, false);
	}

	@Override
	public void onListItemClick(final ListView l, final View v,
			final int position, final long id)
	{
		final Intent detailsIntent = new Intent(getActivity(),
				EventDetailsFragment.class);
		detailsIntent.putExtra("eventId", adapter.getItemId(position));
		startActivity(detailsIntent);
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader)
	{
		adapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor data)
	{
		adapter.swapCursor(data);

		// TODO
		//if (data.getCount() == 0)
		//	setEmptyText(getText(R.string.agenda_empty)); // Can't be used with a custom item!
	}
}