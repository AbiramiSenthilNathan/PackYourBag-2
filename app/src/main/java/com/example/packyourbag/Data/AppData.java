package com.example.packyourbag.Data;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.packyourbag.Constants.MyConstants;
import com.example.packyourbag.Database.RoomDB;
import com.example.packyourbag.Models.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData extends Application {

    RoomDB database;
    Context context;
    String category;

    public static final String LAST_VERSION ="LAST_VERSION";
    public static final int NEW_VERSION = 1;

    public AppData(RoomDB database) {
        this.database = database;
    }

    public AppData(RoomDB database, Context context) {
        this.database = database;
        this.context = context;
    }

    public List<Items> getBasicData(){
        category ="Basic Needs";
        List<Items> basicItem=new ArrayList<Items>();
        basicItem.add(new Items("visa",category,false));
        basicItem.add(new Items("Passport",category,false));
        basicItem.add(new Items("Tickets",category,false));
        basicItem.add(new Items("Wallet",category,false));
        basicItem.add(new Items("DrivingLicense",category,false));
        basicItem.add(new Items("Currency",category,false));
        basicItem.add(new Items("House Key",category,false));
        basicItem.add(new Items("Book",category,false));
        basicItem.add(new Items("Travel pillow",category,false));
        basicItem.add(new Items("Umbrella",category,false));
        basicItem.add(new Items("Notebook",category,false));
        return basicItem;
    }
    public List<Items>getPersonalcareData(){
        String[] data={"Toothbrush,Toothpaste,comb,Towel,Floss,Mouthwash" };
        return prepareItemsList(MyConstants.PERSONAL_CARE_CAMEL_CASE,data);}
    public List<Items>getClothingData(){
        String[] data={"Shirts,shorts,underwear,socks,pants" };
        return prepareItemsList(MyConstants.CLOTHING_CAMEL_CASE,data);}

    public List<Items>getBabyNeedsData(){
        String[] data={"Diaper,milk bottle,baby food,baby spoon,water bottle" };
        return prepareItemsList(MyConstants.BABY_NEEDS_CAMEL_CASE,data);}

    public List<Items>getHealthData(){
        String[] data={"Medicine,mask,plaster,painReliver" };
        return prepareItemsList(MyConstants.HEALTH_CAMEL_CASE,data);}

    public List<Items>getTechnologyData(){
        String[] data={"Charger,camera,laptop,Mp3" };
        return prepareItemsList(MyConstants.TECHNOLOGY_CAMEL_CASE,data);}

    public List<Items>getFoodData(){
        String[] data={"Noodles,breads,snacks,chocolates" };
        return prepareItemsList(MyConstants.FOOD_CAMEL_CASE,data);}

    public List<Items>getBeachsuppliesData(){
        String[] data={"sunscreen,beachtowel,flipflops,mat" };
        return prepareItemsList(MyConstants.BEACH_SUPPLIES_CAMEL_CASE,data);}

    public List<Items>getCarsuppliesData(){
        String[] data={"pump,sparekey,carcover,windowsunshades" };
        return prepareItemsList(MyConstants.CAR_SUPPLIES_CAMEL_CASE,data);}

    public List<Items>getNeedsData(){
        String[] data={"Food,water,Currency,Idcards,waterbottle" };
        return prepareItemsList(MyConstants.NEEDS_CAMEL_CASE,data);}


    public List<Items>prepareItemsList(String category,String[]data){
        List<String> list= Arrays.asList(data);
        List<Items>dataList=new ArrayList<>();
        dataList.clear();
        for (int i=0;i<list.size();i++){
            dataList.add(new Items(list.get(i),category,false ));

        }
        return dataList;
    }
    public List<List<Items>> getAllData(){
        List<List<Items>> listofAllItems=new ArrayList<>();
        listofAllItems.clear();
        listofAllItems.add(getBasicData());
        listofAllItems.add(getClothingData());
        listofAllItems.add(getPersonalcareData());
        listofAllItems.add(getBabyNeedsData());
        listofAllItems.add(getHealthData());
        listofAllItems.add(getTechnologyData());
        listofAllItems.add(getFoodData());
        listofAllItems.add(getBeachsuppliesData());
        listofAllItems.add(getCarsuppliesData());
        listofAllItems.add(getNeedsData());
        return listofAllItems;

    }
    public void presistAllData(){
        List<List<Items>>listofAllItems=getAllData();
        for (List<Items>list:listofAllItems){
            for (Items items:list){
                database.mainDao().saveItem(items);
            }
        }
        System.out.println("Data added.");

    }

    public void persistDataByCategory(String category,Boolean onlyDelete){
        try {
            List<Items> list= deleteAndGetListByCategory(category,onlyDelete);
            if(!onlyDelete){
                for(Items item:list){
                    database.mainDao().saveItem(item);
                }
                Toast.makeText(context, category+"Reset Successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, category+"Reset Successfully.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

        }
    }

    private List<Items> deleteAndGetListByCategory(String category,Boolean onlyDelete){
        if(onlyDelete) {
            database.mainDao().deleteAllByCategoryAndAddedBy(category,MyConstants.SYSTEM_SMALL);
        }
        else{
            database.mainDao().deleteAllByCategory(category);
        }
        switch (category){
            case MyConstants.BASIC_NEEDS_CAMEL_CASE:
                return getBasicData();

            case MyConstants.CLOTHING_CAMEL_CASE:
                return getClothingData();

            case MyConstants.PERSONAL_CARE_CAMEL_CASE:
                return getPersonalcareData();

            case MyConstants.BABY_NEEDS_CAMEL_CASE:
                return getBabyNeedsData();

            case MyConstants.HEALTH_CAMEL_CASE:
                return getHealthData();

            case MyConstants.TECHNOLOGY_CAMEL_CASE:
                return getTechnologyData();

            case MyConstants.FOOD_CAMEL_CASE:
                return getFoodData();

            case MyConstants.BEACH_SUPPLIES_CAMEL_CASE:
                return getBeachsuppliesData();

            case MyConstants.CAR_SUPPLIES_CAMEL_CASE:
                return getCarsuppliesData();

            case MyConstants.NEEDS_CAMEL_CASE:
                return getNeedsData();

            default:
                return new ArrayList<>();


        }
    }

}
