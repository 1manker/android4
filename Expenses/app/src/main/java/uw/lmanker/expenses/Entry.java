package uw.lmanker.expenses;

public class Entry {
    //simple class to make the entries a bit more manageable
    String mName;
    String mCategory;
    String mDate;
    double mAmount;
    String mNote;
    int mId;

    public Entry(String name, String category, String date, double amount, String note, int id){
        mName = name;
        mCategory = category;
        mDate = date;
        mAmount = amount;
        mNote = note;
        mId = id;
    }

    public String condense(){
        //puts the object into a string format for the recyclerview
        String condensed = mName + "," + mCategory + "," + mDate + "," + mAmount + "," + mNote +
                "," + mId;
        return condensed;
    }

}
