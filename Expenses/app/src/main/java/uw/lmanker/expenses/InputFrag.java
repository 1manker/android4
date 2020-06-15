package uw.lmanker.expenses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;


public class InputFrag extends Fragment {
    //this is the input fragment, it edits entries and adds new ones
    private MainFrag.OnFragmentInteractionListener mListener;
    Boolean editFlag = false;
    //this variable checks to see if this is an edit or a new entry
    Entry mEntry;
    TextInputEditText name;
    TextInputEditText category;
    TextInputEditText date;
    TextInputEditText amount;
    TextInputEditText note;
    public InputFrag() {}
    public InputFrag(Entry entry){
        //overloaded constructor for an edit
        editFlag = true;
        mEntry = entry;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_input, container, false);
        return rootView;
    }
    @Override
    public void onViewCreated(final View rootView, @Nullable Bundle savedInstanceState){
        name = rootView.findViewById(R.id.name);
        category = rootView.findViewById(R.id.category);
        date = rootView.findViewById(R.id.date);
        amount = rootView.findViewById(R.id.amount);
        note = rootView.findViewById(R.id.note);
        if(editFlag){
            //fills in the fields with the entry selected for edit
            name.setText(mEntry.mName);
            category.setText(mEntry.mCategory);
            date.setText(mEntry.mDate);
            amount.setText(String.valueOf(mEntry.mAmount));
            note.setText(mEntry.mNote);
        }
        Button cancel = rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mListener != null) {
                    String temp = "main";
                    mListener.onFragmentInteraction(temp);
                }
            }

        });
        Button submit = rootView.findViewById(R.id.submit);
        //buttons to submit changes or cancel and go back to the main fragment
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mListener != null) {
                    //awful, awful block of code, but i realized about 15 minutes after i turned in
                    //the cover page in the morning that i didn't have a try block if "." was put
                    //in for amount.
                    try{
                        Double.parseDouble(amount.getText().toString());
                        if(name.getText().toString().trim().length() > 0 &&
                                category.getText().toString().trim().length() > 0 &&
                                date.getText().toString().trim().length() > 0 &&
                                amount.getText().toString().trim().length() > 0) {
                            Entry frag_entry = new Entry(name.getText().toString(), category
                                    .getText().toString(),date.getText().toString(),
                                    Double.parseDouble(amount.getText().
                                    toString()), note.getText().toString(), 1);
                            if(!editFlag) {write(frag_entry);}
                            //if this is a new entry then write a  new entry
                            else{
                                //otherwise update the entry
                                frag_entry.mId = mEntry.mId;
                                edit(frag_entry);
                            }
                            String temp = "main";
                            mListener.onFragmentInteraction(temp);
                        }
                        else{
                            //if everything except the note field isn't filled in send a toast
                            Toast toast = Toast.makeText(getActivity(),
                                    "Please Fill In All Values",
                                    Toast.LENGTH_SHORT);

                            toast.show();
                        }
                    }
                    catch(NumberFormatException ex){
                        Toast exToast = Toast.makeText(getActivity(),
                                "Please enter only numbers for amount.",
                                Toast.LENGTH_SHORT);
                        exToast.show();
                    }
                }
            }

        });
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity = getActivity();
        try{
            mListener = (MainFrag.OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + "must have frag listenener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String name);
    }

    private void write(Entry entry){
        //just writes the new entry to the database
        DbHandler dbHelper = new DbHandler(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        ContentValues values = new ContentValues();
        values.put(DbHandler.FeedEntry.COLUMN_NAME_ID, findID());
        values.put(DbHandler.FeedEntry.COLUMN_NAME_NAME, entry.mName);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_CATEGORY, entry.mCategory);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_DATE, entry.mDate);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_AMOUNT, entry.mAmount);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_NOTE, entry.mNote);
        long newRowId = db.insert(DbHandler.FeedEntry.TABLE_NAME, null, values);
    }

    private void edit(Entry entry){
        //updates the edited entry
        DbHandler dbHelper = new DbHandler(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        ContentValues values = new ContentValues();
        String[] oldID = {String.valueOf(entry.mId)};
        values.put(DbHandler.FeedEntry.COLUMN_NAME_ID, entry.mId);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_NAME, entry.mName);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_CATEGORY, entry.mCategory);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_DATE, entry.mDate);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_AMOUNT, entry.mAmount);
        values.put(DbHandler.FeedEntry.COLUMN_NAME_NOTE, entry.mNote);
        long newRowId = db.update(DbHandler.FeedEntry.TABLE_NAME, values,
                "id = " + oldID[0], null);
    }

    private int findID(){
        //this is the dumbest way to do this, but i couldn't get max to work because sqlite is awful
        DbHandler dbHelper = new DbHandler(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DbHandler.FeedEntry.COLUMN_NAME_ID;
        Cursor cursor = db.query(
                DbHandler.FeedEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null              // The sort order
        );
        int id = 0;
        while(cursor.moveToNext()) {
            int temp = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_ID));
            if(temp > id){id = temp;}
            Log.v(String.valueOf(temp), "THIS IS THE ID");

        }
        cursor.close();
        //gives a new id that's one plus the max
        return id + 1;

    }
}
