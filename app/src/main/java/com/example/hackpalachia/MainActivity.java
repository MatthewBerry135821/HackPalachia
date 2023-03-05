package com.example.hackpalachia;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import static java.lang.Float.parseFloat;
import static java.lang.Math.sqrt;

import io.realm.OrderedCollectionChangeSet;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.realm.OrderedRealmCollectionChangeListener;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.sync.SyncConfiguration;

import java.util.ArrayList;
import java.util.Vector;

//import com.mongodb.realm.examples.model.Task;
//import com.mongodb.realm.examples.model.TaskStatus;
// Base Realm Packages
// Realm Authentication Packages
// MongoDB Service Packages
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.MongoCollection;
// Utility Packages


public class MainActivity extends AppCompatActivity {
    ListView listView;
    Realm uiThreadRealm;
    MongoCollection<Ewaste> ewasteMongoCollection;
    MongoCollection<LocationCoordinate> locationCoordinatemongoCollection;
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;
    ArrayList<String> arrayList;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this); // context, usually an Activity or Application

        app = new App(new AppConfiguration.Builder("hackapp-lahar").build());

        Credentials credentials = Credentials.anonymous();

        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                User user = app.currentUser();


                SyncConfiguration config = new SyncConfiguration.Builder(
                        user)
                        .allowQueriesOnUiThread(true)
                        .allowWritesOnUiThread(true)
                        .build();
                /*
                Realm.getInstanceAsync(config, new Realm.Callback() {
                    @Override
                    public void onSuccess(Realm realm) {
                        Log.v(
                                "EXAMPLE",
                                "Successfully opened a realm with reads and writes allowed on the UI thread."
                        );
                    }
                });

                uiThreadRealm = Realm.getInstance(config);

                addChangeListenerToRealm(uiThreadRealm);

                FutureTask<String> task = new FutureTask(new BackgroundQuickStart(app.currentUser()), "test");
                ExecutorService executorService = Executors.newFixedThreadPool(2);
                executorService.execute(task);
*/
            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });







        User user = app.currentUser();
        mongoClient =
                user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("LocationCoordinate");
