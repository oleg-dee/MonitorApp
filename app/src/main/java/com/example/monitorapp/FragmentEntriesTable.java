package com.example.monitorapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FragmentEntriesTable extends Fragment {

    private final int pageSize = 12;
    private int itemsLoaded = 0;
    private List<String> itemsToShow = new ArrayList<>();


    public MyRecyclerViewAdapter loadMore(RecyclerView recyclerView) {
        //trying to get few recent entries data
        EntryRepository entryRepository = new EntryRepository(getContext());
        List<Entry> list = new ArrayList<>();
        try {
            list = entryRepository.getRecent(pageSize, itemsLoaded);
            itemsLoaded += list.size();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (list.isEmpty() && itemsLoaded > 0) {
            return (MyRecyclerViewAdapter) recyclerView.getAdapter();
        }

        Exporter exporter = new Exporter(getContext());
        List<String> rowList;
        for (Entry entry : list) {
            rowList = exporter.buildRowList(entry, true);
            StringBuilder sb = new StringBuilder();
            for (String row : rowList) {
                sb.append(row);
                sb.append("  ");
            }

            itemsToShow.add(sb.toString());
        }

        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getContext(), itemsToShow);

        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
                //TODO rewrite adapter to hold UIDs separately
                int uid = Integer.parseInt(adapter.getItem(position).split("DETAILS")[0].split(":")[1].trim());
                try {
                    ((MainActivity) getActivity()).loadEntry(uid);
                } catch (ExecutionException | InterruptedException | IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        });

        recyclerView.setAdapter(adapter);

        System.out.println("Items loaded: " + itemsLoaded);

        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        System.out.println("FragmentEntriesTable -- onCreateView");
        itemsLoaded = 0;
        itemsToShow.clear();
        return inflater.inflate(R.layout.fragment_entries_table, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        System.out.println("FragmentEntriesTable -- onViewCreated");

        RecyclerView recyclerView = getView().findViewById(R.id.rvEntries);
        int numberOfColumns = 1;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getContext(), itemsToShow);

        recyclerView.setAdapter(adapter);

        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position to right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int uid = Integer.parseInt(adapter.getItem(viewHolder.getAdapterPosition()).split("DETAILS")[0].split(":")[1].trim());
                EntryRepository entryRepository = new EntryRepository(getContext());
                try {
                    Entry entry = entryRepository.getById(uid);
                    entryRepository.delete(entry);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                itemsLoaded = 0;
                itemsToShow.clear();
                RecyclerView recyclerView = getView().findViewById(R.id.rvEntries);
                loadMore(recyclerView);
                System.out.println("Entry with uid " + uid + " is deleted.");
//                // this method is called when we swipe our item to right direction.
//                // on below line we are getting the item at a particular position.
//                RecyclerData deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());
//
//                // below line is to get the position
//                // of the item at that position.
//                int position = viewHolder.getAdapterPosition();
//
//                // this method is called when item is swiped.
//                // below line is to remove item from our array list.
//                recyclerDataArrayList.remove(viewHolder.getAdapterPosition());
//
//                // below line is to notify our item is removed from adapter.
//                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//
//                // below line is to display our snackbar with action.
//                Snackbar.make(recyclerView, deletedCourse.getTitle(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // adding on click listener to our action of snack bar.
//                        // below line is to add our item to array list with a position.
//                        recyclerDataArrayList.add(position, deletedCourse);
//
//                        // below line is to notify item is
//                        // added to our adapter class.
//                        recyclerViewAdapter.notifyItemInserted(position);
//                    }
//                }).show();
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == itemsToShow.size() - 1) {
                    //bottom of the list!
                    loadMore(recyclerView);
                }
            }


        });

    }

    @Override
    public void onResume() {
        System.out.println("RecyclerView resumed!");
        itemsLoaded = 0;
        itemsToShow.clear();
        RecyclerView recyclerView = getView().findViewById(R.id.rvEntries);
        loadMore(recyclerView);
        super.onResume();
    }
}