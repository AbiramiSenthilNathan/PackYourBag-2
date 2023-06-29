package com.example.packyourbag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.packyourbag.Adapter.ChecklistAdapter;
import com.example.packyourbag.Constants.MyConstants;
import com.example.packyourbag.Data.AppData;
import com.example.packyourbag.Database.RoomDB;
import com.example.packyourbag.Models.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckList extends AppCompatActivity {
    RecyclerView recyclerView;
    ChecklistAdapter checklistAdapter;
    RoomDB database;
    List<Items> itemsList= new ArrayList<>();
    String header,show;

    EditText txtAdd;
    Button btnAdd;
    LinearLayout linearLayout;

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one,menu);
        recyclerView = findViewById(R.id.recyclerview);

        if(MyConstants.MY_SELECTIONS.equals(header)){
          menu.getItem(0).setVisible(false);
          menu.getItem(2).setVisible(false);
          menu.getItem(3).setVisible(false);
        }else if (MyConstants.MY_LIST_CAMEL_CASE.equals(header))
            menu.getItem(1).setVisible(false);

        MenuItem menuItem = menu.findItem (R.id.btnSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Items> mFinallist = new ArrayList<>();
                for (Items items:itemsList){
                    if(items.getItemname().toLowerCase().startsWith(newText.toLowerCase())){
                        mFinallist.add(items);
                    }
                }
                updateRecycler(mFinallist);
                return false;
            }
        });

        return true;




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this,CheckList.class);
        AppData appData = new AppData(database,this);

        switch (item.getItemId()){

            case R.id.btnMySelection:
                intent.putExtra(MyConstants.HEADER_SMALL,MyConstants.MY_SELECTIONS);
                intent.putExtra(MyConstants.SHOW_SMALL,MyConstants.FALSE_STRING);
                startActivityForResult(intent,101);
                return true;

            case R.id.btnCustomList:
                intent.putExtra(MyConstants.HEADER_SMALL,MyConstants.MY_LIST_CAMEL_CASE);
                intent.putExtra(MyConstants.SHOW_SMALL,MyConstants.TRUE_STRING);
                startActivity(intent);
                return true;

            case R.id.btnDeleteDefault:
                new AlertDialog.Builder(this)
                        .setTitle("Delete default data")
                        .setMessage("Are you sure?\n\nAs this will delete the data provided by(Pack Your Bag) while installing.")
                        .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                appData.persistDataByCategory(header,true);
                                itemsList= database.mainDao().getAll(header);
                                updateRecycler(itemsList);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(R.drawable.ic_alert)
                        .show();
                return true;

                case R.id.btnReset:
                    new AlertDialog.Builder(this)
                            .setTitle("Reset to Default")
                            .setMessage("Are you Sure\n\n As this will load the default data provided by (Pack Your Bag)"+
                                    "and will delete the custom data you have added in ("+header+")")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    appData.persistDataByCategory(header,false);
                                    itemsList = database.mainDao().getAll(header);
                                    updateRecycler(itemsList);

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setIcon(R.drawable.ic_alert)
                            .show();
                    return true;

                 case R.id.btnExit:
                     this.finishAffinity();
                     Toast.makeText(this,"Pack Your Bag\nExit completed",Toast.LENGTH_SHORT).show();
                     return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            itemsList = database.mainDao().getAll(header);
            updateRecycler(itemsList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerview);

        Intent intent =getIntent();
        header=intent.getStringExtra(MyConstants.HEADER_SMALL);
        show=intent.getStringExtra(MyConstants.SHOW_SMALL);

        getSupportActionBar().setTitle(header);

        txtAdd =findViewById(R.id.txtAdd);
        btnAdd = findViewById(R.id.btnAdd);
        linearLayout=findViewById(R.id.linearlayout);

        database = RoomDB.getInstance(this);

        if(MyConstants.FALSE_STRING.equals(show)){
            linearLayout.setVisibility(View.GONE);
            itemsList=database.mainDao().getAllSelected(true);
        }else{
            itemsList=database.mainDao().getAll(header);
        }
        updateRecycler(itemsList);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = txtAdd.getText().toString();
                if(itemName!=null&& !itemName.isEmpty()){
                    addNewItem(itemName);
                    Toast.makeText(CheckList.this, "Item Added", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CheckList.this, "Empty cant be added", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addNewItem(String itemName){
        Items item = new Items();
        item.setChecked(false);
        item.setCategory(header);
        item.setItemname(itemName);
        item.setAddedby(MyConstants.USER_SMALL);
        database.mainDao().saveItem(item);
        itemsList = database.mainDao().getAll(header);
        updateRecycler(itemsList);
        recyclerView.scrollToPosition(checklistAdapter.getItemCount()-1);
        txtAdd.setText("");
    }
    private void updateRecycler(List<Items>itemsList){
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
       checklistAdapter = new ChecklistAdapter(CheckList.this,itemsList,database,show);
       recyclerView.setAdapter(checklistAdapter);

    }
}