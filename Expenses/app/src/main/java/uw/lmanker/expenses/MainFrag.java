package uw.lmanker.expenses;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainFrag extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {
    ArrayList<String> data = new ArrayList<>();
    //gets and arraylist for the recyclerview
    FloatingActionButton fab;
    //button to add entries
    MyRecyclerViewAdapter adapter;
    Entry passEntry;
    //entry used for editing
    private OnFragmentInteractionListener mListener;

    public MainFrag(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState){
        RecyclerView recyclerView = rootView.findViewById(R.id.recView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        initialize();
        data = read();
        //fills the recyclerfiew with everything in the database
        adapter = new MyRecyclerViewAdapter(getContext(), data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mListener != null) {
                    String temp = "input";
                    mListener.onFragmentInteraction(temp);
                }
            }
        });

    }

    @Override
    public void onItemClick(View view, final int position) {
        String cancel = "cancel";
        String edit = "edit";
        String delete = "delete";
        new AlertDialog.Builder(getContext())
                //alert button to delete or edit entries.  I tried pretty hard to get a swipe
                //figured out but i'm an idiot and couldn't get it working.
                .setTitle("Entry")
                .setMessage("Do what with this entry?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String temp = "edit";
                        String pos = adapter.getItem(position);
                        String[] strArr = pos.split(",");
                        passEntry = new Entry(strArr[0], strArr[1], strArr[2],
                                Double.parseDouble(strArr[3]), strArr[4],
                                Integer.parseInt(strArr[5]));
                        mListener.onFragmentInteraction(temp, passEntry);
                    }})
                .setNegativeButton(delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String temp = "edit";
                        String pos = adapter.getItem(position);
                        String[] strArr = pos.split(",");
                        passEntry = new Entry(strArr[0], strArr[1], strArr[2],
                                Double.parseDouble(strArr[3]), strArr[4],
                                Integer.parseInt(strArr[5]));
                        deleteEntry(passEntry);
                        data.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, data.size());
                    }})
                .setNeutralButton(cancel, null).show();
    }

    public void initialize(){
        DbHandler dbHelper = new DbHandler(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity = getActivity();
        try{
            mListener = (OnFragmentInteractionListener) activity;
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
        //picks up which function to use, the second is the edit
        void onFragmentInteraction(String name);
        void onFragmentInteraction(String name, Entry entry);
    }

    public ArrayList<String> read(){
        //fills up the recyclerview
        ArrayList<String> entries = new ArrayList<>();
        DbHandler dbHelper = new DbHandler(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DbHandler.FeedEntry.COLUMN_NAME_ID;
        Cursor cursor = db.query(
                DbHandler.FeedEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_NAME));
            String category = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_CATEGORY));
            String date = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_DATE));
            double amount = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_AMOUNT));
            String note = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_NOTE));
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DbHandler.FeedEntry.COLUMN_NAME_ID));
            Entry entry = new Entry(name, category, date, amount, note, id);
            entries.add(entry.condense());
        }
        cursor.close();
        return entries;
    }
    private void deleteEntry(Entry entry) {
        DbHandler dbHelper = new DbHandler(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        String[] oldID = {String.valueOf(entry.mId)};
        db.delete(DbHandler.FeedEntry.TABLE_NAME, DbHandler.FeedEntry.COLUMN_NAME_ID
                + "=?", oldID);
    }
    }