// registry to handle POJOs (Plain Old Java Objects)
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        ewasteMongoCollection = mongoDatabase.getCollection("Ewaste", Ewaste.class).withCodecRegistry(pojoCodecRegistry);
        locationCoordinatemongoCollection = mongoDatabase.getCollection("LocationCoordinate", LocationCoordinate.class).withCodecRegistry(pojoCodecRegistry);








        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();

        arrayList.add("Luke");
        arrayList.add("Matthew");
        arrayList.add("Narayan");



        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        //listView.setAdapter(arrayAdapter);

        updateView();

        searchByType("Batteries");

    }
    private void updateView() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
    private void searchByType(String string){
        search("type", string);
    }
    private void searchByLocation(String string){
        searchByLocationAll("city" , string);
    }
    private void searchByLocationAll(String string1 ,String string){
        //Document queryFilter  = new Document("location", string);
        Document queryFilter  = new Document("city", string);

        locationCoordinatemongoCollection.findOne(queryFilter).getAsync(task -> {
            if (task.isSuccess()) {
                /*MongoCursor<Ewaste> results = task.get();

                Log.v("EXAMPLE", "successfully found all plants for Store 42:");
                while (results.hasNext()) {
                    Ewaste result = results.next();
                    Log.v("EXAMPLE", result.toString());
                    arrayList.add(result.getLocation());
                }
                 */
                LocationCoordinate result = task.get();
                showCentersByDistance(result.getLat(), result.getLng());
            } else {
                if(string1 == "city"){
                    searchByLocationAll("state_id" , string);
                }else if(string1 == "state_id"){
                    searchByLocationAll("state_name" , string);
                }else if(string1 == "state_name"){
                    searchByLocationAll("county_name" , string);
                }else if(string1 == "county_name"){
                    //searchByLocationAll("zips" , string);//contains multipole zips figure out later
                }
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });
    }
    private void showCentersByDistance(String lat, String lng) {
        float latF = parseFloat(lat);
        float lngF = parseFloat(lng);
        Document queryFilter  = new Document();

        RealmResultTask<MongoCursor<Ewaste>> findTask = ewasteMongoCollection.find().iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                Vector<Ewaste> centers = new Vector<Ewaste>(0);
                //Vector<int> centers = new Vector<Ewaste>(0);
                MongoCursor<Ewaste> results = task.get();

                Log.v("EXAMPLE", "successfully found all plants for Store 42:");
                while (results.hasNext()) {
                    Ewaste result = results.next();
                    centers.addElement(result);
                    //Log.v("EXAMPLE", result.toString());
                    //arrayList.add(result.getLocation());
                }
                int numDisplay = 5;
                int countDisplayed = 0;
                for(int i=0; i < centers.size() && countDisplayed < numDisplay;++i) {
                    float cLatF = parseFloat(centers.get(i).getLatitude());
                    float cLngF = parseFloat(centers.get(i).getLongitude());
                    double distance =  sqrt((latF - cLatF)*(latF - cLatF) + (lngF - cLngF)*(lngF - cLngF));
                    int smallest = 1;

                    for(int j=i; j < centers.size();++j) {
                        float cLatF2 = parseFloat(centers.get(i).getLatitude());
                        float cLngF2 = parseFloat(centers.get(i).getLongitude());
                        double distance2 =  sqrt((latF - cLatF)*(latF - cLatF) + (lngF - cLngF)*(lngF - cLngF));
                        if(distance2 < distance) {
                            smallest = 0;
                        }
                    }
                    if(smallest == 1) {
                        //arrayList.add(centers.get(i).getLandfillName() + distance.toString());
                        countDisplayed += 1;
                    }
                }
                updateView();
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });
    }
      /*  updateView();
    }*/
    private void search(String string, String string2){

        Document queryFilter  = new Document(string, string2);

        RealmResultTask<MongoCursor<Ewaste>> findTask = ewasteMongoCollection.find(queryFilter).iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Ewaste> results = task.get();

                Log.v("EXAMPLE", "successfully found all plants for Store 42:");
                while (results.hasNext()) {
                    Ewaste result = results.next();
                    Log.v("EXAMPLE", result.toString());
                    arrayList.add(result.getLandfillName());
                }
                updateView();
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });
    }

    private void addChangeListenerToRealm(Realm realm) {
        // all tasks in the realm
        RealmResults<Task> tasks = uiThreadRealm.where(Task.class).findAllAsync();

        tasks.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> collection, OrderedCollectionChangeSet changeSet) {
                // process deletions in reverse order if maintaining parallel data structures so indices don't change as you iterate
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (OrderedCollectionChangeSet.Range range : deletions) {
                    Log.v("QUICKSTART", "Deleted range: " + range.startIndex + " to " + (range.startIndex + range.length - 1));
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    Log.v("QUICKSTART", "Inserted range: " + range.startIndex + " to " + (range.startIndex + range.length - 1));                            }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    Log.v("QUICKSTART", "Updated range: " + range.startIndex + " to " + (range.startIndex + range.length - 1));                            }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // the ui thread realm uses asynchronous transactions, so we can only safely close the realm
        // when the activity ends and we can safely assume that those transactions have completed
        uiThreadRealm.close();
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully logged out.");
            } else {
                Log.e("QUICKSTART", "Failed to log out, error: " + result.getError());
            }
        });
    }

    public class BackgroundQuickStart implements Runnable {
        User user;

        public BackgroundQuickStart(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            String partitionValue = "My Project";
            SyncConfiguration config = new SyncConfiguration.Builder(
                    user,
                    partitionValue)
                    .build();

            Realm backgroundThreadRealm = Realm.getInstance(config);

            Task task = new Task("New Task");
            backgroundThreadRealm.executeTransaction (transactionRealm -> {
                transactionRealm.insert(task);
            });

            // all tasks in the realm
            RealmResults<Task> tasks = backgroundThreadRealm.where(Task.class).findAll();

            // you can also filter a collection
            RealmResults<Task> tasksThatBeginWithN = tasks.where().beginsWith("name", "N").findAll();
            RealmResults<Task> openTasks = tasks.where().equalTo("status", TaskStatus.Open.name()).findAll();

            Task otherTask = tasks.get(0);

            // all modifications to a realm must happen inside of a write block
            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                Task innerOtherTask = transactionRealm.where(Task.class).equalTo("_id", otherTask.get_id()).findFirst();
                innerOtherTask.setStatus(TaskStatus.Complete);
            });

            Task yetAnotherTask = tasks.get(0);
            ObjectId yetAnotherTaskId = yetAnotherTask.get_id();
            // all modifications to a realm must happen inside of a write block
            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                Task innerYetAnotherTask = transactionRealm.where(Task.class).equalTo("_id", yetAnotherTaskId).findFirst();
                innerYetAnotherTask.deleteFromRealm();
            });

            // because this background thread uses synchronous realm transactions, at this point all
            // transactions have completed and we can safely close the realm
            backgroundThreadRealm.close();
        }
    }
}
