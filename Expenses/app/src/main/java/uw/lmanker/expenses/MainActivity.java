package uw.lmanker.expenses;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;



public class MainActivity extends AppCompatActivity implements
        MainFrag.OnFragmentInteractionListener{
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFrag())
                    .commit();
        }
    }
    public void onFragmentInteraction(String name) {
        //checks to see which fragment to switch to
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Log.v(name, "this is what the fragment sent");
        FragmentManager fm = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction();
        if(name.equals("input")) {
            transaction.replace(R.id.container, new InputFrag());
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else{
            transaction.replace(R.id.container, new MainFrag());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    public void onFragmentInteraction(String name, Entry entry) {
        //overloaded method that can have an entry passed through it
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Log.v(name, "this is what the fragment sent");
        FragmentManager fm = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction();
        if(name.equals("edit")) {
            //a bundle is probably smarter to use? but i couldn't get it to work
            transaction.replace(R.id.container, new InputFrag(entry));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}