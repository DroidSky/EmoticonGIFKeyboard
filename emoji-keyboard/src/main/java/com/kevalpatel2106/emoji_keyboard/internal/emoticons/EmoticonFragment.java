package com.kevalpatel2106.emoji_keyboard.internal.emoticons;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kevalpatel2106.emoji_keyboard.EmoticonSelectListener;
import com.kevalpatel2106.emoji_keyboard.R;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Cars;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Electr;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Emoticon;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Food;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Nature;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.People;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Sport;
import com.kevalpatel2106.emoji_keyboard.internal.emoticons.emoji.Symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmoticonFragment extends Fragment {
    private Context mContext;

    //Array list to hold currently displaying emoticons list
    private List<Emoticon> mEmoticons;

    //Adapter to display emoticon grids.
    private EmoticonGridAdapter mEmoticonGridAdapter;

    //Listener to notify when emoticons selected.
    private EmoticonSelectListener mEmoticonSelectListener;

    //Recently used emoticons.
    private EmoticonRecentManager mEmoticonRecentManager;

    //set the emoticon click listener
    private AdapterView.OnItemClickListener mOnEmoticonSelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Emoticon emoticonSelected = mEmoticonGridAdapter.getItem(position);
            if (emoticonSelected == null) return;

            //Notify the emoticon
            if (mEmoticonSelectListener != null)
                mEmoticonSelectListener.emoticonSelected(emoticonSelected);

            //Save the emoticon to the recent list
            mEmoticonRecentManager.add(emoticonSelected);
        }
    };

    public EmoticonFragment() {
        // Required empty public constructor
    }

    public static EmoticonFragment getNewInstance() {
        return new EmoticonFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmoticonRecentManager = EmoticonRecentManager.getInstance(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emoticon, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set the grid view
        mEmoticons = new ArrayList<>();
        mEmoticons.addAll(getEmoticonsList(mEmoticonRecentManager.getRecentPage()));
        mEmoticonGridAdapter = new EmoticonGridAdapter(mContext, mEmoticons, false);
        GridView gridView = view.findViewById(R.id.emoji_gridView);
        gridView.setAdapter(mEmoticonGridAdapter);
        gridView.setOnItemClickListener(mOnEmoticonSelectedListener);

        //Set headers
        setTabHeaders(view);

        //Set back space key
        setBackSpace(view);
    }

    /**
     * Set the tab headers with categories of emoticons and back space button.
     *
     * @param view Root view.
     */
    private void setTabHeaders(@NonNull View view) {
        final View[] emojiTabs = new View[8];
        emojiTabs[EmoticonsCategories.RECENT] = view.findViewById(R.id.emojis_tab_0_recents);
        emojiTabs[EmoticonsCategories.PEOPLE] = view.findViewById(R.id.emojis_tab_1_people);
        emojiTabs[EmoticonsCategories.NATURE] = view.findViewById(R.id.emojis_tab_2_nature);
        emojiTabs[EmoticonsCategories.FOOD] = view.findViewById(R.id.emojis_tab_3_food);
        emojiTabs[EmoticonsCategories.SPORT] = view.findViewById(R.id.emojis_tab_4_sport);
        emojiTabs[EmoticonsCategories.CARS] = view.findViewById(R.id.emojis_tab_5_cars);
        emojiTabs[EmoticonsCategories.ELECTRIC] = view.findViewById(R.id.emojis_tab_6_elec);
        emojiTabs[EmoticonsCategories.SYMBOLS] = view.findViewById(R.id.emojis_tab_7_sym);

        //Set the click listener in each tab
        for (int i = 0; i < emojiTabs.length; i++) {
            final int position = i;
            emojiTabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Mark current tab as selected.
                    for (View emojiTab : emojiTabs) emojiTab.setSelected(false);
                    v.setSelected(true);

                    //Update the grid with emoticons for that category
                    mEmoticons.clear();
                    mEmoticons.addAll(getEmoticonsList(position));
                    mEmoticonGridAdapter.notifyDataSetChanged();

                    //Save the selected category
                    //noinspection WrongConstant
                    mEmoticonRecentManager.setRecentPage(position);
                }
            });
        }

        //Select recent tabs selected while creating new instance
        emojiTabs[mEmoticonRecentManager.getRecentPage()].setSelected(true);
    }

    private void setBackSpace(@NonNull View view) {
        view.findViewById(R.id.emojis_backspace)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mEmoticonSelectListener != null) mEmoticonSelectListener.onBackSpace();
                    }
                });
    }

    /**
     * Set the {@link EmoticonSelectListener} to get notify whenever the emoticon is selected or deleted.
     *
     * @param emoticonSelectListener {@link EmoticonSelectListener}
     */
    @SuppressWarnings("ConstantConditions")
    public void setEmoticonSelectListener(@NonNull EmoticonSelectListener emoticonSelectListener) {
        if (emoticonSelectListener == null)
            throw new IllegalArgumentException("EmoticonSelectListener cannot be null.");
        mEmoticonSelectListener = emoticonSelectListener;
    }


    private List<Emoticon> getEmoticonsList(int position) {
        switch (position) {
            case EmoticonsCategories.RECENT:
                return mEmoticonRecentManager.getRecentEmoticons();
            case EmoticonsCategories.PEOPLE:
                return Arrays.asList(People.DATA);
            case EmoticonsCategories.NATURE:
                return Arrays.asList(Nature.DATA);
            case EmoticonsCategories.FOOD:
                return Arrays.asList(Food.DATA);
            case EmoticonsCategories.SPORT:
                return Arrays.asList(Sport.DATA);
            case EmoticonsCategories.CARS:
                return Arrays.asList(Cars.DATA);
            case EmoticonsCategories.ELECTRIC:
                return Arrays.asList(Electr.DATA);
            case EmoticonsCategories.SYMBOLS:
                return Arrays.asList(Symbols.DATA);
            default:
                throw new IllegalStateException("Invalid position.");
        }
    }
}